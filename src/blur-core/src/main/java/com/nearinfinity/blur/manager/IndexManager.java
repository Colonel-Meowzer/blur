package com.nearinfinity.blur.manager;

import static com.nearinfinity.blur.utils.RowSuperDocumentUtil.createSuperDocument;
import static com.nearinfinity.blur.utils.RowSuperDocumentUtil.getRow;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexDeletionPolicy;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

import com.nearinfinity.blur.analysis.BlurAnalyzer;
import com.nearinfinity.blur.lucene.index.SuperDocument;
import com.nearinfinity.blur.lucene.search.BlurSearcher;
import com.nearinfinity.blur.lucene.search.FairSimilarity;
import com.nearinfinity.blur.lucene.search.SuperParser;
import com.nearinfinity.blur.manager.hits.HitsIterable;
import com.nearinfinity.blur.manager.hits.HitsIterableSearcher;
import com.nearinfinity.blur.manager.hits.MergerHitsIterable;
import com.nearinfinity.blur.thrift.generated.BlurException;
import com.nearinfinity.blur.thrift.generated.MissingShardException;
import com.nearinfinity.blur.thrift.generated.Row;
import com.nearinfinity.blur.thrift.generated.ScoreType;
import com.nearinfinity.blur.thrift.generated.Selector;
import com.nearinfinity.blur.utils.ForkJoin;
import com.nearinfinity.blur.utils.PrimeDocCache;
import com.nearinfinity.blur.utils.TermDocIterable;
import com.nearinfinity.blur.utils.ForkJoin.ParallelCall;
import com.nearinfinity.mele.Mele;

public class IndexManager {

	private static final Version LUCENE_VERSION = Version.LUCENE_30;
    private static final Log LOG = LogFactory.getLog(IndexManager.class);
	private static final long POLL_TIME = 30000;
	private static final TableManager ALWAYS_ON = new TableManager() {
		@Override
		public boolean isTableEnabled(String table) {
			return true;
		}

		@Override
		public String getAnalyzerDefinitions(String table) {
			return "";
		}
	};
	
	private Mele mele;
	private Map<String, Map<String, IndexWriter>> indexWriters = new ConcurrentHashMap<String, Map<String, IndexWriter>>();
	private Map<String, Analyzer> analyzers = new ConcurrentHashMap<String, Analyzer>();
	private PartitionerManager partitionerManager;
	private Similarity similarity = new FairSimilarity();
	private ExecutorService executor = Executors.newCachedThreadPool();
	private TableManager manager;
	private Thread daemonUnservedShards;
	private Thread daemonCommitDaemon;
	
	public interface TableManager {
		boolean isTableEnabled(String table);
		String getAnalyzerDefinitions(String table);
	}
	
	public IndexManager(Mele mele, TableManager manager) throws IOException, BlurException {
	    this.mele = mele;
		this.manager = manager;
		this.partitionerManager = new PartitionerManager(mele);
		setupIndexManager();
		Runtime.getRuntime().addShutdownHook(new ShutdownThread(this));
	}
	
	public IndexManager(Mele mele) throws IOException, BlurException {
		this(mele,ALWAYS_ON);
	}

	public Map<String, IndexReader> getIndexReaders(String table)
			throws IOException {
		Map<String, IndexWriter> map = indexWriters.get(table);
		Map<String, IndexReader> reader = new HashMap<String, IndexReader>();
		for (Entry<String, IndexWriter> writer : map.entrySet()) {
			reader.put(writer.getKey(), writer.getValue().getReader());
		}
		return reader;
	}

	public static void replace(IndexWriter indexWriter, Row row) throws IOException {
		replace(indexWriter,createSuperDocument(row));
	}
	
	public static void replace(IndexWriter indexWriter, SuperDocument document) throws IOException {
		synchronized (indexWriter) {
			indexWriter.deleteDocuments(new Term(SuperDocument.ID, document.getId()));
			if (!replaceInternal(indexWriter,document)) {
				indexWriter.deleteDocuments(new Term(SuperDocument.ID, document.getId()));
				if (!replaceInternal(indexWriter,document)) {
					throw new IOException("SuperDocument too large, try increasing ram buffer size.");
				}
			}
		}
	}
	
