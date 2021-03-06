/**
 * Autogenerated by Thrift Compiler (0.7.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 */
package com.nearinfinity.blur.thrift.generated;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
public class TableStats implements org.apache.thrift.TBase<TableStats, TableStats._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TableStats");

  private static final org.apache.thrift.protocol.TField TABLE_NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("tableName", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField BYTES_FIELD_DESC = new org.apache.thrift.protocol.TField("bytes", org.apache.thrift.protocol.TType.I64, (short)2);
  private static final org.apache.thrift.protocol.TField RECORD_COUNT_FIELD_DESC = new org.apache.thrift.protocol.TField("recordCount", org.apache.thrift.protocol.TType.I64, (short)3);
  private static final org.apache.thrift.protocol.TField ROW_COUNT_FIELD_DESC = new org.apache.thrift.protocol.TField("rowCount", org.apache.thrift.protocol.TType.I64, (short)4);
  private static final org.apache.thrift.protocol.TField QUERIES_FIELD_DESC = new org.apache.thrift.protocol.TField("queries", org.apache.thrift.protocol.TType.I64, (short)5);

  /**
   * 
   */
  public String tableName; // required
  /**
   * 
   */
  public long bytes; // required
  /**
   * 
   */
  public long recordCount; // required
  /**
   * 
   */
  public long rowCount; // required
  /**
   * 
   */
  public long queries; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    /**
     * 
     */
    TABLE_NAME((short)1, "tableName"),
    /**
     * 
     */
    BYTES((short)2, "bytes"),
    /**
     * 
     */
    RECORD_COUNT((short)3, "recordCount"),
    /**
     * 
     */
    ROW_COUNT((short)4, "rowCount"),
    /**
     * 
     */
    QUERIES((short)5, "queries");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // TABLE_NAME
          return TABLE_NAME;
        case 2: // BYTES
          return BYTES;
        case 3: // RECORD_COUNT
          return RECORD_COUNT;
        case 4: // ROW_COUNT
          return ROW_COUNT;
        case 5: // QUERIES
          return QUERIES;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __BYTES_ISSET_ID = 0;
  private static final int __RECORDCOUNT_ISSET_ID = 1;
  private static final int __ROWCOUNT_ISSET_ID = 2;
  private static final int __QUERIES_ISSET_ID = 3;
  private BitSet __isset_bit_vector = new BitSet(4);

  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.TABLE_NAME, new org.apache.thrift.meta_data.FieldMetaData("tableName", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.BYTES, new org.apache.thrift.meta_data.FieldMetaData("bytes", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.RECORD_COUNT, new org.apache.thrift.meta_data.FieldMetaData("recordCount", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.ROW_COUNT, new org.apache.thrift.meta_data.FieldMetaData("rowCount", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.QUERIES, new org.apache.thrift.meta_data.FieldMetaData("queries", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TableStats.class, metaDataMap);
  }

  public TableStats() {
  }

  public TableStats(
    String tableName,
    long bytes,
    long recordCount,
    long rowCount,
    long queries)
  {
    this();
    this.tableName = tableName;
    this.bytes = bytes;
    setBytesIsSet(true);
    this.recordCount = recordCount;
    setRecordCountIsSet(true);
    this.rowCount = rowCount;
    setRowCountIsSet(true);
    this.queries = queries;
    setQueriesIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TableStats(TableStats other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    if (other.isSetTableName()) {
      this.tableName = other.tableName;
    }
    this.bytes = other.bytes;
    this.recordCount = other.recordCount;
    this.rowCount = other.rowCount;
    this.queries = other.queries;
  }

  public TableStats deepCopy() {
    return new TableStats(this);
  }

  @Override
  public void clear() {
    this.tableName = null;
    setBytesIsSet(false);
    this.bytes = 0;
    setRecordCountIsSet(false);
    this.recordCount = 0;
    setRowCountIsSet(false);
    this.rowCount = 0;
    setQueriesIsSet(false);
    this.queries = 0;
  }

  /**
   * 
   */
  public String getTableName() {
    return this.tableName;
  }

  /**
   * 
   */
  public TableStats setTableName(String tableName) {
    this.tableName = tableName;
    return this;
  }

  public void unsetTableName() {
    this.tableName = null;
  }

  /** Returns true if field tableName is set (has been assigned a value) and false otherwise */
  public boolean isSetTableName() {
    return this.tableName != null;
  }

  public void setTableNameIsSet(boolean value) {
    if (!value) {
      this.tableName = null;
    }
  }

  /**
   * 
   */
  public long getBytes() {
    return this.bytes;
  }

  /**
   * 
   */
  public TableStats setBytes(long bytes) {
    this.bytes = bytes;
    setBytesIsSet(true);
    return this;
  }

  public void unsetBytes() {
    __isset_bit_vector.clear(__BYTES_ISSET_ID);
  }

  /** Returns true if field bytes is set (has been assigned a value) and false otherwise */
  public boolean isSetBytes() {
    return __isset_bit_vector.get(__BYTES_ISSET_ID);
  }

  public void setBytesIsSet(boolean value) {
    __isset_bit_vector.set(__BYTES_ISSET_ID, value);
  }

  /**
   * 
   */
  public long getRecordCount() {
    return this.recordCount;
  }

  /**
   * 
   */
  public TableStats setRecordCount(long recordCount) {
    this.recordCount = recordCount;
    setRecordCountIsSet(true);
    return this;
  }

  public void unsetRecordCount() {
    __isset_bit_vector.clear(__RECORDCOUNT_ISSET_ID);
  }

  /** Returns true if field recordCount is set (has been assigned a value) and false otherwise */
  public boolean isSetRecordCount() {
    return __isset_bit_vector.get(__RECORDCOUNT_ISSET_ID);
  }

  public void setRecordCountIsSet(boolean value) {
    __isset_bit_vector.set(__RECORDCOUNT_ISSET_ID, value);
  }

  /**
   * 
   */
  public long getRowCount() {
    return this.rowCount;
  }

  /**
   * 
   */
  public TableStats setRowCount(long rowCount) {
    this.rowCount = rowCount;
    setRowCountIsSet(true);
    return this;
  }

  public void unsetRowCount() {
    __isset_bit_vector.clear(__ROWCOUNT_ISSET_ID);
  }

  /** Returns true if field rowCount is set (has been assigned a value) and false otherwise */
  public boolean isSetRowCount() {
    return __isset_bit_vector.get(__ROWCOUNT_ISSET_ID);
  }

  public void setRowCountIsSet(boolean value) {
    __isset_bit_vector.set(__ROWCOUNT_ISSET_ID, value);
  }

  /**
   * 
   */
  public long getQueries() {
    return this.queries;
  }

  /**
   * 
   */
  public TableStats setQueries(long queries) {
    this.queries = queries;
    setQueriesIsSet(true);
    return this;
  }

  public void unsetQueries() {
    __isset_bit_vector.clear(__QUERIES_ISSET_ID);
  }

  /** Returns true if field queries is set (has been assigned a value) and false otherwise */
  public boolean isSetQueries() {
    return __isset_bit_vector.get(__QUERIES_ISSET_ID);
  }

  public void setQueriesIsSet(boolean value) {
    __isset_bit_vector.set(__QUERIES_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case TABLE_NAME:
      if (value == null) {
        unsetTableName();
      } else {
        setTableName((String)value);
      }
      break;

    case BYTES:
      if (value == null) {
        unsetBytes();
      } else {
        setBytes((Long)value);
      }
      break;

    case RECORD_COUNT:
      if (value == null) {
        unsetRecordCount();
      } else {
        setRecordCount((Long)value);
      }
      break;

    case ROW_COUNT:
      if (value == null) {
        unsetRowCount();
      } else {
        setRowCount((Long)value);
      }
      break;

    case QUERIES:
      if (value == null) {
        unsetQueries();
      } else {
        setQueries((Long)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case TABLE_NAME:
      return getTableName();

    case BYTES:
      return Long.valueOf(getBytes());

    case RECORD_COUNT:
      return Long.valueOf(getRecordCount());

    case ROW_COUNT:
      return Long.valueOf(getRowCount());

    case QUERIES:
      return Long.valueOf(getQueries());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case TABLE_NAME:
      return isSetTableName();
    case BYTES:
      return isSetBytes();
    case RECORD_COUNT:
      return isSetRecordCount();
    case ROW_COUNT:
      return isSetRowCount();
    case QUERIES:
      return isSetQueries();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof TableStats)
      return this.equals((TableStats)that);
    return false;
  }

  public boolean equals(TableStats that) {
    if (that == null)
      return false;

    boolean this_present_tableName = true && this.isSetTableName();
    boolean that_present_tableName = true && that.isSetTableName();
    if (this_present_tableName || that_present_tableName) {
      if (!(this_present_tableName && that_present_tableName))
        return false;
      if (!this.tableName.equals(that.tableName))
        return false;
    }

    boolean this_present_bytes = true;
    boolean that_present_bytes = true;
    if (this_present_bytes || that_present_bytes) {
      if (!(this_present_bytes && that_present_bytes))
        return false;
      if (this.bytes != that.bytes)
        return false;
    }

    boolean this_present_recordCount = true;
    boolean that_present_recordCount = true;
    if (this_present_recordCount || that_present_recordCount) {
      if (!(this_present_recordCount && that_present_recordCount))
        return false;
      if (this.recordCount != that.recordCount)
        return false;
    }

    boolean this_present_rowCount = true;
    boolean that_present_rowCount = true;
    if (this_present_rowCount || that_present_rowCount) {
      if (!(this_present_rowCount && that_present_rowCount))
        return false;
      if (this.rowCount != that.rowCount)
        return false;
    }

    boolean this_present_queries = true;
    boolean that_present_queries = true;
    if (this_present_queries || that_present_queries) {
      if (!(this_present_queries && that_present_queries))
        return false;
      if (this.queries != that.queries)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(TableStats other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    TableStats typedOther = (TableStats)other;

    lastComparison = Boolean.valueOf(isSetTableName()).compareTo(typedOther.isSetTableName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTableName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.tableName, typedOther.tableName);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetBytes()).compareTo(typedOther.isSetBytes());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetBytes()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.bytes, typedOther.bytes);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetRecordCount()).compareTo(typedOther.isSetRecordCount());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetRecordCount()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.recordCount, typedOther.recordCount);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetRowCount()).compareTo(typedOther.isSetRowCount());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetRowCount()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.rowCount, typedOther.rowCount);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetQueries()).compareTo(typedOther.isSetQueries());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetQueries()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.queries, typedOther.queries);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    org.apache.thrift.protocol.TField field;
    iprot.readStructBegin();
    while (true)
    {
      field = iprot.readFieldBegin();
      if (field.type == org.apache.thrift.protocol.TType.STOP) { 
        break;
      }
      switch (field.id) {
        case 1: // TABLE_NAME
          if (field.type == org.apache.thrift.protocol.TType.STRING) {
            this.tableName = iprot.readString();
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 2: // BYTES
          if (field.type == org.apache.thrift.protocol.TType.I64) {
            this.bytes = iprot.readI64();
            setBytesIsSet(true);
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 3: // RECORD_COUNT
          if (field.type == org.apache.thrift.protocol.TType.I64) {
            this.recordCount = iprot.readI64();
            setRecordCountIsSet(true);
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 4: // ROW_COUNT
          if (field.type == org.apache.thrift.protocol.TType.I64) {
            this.rowCount = iprot.readI64();
            setRowCountIsSet(true);
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 5: // QUERIES
          if (field.type == org.apache.thrift.protocol.TType.I64) {
            this.queries = iprot.readI64();
            setQueriesIsSet(true);
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        default:
          org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
      }
      iprot.readFieldEnd();
    }
    iprot.readStructEnd();

    // check for required fields of primitive type, which can't be checked in the validate method
    validate();
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    validate();

    oprot.writeStructBegin(STRUCT_DESC);
    if (this.tableName != null) {
      oprot.writeFieldBegin(TABLE_NAME_FIELD_DESC);
      oprot.writeString(this.tableName);
      oprot.writeFieldEnd();
    }
    oprot.writeFieldBegin(BYTES_FIELD_DESC);
    oprot.writeI64(this.bytes);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(RECORD_COUNT_FIELD_DESC);
    oprot.writeI64(this.recordCount);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(ROW_COUNT_FIELD_DESC);
    oprot.writeI64(this.rowCount);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(QUERIES_FIELD_DESC);
    oprot.writeI64(this.queries);
    oprot.writeFieldEnd();
    oprot.writeFieldStop();
    oprot.writeStructEnd();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("TableStats(");
    boolean first = true;

    sb.append("tableName:");
    if (this.tableName == null) {
      sb.append("null");
    } else {
      sb.append(this.tableName);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("bytes:");
    sb.append(this.bytes);
    first = false;
    if (!first) sb.append(", ");
    sb.append("recordCount:");
    sb.append(this.recordCount);
    first = false;
    if (!first) sb.append(", ");
    sb.append("rowCount:");
    sb.append(this.rowCount);
    first = false;
    if (!first) sb.append(", ");
    sb.append("queries:");
    sb.append(this.queries);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bit_vector = new BitSet(1);
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

}

