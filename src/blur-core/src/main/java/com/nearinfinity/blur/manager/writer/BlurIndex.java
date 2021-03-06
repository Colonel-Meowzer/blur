package com.nearinfinity.blur.manager.writer;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.lucene.index.IndexReader;

import com.nearinfinity.blur.thrift.generated.Row;

public abstract class BlurIndex {

  public abstract void replaceRow(boolean waitToBeVisible, boolean wal, Row row) throws IOException;
  
  public abstract void deleteRow(boolean waitToBeVisible, boolean wal, String rowId) throws IOException;

  public abstract IndexReader getIndexReader() throws IOException;

  public abstract void close() throws IOException;

  public abstract void refresh() throws IOException;

  public abstract AtomicBoolean isClosed();

  public abstract void optimize(int numberOfSegmentsPerShard) throws IOException;

}