	public void close() throws InterruptedException {
	    if (daemonCommitDaemon != null) {
    	    daemonCommitDaemon.interrupt();
    	    daemonCommitDaemon.join();
	    }
	    if (daemonUnservedShards != null) {
    	    daemonUnservedShards.interrupt();
    	    daemonUnservedShards.join();
	    }
		for (Entry<String, Map<String, IndexWriter>> writers : indexWriters.entrySet()) {
			for (Entry<String, IndexWriter> writer : writers.getValue().entrySet()) {
				try {
					writer.getValue().close();
				} catch (IOException e) {
					LOG.error("Erroring trying to close [" + writers.getKey()
							+ "] [" + writer.getKey() + "]", e);
				}
			}
		}
	}

	public void replaceRow(String table, Row row) throws BlurException, MissingShardException {
	    try {
    		IndexWriter indexWriter = getIndexWriter(table, row.id);
    		checkIfShardIsNull(indexWriter);
    		try {
    			replace(indexWriter,row);
    		} catch (IOException e) {
    			LOG.error("Unknown error",e);
    			throw new BlurException("Unknown error [" + e.getMessage() + "]");
    		}
	    } catch (Exception e) {
	        LOG.error("Unknown error",e);
            throw new BlurException("Unknown error [" + e.getMessage() + "]");
        }
	}

	public void removeRow(String table, String id) throws BlurException {
		//@todo finish
	}

	public Row fetchRow(String table, Selector selector) throws BlurException, MissingShardException {
		try {
			IndexReader reader = getIndexReader(table,selector.id);
			checkIfShardIsNull(reader);
			return getRow(selector, getDocs(reader,selector.id));
		} catch (Exception e) {
			LOG.error("Unknown error while trying to fetch row.",e);
			throw new BlurException(e.getMessage());
		}
	}
	
	public HitsIterable search(final String table, String query, final boolean superQueryOn,
			ScoreType type, String postSuperFilter, String preSuperFilter,
			long minimumNumberOfHits, long maxQueryTime) throws Exception {
		Map<String, IndexReader> indexReaders;
		try {
			indexReaders = getIndexReaders(table);
		} catch (IOException e) {
			LOG.error("Unknown error while trying to fetch index readers.",e);
			throw new BlurException(e.getMessage());
		}
		Filter preFilter = parseFilter(table, preSuperFilter, false, type);
		Filter postFilter = parseFilter(table, postSuperFilter, true, type);
		final Query userQuery = parseQuery(query,superQueryOn, getAnalyzer(table), postFilter, preFilter, type);
		return ForkJoin.execute(executor, indexReaders.entrySet(), new ParallelCall<Entry<String, IndexReader>, HitsIterable>() {
			@Override
			public HitsIterable call(Entry<String, IndexReader> entry) throws Exception {
			    IndexReader reader = entry.getValue();
		        String shard = entry.getKey();
                BlurSearcher searcher = new BlurSearcher(reader, PrimeDocCache.getTableCache().getShardCache(table).getIndexReaderCache(shard));
		        searcher.setSimilarity(similarity);
			    return new HitsIterableSearcher((Query) userQuery.clone(), table, shard, searcher);
			}
		}).merge(new MergerHitsIterable(minimumNumberOfHits,maxQueryTime));
	}

	private Filter parseFilter(String table, String filter, boolean superQueryOn, ScoreType scoreType) throws ParseException, BlurException {
		if (filter == null) {
			return null;
		}
		return new QueryWrapperFilter(new SuperParser(LUCENE_VERSION, getAnalyzer(table), superQueryOn, null, scoreType).parse(filter));
	}

