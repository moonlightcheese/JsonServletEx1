import java.util.Map;
import java.util.HashMap;
import java.sql.Types;

public class TypesLookup {
	private TypesLookup(){}
	public static final String mBit = "BIT";
	public static final String mTinyInt = "TINYINT";
	public static final String mSmallInt = "SMALLINT";
	public static final String mInteger = "INTEGER";
	public static final String mBigInt = "BIGINT";
	public static final String mFloat = "FLOAT";
	public static final String mReal = "REAL";
	public static final String mDouble = "DOUBLE";
	public static final String mNumeric = "NUMERIC";
	public static final String mDecimal = "DECIMAL";
	public static final String mChar = "CHAR";
	public static final String mVarChar = "VARCHAR";
	public static final String mLongVarChar = "LONGVARCHAR";
	public static final String mDate = "DATE";
	public static final String mTime = "TIME";
	public static final String mTimeStamp = "TIMESTAMP";
	public static final String mBinary = "BINARY";
	public static final String mVarBinary = "VARBINARY";
	public static final String mLongVarBinary = "LONGVARBINARY";
	public static final String mNull = "NULL";
	public static final String mOther = "OTHER";
	public static final String mJavaObject = "JAVA_OBJECT";
	public static final String mDistinct = "DISTINCT";
	public static final String mStruct = "STRUCT";
	public static final String mArray = "ARRAY";
	public static final String mBlob = "BLOB";
	public static final String mClob = "CLOB";
	public static final String mRef = "REF";
	public static final String mDataLink = "DATALINK";
	public static final String mBoolean = "BOOLEAN";
	public static final String mRowID = "ROWID";
	public static final String mNChar = "NCHAR";
	public static final String mNVarChar = "NVARCHAR";
	public static final String mLongNVarChar = "LONGNVARCHAR";
	public static final String mNClob = "NCLOB";
	public static final String mSQLXML = "SQLXML";
	private static final Map<Integer, String> mTypeMap = new HashMap<Integer, String>();
	static {
		mTypeMap.put(Types.BIT, mBit);
		mTypeMap.put(Types.TINYINT, mTinyInt);
		mTypeMap.put(Types.SMALLINT, mSmallInt);
		mTypeMap.put(Types.INTEGER, mInteger);
		mTypeMap.put(Types.BIGINT, mBigInt);
		mTypeMap.put(Types.FLOAT, mFloat);
		mTypeMap.put(Types.REAL, mReal);
		mTypeMap.put(Types.DOUBLE, mDouble);
		mTypeMap.put(Types.NUMERIC, mNumeric);
		mTypeMap.put(Types.DECIMAL, mDecimal);
		mTypeMap.put(Types.CHAR, mChar);
		mTypeMap.put(Types.VARCHAR, mVarChar);
		mTypeMap.put(Types.LONGVARCHAR, mLongVarChar);
		mTypeMap.put(Types.DATE, mDate);
		mTypeMap.put(Types.TIME, mTime);
		mTypeMap.put(Types.TIMESTAMP, mTimeStamp);
		mTypeMap.put(Types.BINARY, mBinary);
		mTypeMap.put(Types.VARBINARY, mVarBinary);
		mTypeMap.put(Types.LONGVARCHAR, mLongVarChar);
		mTypeMap.put(Types.NULL, mNull);
		mTypeMap.put(Types.OTHER, mOther);
		mTypeMap.put(Types.JAVA_OBJECT, mJavaObject);
		mTypeMap.put(Types.DISTINCT, mDistinct);
		mTypeMap.put(Types.STRUCT, mStruct);
		mTypeMap.put(Types.ARRAY, mArray);
		mTypeMap.put(Types.BLOB, mBlob);
		mTypeMap.put(Types.CLOB, mClob);
		mTypeMap.put(Types.REF, mRef);
		mTypeMap.put(Types.DATALINK, mDataLink);
		mTypeMap.put(Types.BOOLEAN, mBoolean);
		mTypeMap.put(Types.ROWID, mRowID);
		mTypeMap.put(Types.NCHAR, mNChar);
		mTypeMap.put(Types.NVARCHAR, mNVarChar);
		mTypeMap.put(Types.LONGNVARCHAR, mLongNVarChar);
		mTypeMap.put(Types.NCLOB, mNClob);
		mTypeMap.put(Types.SQLXML, mSQLXML);
	}
	public static String getTypeName(int type) {
		return mTypeMap.get(new Integer(type));
	}
}
