package com.conceptualsystems.database;

import java.util.*;

/*
public abstract class Schema implements BaseColumns {
	String TABLE_NAME = null;
	String DROP_TABLE = null;
	String CREATE_TABLE = null;
	public String getDropTableStatement() {
		return DROP_TABLE;
	}
	public String getCreateTableStatement() {
		return CREATE_TABLE;
	}
}
*/
public class Schema {
	public static String TABLE_NAME;
	public String CREATE_TABLE;
	public String _ID = "_id";
	protected Map<String, String> mColumnDefinitions = new HashMap<String, String>();
	protected Set<String> mColumnNames = new HashSet<String>();
	protected String mIndexColumn = null;

	public static final String TYPE_NOT_NULL = "NOT NULL";
	public static final String TYPE_INTEGER = "INTEGER";
	public static final String TYPE_REAL = "REAL";
	public static final String TYPE_TEXT = "TEXT";
	public static final String TYPE_BLOB = "BLOB";
	public static final String TYPE_BOOLEAN = "BOOLEAN";

	public Schema(String tableName) {
		TABLE_NAME = tableName;
	}

	public String getColumnDefinition(String key) {
		return mColumnDefinitions.get(key);
	}

	public String getDropTableStatement() {
		return "DROP TABLE IF EXISTS " + TABLE_NAME;
	}

	public String getCreateTableStatement() {
		if(CREATE_TABLE==null && mColumnNames!=null && !mColumnNames.isEmpty()) {
			CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + _ID + " " + TYPE_INTEGER + " PRIMARY KEY AUTOINCREMENT";
			Iterator<String> iter = mColumnNames.iterator();
			do {
				String name = iter.next();
				CREATE_TABLE += ", " + name + " " + mColumnDefinitions.get(name);
			} while(iter.hasNext());
			CREATE_TABLE += ");";
		}
		return CREATE_TABLE;
	}

	protected void addColumn(String columnName, String columnType) {
		mColumnNames.add(columnName);
		mColumnDefinitions.put(columnName, columnType);
	}

	public Map<String, String> getColumnDefinitions() {
		return mColumnDefinitions;
	}

	public String getIndexColumn() {
		return mIndexColumn;
	}

	protected void setIndexColumn(String indexColumn) {
		if(mColumnDefinitions.containsKey(indexColumn) && mColumnNames.contains(indexColumn)) {
			mIndexColumn = indexColumn;
		} else {
			throw new IllegalArgumentException();
		}
	}
	//public Set<String> getColumnSet();
	//public int size();
}