	private Analyzer getAnalyzer(String table) throws BlurException {
		Analyzer analyzer = analyzers.get(table);
		if (analyzer != null) {
			return analyzer;
		}
		try {
			BlurAnalyzer blurAnalyzer = BlurAnalyzer.create(manager.getAnalyzerDefinitions(table));
			analyzers.put(table, blurAnalyzer);
			return blurAnalyzer;
		} catch (Exception e) {
			LOG.error("unknown error", e);
			throw new BlurException(e.getMessage());
		}
	}

	private Query parseQuery(String query, boolean superQueryOn, Analyzer analyzer, Filter postFilter, Filter preFilter, ScoreType scoreType) throws ParseException {
		Query result = new SuperParser(LUCENE_VERSION, analyzer, superQueryOn, preFilter, scoreType).parse(query);
		if (postFilter == null) {
			return result;	
		}
		return new FilteredQuery(result, postFilter);
	}
	
	private Iterable<Document> getDocs(final IndexReader reader, String id) throws IOException {
		TermDocs termDocs = reader.termDocs(new Term(SuperDocument.ID,id));
		return new TermDocIterable(termDocs,reader);
	}

	private void setupIndexManager() throws IOException, BlurException {
		List<String> listClusters = mele.listClusters();
		for (String cluster : listClusters) {
			if (!manager.isTableEnabled(cluster)) {
				ensureClosed(cluster);
				continue;
			}
			Map<String, IndexWriter> map = indexWriters.get(cluster);
			if (map == null) {
				map = new ConcurrentHashMap<String, IndexWriter>();
				indexWriters.put(cluster, map);
			}
			List<String> localDirectories = mele.listLocalDirectories(cluster);
			openForWriting(cluster, localDirectories, map);
		}
		
		// @todo once all are brought online, look for shards that are not
		// online yet...
		serverUnservedShards();
		startDaemonUnservedShards();
		startDaemonWriterCommit();
	}
	
	private void startDaemonWriterCommit() {
		daemonCommitDaemon = new Thread(new Runnable() {
			@Override
			public void run() {
				while (sleep(POLL_TIME)) {
					commitIndexWriters();
				}
			}
		});
		daemonCommitDaemon.setDaemon(true);
		daemonCommitDaemon.setName("IndexManager-Commit-Daemon");
		daemonCommitDaemon.start();
	}
	
	private void commitIndexWriters() {
		for (Entry<String, Map<String, IndexWriter>> tableEntry : indexWriters.entrySet()) {
			String table = tableEntry.getKey();
			for (Entry<String, IndexWriter> shardEntry : tableEntry.getValue().entrySet()) {
				String shard = shardEntry.getKey();
				IndexWriter indexWriter = shardEntry.getValue();
				try {
                    indexWriter.commit();
                } catch (IOException e) {
                    LOG.error("Unknown error while commiting shard [" + shard + "] in table [" + table + "]",e);
                }
			}
		}
	}

	private void startDaemonUnservedShards() {
		daemonUnservedShards = new Thread(new Runnable() {
			@Override
			public void run() {
				while (sleep(POLL_TIME)) {
					serverUnservedShards();
				}
			}
		});
		daemonUnservedShards.setDaemon(true);
		daemonUnservedShards.setName("IndexManager-Unserved-Shard-Daemon");
		daemonUnservedShards.start();
	}

	private void serverUnservedShards() {
		try {
			for (String cluster : mele.listClusters()) {
				if (!manager.isTableEnabled(cluster)) {
					ensureClosed(cluster);
					continue;
				}
				Map<String, IndexWriter> map = indexWriters.get(cluster);
				if (map == null) {
					map = new ConcurrentHashMap<String, IndexWriter>();
					indexWriters.put(cluster, map);
				}
				List<String> listDirectories = mele.listDirectories(cluster);
				openForWriting(cluster, listDirectories, map);
			}
		} catch (Exception e) {
			LOG.error("Unknown error",e);
		}
	}

	private void ensureClosed(String table) {
		Map<String, IndexWriter> writers = indexWriters.get(table);
		ensureClosed(writers);
	}

