package com.nearinfinity.blur.zookeeper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import com.nearinfinity.blur.manager.DirectoryManagerStore;
import com.nearinfinity.blur.utils.BlurConfiguration;
import com.nearinfinity.blur.utils.BlurConstants;
import com.nearinfinity.blur.utils.ZkUtils;

public class ZookeeperDirectoryManagerStore implements DirectoryManagerStore, BlurConstants {
	
	private static final Log LOG = LogFactory.getLog(ZookeeperDirectoryManagerStore.class);
	private static final ArrayList<ACL> ACL = Ids.OPEN_ACL_UNSAFE;
	private static final String TABLE_LOCKS = "tableLocks";
	private static final String TABLES = "tables";
	private static final String UTF_8 = "UTF-8";
	private BlurConfiguration configuration = new BlurConfiguration();
	private String uuid = configuration.getNodeUuid();
	private ZooKeeper zk;
	private final String blurZookeeperPath = configuration.get(BLUR_ZOOKEEPER_PATH);
	private Map<String, Set<String>> currentlyServing = new HashMap<String, Set<String>>();
	
	public ZookeeperDirectoryManagerStore() {
		try {
			zk = ZooKeeperFactory.getZooKeeper();
			ZkUtils.mkNodes(getPath(blurZookeeperPath,TABLE_LOCKS), zk);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Map<String, Set<String>> getShardIdsToServe(String nodeId, int maxToServePerCall) {
		int count = 0;
		Set<String> tables = getTables();
		for (String table : tables) {
			Set<String> shards = currentlyServing.get(table);
			if (shards == null) {
				shards = new TreeSet<String>();
				currentlyServing.put(table, shards);
			}
			Set<String> shardIds = getShardIds(table);
			for (String shardId : shardIds) {
				if (!isThisNodeServing(table,shardId)) {
					if (obtainLock(table,shardId)) {
						shards.add(shardId);
						count++;
						if (count >= maxToServePerCall) {
							return currentlyServing;
						}
					}
				}
			}
		}
		return currentlyServing;
	}
	
	@Override
	public boolean obtainLock(String table, String shardId) {
		try {
			ZkUtils.mkNodes(getPath(blurZookeeperPath,TABLE_LOCKS,table), zk);
			String path = getShardIdLockPath(table, shardId);
			try {
				zk.create(path, toBytes(uuid), ACL, CreateMode.EPHEMERAL);
				return true;
			} catch (Exception e) {
				LOG.info("Cannot obtain lock for table [" + table + "] with shard [" + shardId + "]");
			}
			return false;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	
	private byte[] toBytes(String str) {
		try {
			return str.getBytes(UTF_8);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private String getShardIdLockPath(String table, String shardId) {
		return getPath(blurZookeeperPath,TABLE_LOCKS,table,shardId);
	}

	@Override
	public boolean isThisNodeServing(String table, String shardId) {
		Set<String> shards = currentlyServing.get(table);
		if (shards == null) {
			return false;
		}
		return shards.contains(shardId);
	}

	@Override
	public URI getDirectoryURIToServe(String table, String shardId) {
		try {
			String path = getShardIdPath(table,shardId);
			Stat stat = zk.exists(path, false);
			if (stat == null) {
				return null;
			} else {
				byte[] data = zk.getData(path, false, stat);
				return new URI(new String(data));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void addDirectoryURIToServe(String table, String shardId, URI dirUri) {
		try {
			mkTable(table);
			String path = getShardIdPath(table,shardId);
			byte[] data = toBytes(dirUri);
			Stat stat = zk.exists(path, false);
			if (stat == null) {
				zk.create(path, data, ACL, CreateMode.PERSISTENT);
			} else {
				zk.setData(path, data, stat.getVersion());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	@Override
	public Set<String> getShardIds(String table) {
		try {
			String path = getTablePath(table);
			Stat stat = zk.exists(path, false);
			if (stat == null) {
				return null;
			}
		 	List<String> list = zk.getChildren(path, false);
			return new TreeSet<String>(list);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Set<String> getTables() {
		try {
			String path = getPath(blurZookeeperPath,TABLES);
			Stat stat = zk.exists(path, false);
			if (stat == null) {
				return null;
			}
		 	List<String> list = zk.getChildren(path, false);
			return new TreeSet<String>(list);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void removeTable(String table) {
		try {
			for (String shardId : getShardIds(table)) {
				removeDirectoryURIToServe(table, shardId);
			}
			String path = getTablePath(table);
			Stat stat = zk.exists(path, false);
			if (stat != null) {
				zk.delete(path, stat.getVersion());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void removeDirectoryURIToServe(String table, String shardId) {
		try {
			String shardIdPath = getShardIdPath(table, shardId);
			Stat stat = zk.exists(shardIdPath, false);
			if (stat != null) {
				zk.delete(shardIdPath, stat.getVersion());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void mkTable(String table) throws KeeperException, InterruptedException, IOException {
		String path = getTablePath(table);
		ZkUtils.mkNodes(path, zk);
	}

	private byte[] toBytes(URI dirUri) {
		try {
			return dirUri.toString().getBytes(UTF_8);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	private String getTablePath(String table) {
		return getPath(blurZookeeperPath,TABLES,table);
	}

	private String getShardIdPath(String table, String shardId) {
		return getPath(blurZookeeperPath,TABLES,table,shardId);
	}

	private static String getPath(String... parts) {
		if (parts == null || parts.length == 0) {
			return null;
		}
		StringBuilder builder = new StringBuilder(parts[0]);
		for (int i = 1; i < parts.length; i++) {
			builder.append('/');
			builder.append(parts[i]);
		}
		return builder.toString();
	}






}