	private void ensureClosed(Map<String, IndexWriter> writers) {
	    if (writers == null) {
	        return;
	    }
		for (IndexWriter writer : writers.values()) {
			ensureClosed(writer);
		}
	}

	private void ensureClosed(IndexWriter writer) {
		try {
			writer.close();
		} catch (IOException e) {
			LOG.error("Uknown lucene error",e);
		}
	}

	private void openForWriting(String table, List<String> dirs, Map<String, IndexWriter> writersMap) throws IOException, BlurException {
		for (String shard : dirs) {
			// @todo get zk lock here....??? maybe if the index writer cannot
			// get a lock that is good enough???
			// also maybe send this into a loop so that if a node goes down it
			// will picks the pieces...
			// need to think about what happens when a node comes back online.
			Directory directory = mele.open(table, shard);
			IndexDeletionPolicy deletionPolicy = mele.getIndexDeletionPolicy(table, shard);
			Analyzer analyzer = getAnalyzer(table);
			try {
				if (!IndexWriter.isLocked(directory)) {
					IndexWriter indexWriter = new IndexWriter(directory, analyzer, deletionPolicy, MaxFieldLength.UNLIMITED);
					LOG.info("Opening Table [" + table + "] shard [" + shard + "] for writing.");
					indexWriter.setSimilarity(similarity);
					writersMap.put(shard, indexWriter);
				}
			} catch (LockObtainFailedException e) {
				LOG.debug("Table [" + table + "] shard [" + shard + "] is locked by another shard.");
			}
		}
	}
	
	private IndexWriter getIndexWriter(String table, String id) throws BlurException {
		Partitioner partitioner = partitionerManager.getPartitioner(table);
		if (id == null) {
			throw new BlurException("null mutation id");
		}
		String shard = partitioner.getShard(id);
		Map<String, IndexWriter> tableWriters = indexWriters.get(table);
		if (tableWriters == null) {
			LOG.error("Table [" + table + "] not online in this server.");
			throw new BlurException("Table [" + table
					+ "] not online in this server.");
		}
		IndexWriter indexWriter = tableWriters.get(shard);
		if (indexWriter == null) {
			LOG.error("Shard [" + shard + "] from table [" + table
					+ "] not online in this server.");
			return null;
		}
		return indexWriter;
	}

	private static boolean replaceInternal(IndexWriter indexWriter, SuperDocument document) throws IOException {
		long oldRamSize = indexWriter.ramSizeInBytes();
		for (Document doc : document.getAllDocumentsForIndexing()) {
			long newRamSize = indexWriter.ramSizeInBytes();
			if (newRamSize < oldRamSize) {
				LOG.info("Flush occur during writing of super document, start over.");
				return false;
			}
			oldRamSize = newRamSize;
			indexWriter.addDocument(doc);
		}
		return true;
	}

	private IndexReader getIndexReader(String table, String id) throws BlurException {
		IndexWriter indexWriter = getIndexWriter(table, id);
		if (indexWriter == null) {
			return null;
		}
		try {
			return indexWriter.getReader();
		} catch (IOException e) {
			LOG.error("Error trying to open reader on writer.",e);
			throw new BlurException("Error trying to open reader on writer.");
		}
	}

	public static class ShutdownThread extends Thread {
		private IndexManager indexManager;
		public ShutdownThread(IndexManager indexManager) {
			this.indexManager = indexManager;
		}
		@Override
		public void run() {
			try {
			    System.out.println("Closing index manager.");
                indexManager.close();
            } catch (InterruptedException e) {
                LOG.error("Error while closing index manager.",e);
            }
		}
	}
	
	public static boolean sleep(long pauseTime) {
		try {
			Thread.sleep(pauseTime);
			return true;
		} catch (InterruptedException e) {
			return false;
		}
	}
	
	private void checkIfShardIsNull(Object shard) throws MissingShardException {
		if (shard == null) {
			throw new MissingShardException();
		}
	}
}