import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.List;
import java.util.LinkedList;
import java.sql.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.lang.Exception;
import java.text.ParseException;

import org.json.*;
import com.google.gson.*;
import com.conceptualsystems.database.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProcessRequest extends HttpServlet {
	public static final String REQUEST_TYPE = "request_type";
	public static final String REQUEST_TYPE_QUERY = "query";
	public static final String REQUEST_TYPE_RAW_QUERY = "raw_query";
	public static final String REQUEST_TYPE_WEIGHT = "weight";
	public static final String REQUEST_TYPE_UPDATE_TICKET = "update_ticket";
		//NEW_TICKET never used
	public static final String REQUEST_TYPE_UPDATE_CUSTOMER = "update_customer";
	public static final String REQUEST_TYPE_NEW_CUSTOMER = "new_customer";
	public static final String REQUEST_TYPE_UPDATE_PRODUCT = "update_product";
	public static final String REQUEST_TYPE_NEW_PRODUCT = "new_product";

	public static final String JSON_TYPE_STRING = "json_string";
	public static final String JSON_TYPE_BOOLEAN = "json_boolean";
	public static final String JSON_TYPE_NUMBER = "json_number";
	public static final String JSON_TYPE_TIMESTAMP = "json_timestamp";	//this isn't actually a type, but we need to distinguish this type from others so we can convert from M$ retarded date types.
	public static final String JSON_TYPE_NULL = "json_null";

	public static final String LOG_TAG = "ProcessRequest.java";

	private RollBack mRollBack = new RollBack();
	
	public static final String APP_NAME = "firstapp";
	private final Properties mConfig = new Properties();
	private final File mConfigFile = new File("mobile_config.txt");
	//private final File mDebugFile = new File("../webapps/"+APP_NAME+"/debug.txt");
	private final File mDebugFile = new File("debug.txt");
	SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private String mSQLPort = null;
	private String mSQLHost = null;
	private String mDbName = null;
	private String mDbUser = null;
	private String mDbPass = null;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		/*** DATA SOURCE METHOD
		//attempt to connect to SQL Server
		SQLServerDataSource ds = new SQLServerDataSource();
		ds.setServerName("localhost");
		ds.setDatabaseName("TMSV73");
		ds.setDescription("TMS Database v7.3");
		
		//bind to the application context for use elsewhere...?
		Context ctx = new InitialContext();
		ctx.bind("jdbc/TMSV73DB", ds);
		
		SQLServerConnection conn = ds.getConnection("sa", "keith");
		out.println("connection established?");
		***/

		//String query = "SELECT * FROM Ticket t JOIN TicketDetail td ON t.TicketID=td.TicketID WHERE Completed=0";
		//String query = "SELECT * FROM Ticket WHERE Completed=0";
		//out.println("<pre>"+parseRawQuery(query, out)+"</pre>");
		/*** OLD TEST CODE ***
		 * Connection conn = null;
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=TMSV73;user=sa;password=keith;");
		} catch(Exception e) {
			out.println(e.toString());
			return;
		}
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			int columns = rsmd.getColumnCount();
			out.println("<br>Records:"+columns+"<br><br>");
			out.println("<table>");
			out.println("<tr>");
			for(int i=1; i<columns; i++) {
				//System.out.println(""+i);
				out.println("<td><b>"+rsmd.getColumnName(i)+" : "+TypesLookup.getTypeName(rsmd.getColumnType(i))+"</b></td>");
			}
			out.println("</tr>");
			while(rs.next()) {
				out.println("<tr>");
				for(int i=1; i<columns; i++) {
					out.println("<td>"+rs.getString(i)+"</td>");
				}
				out.println("</tr>");
				
				//String ticketNumber = rs.getString("TicketNumber");
				//String ticketDetail = rs.getString("TicketDetailID");
				//out.println("\n<br>"+ticketNumber+"\t"+ticketDetail);
			}
			//out.println("</table>");
		} catch(Exception e) {
			out.println(e.toString());
			return;
		} finally {
			//if(stmt!=null) { stmt.close(); }
		}
		 *******/
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("text/html");
		
		try {
			File directory = new File(".");
			System.out.println(directory.getAbsolutePath());
			FileInputStream istream = new FileInputStream(mConfigFile);
			mConfig.load(istream);
			istream.close();
		} catch(IOException e) {
			System.out.println(e.toString());
		}

		mSQLPort = mConfig.getProperty("sql_port", "1433");
		mSQLHost = mConfig.getProperty("sql_host", "localhost");
		mDbName = mConfig.getProperty("db_name", "TMSV73");
		mDbUser = mConfig.getProperty("username", "sa");
		mDbPass = mConfig.getProperty("password", "keith");
		String requestString = request.getParameter("j");
		System.out.println(requestString);
		OutputStream outStream = response.getOutputStream();
		parseRequest(request.getParameter("j"), outStream);
	}

	public void parseRequest(String requestBody, OutputStream os) {
		try {
			JSONObject requestObject = new JSONObject(requestBody);
			String requestType = requestObject.getString(REQUEST_TYPE);
			/*** Java SE 7+ ***
			switch(requestType) {
				case REQUEST_TYPE_QUERY:
					return parseQuery(requestObject, out);
					break;
				case REQUEST_TYPE_RAW_QUERY:
					return parseRawQuery(requestObject, out);
					break;
				default:
					//do nothing
					break;
			}
			 */
			//PrintWriter out = response.getWriter();

			//outStream.write(parseRequest(requestString).getBytes());
			//String responseString = parseRequest(requestString);
			//System.out.println(responseString);
			//out.println(responseString);
			if(requestType.equals(REQUEST_TYPE_UPDATE_PRODUCT)) {
				os.write(parseUpdateProduct(requestObject).toString().getBytes());
			}
			if(requestType.equals(REQUEST_TYPE_NEW_PRODUCT)) {
				os.write(parseNewProduct(requestObject).toString().getBytes());
			}
			if(requestType.equals(REQUEST_TYPE_UPDATE_CUSTOMER)) {
				os.write(parseUpdateCustomer(requestObject).toString().getBytes());
			}
			if(requestType.equals(REQUEST_TYPE_NEW_CUSTOMER)) {
				os.write(parseNewCustomer(requestObject).toString().getBytes());
			}
			if(requestType.equals(REQUEST_TYPE_UPDATE_TICKET)) {
				os.write(parseTicket(requestObject).toString().getBytes());
			}
			if(requestType.equals(REQUEST_TYPE_WEIGHT)) {
				os.write(parseWeight(requestObject).toString().getBytes());
			}
			if(requestType.equals(REQUEST_TYPE_QUERY)) {
				FileOutputStream ostream = null;
				try {
					ostream = new FileOutputStream(mDebugFile);
					if(mDebugFile.exists()) {
						System.out.println(mDebugFile.getAbsolutePath());
					} else {
						mDebugFile.createNewFile();
					}
				} catch(IOException e) {
					System.out.println(e.toString());
				}
				//parseQuery(requestObject, ostream);
				os.write(parseQuery(requestObject, null).toString(5).getBytes());
			} else if(requestType.equals(REQUEST_TYPE_RAW_QUERY)) {
				//return parseRawQuery(requestObject, out);
			}
		} catch(Exception e) {
			System.out.println(e.toString());
			System.out.println(e.getMessage());
			try {
				JSONObject errorResponseObject = new JSONObject();
				errorResponseObject.put("error", e.getMessage());
				//out.println(errorResponseObject.toString());
				os.write(errorResponseObject.toString().getBytes());
			} catch(JSONException jsone) {
				System.out.println("unable to send error message to requester!");
			} catch(IOException ioe) {
				System.out.println("unable to send error message to requester!");
			}
		}
	}

	private class RollBack {
		List<RollBackRecord> mRollBackRecords;

		private class RollBackRecord {
			private String mTableName;
			private String mIndexColumn;
			private String mIndexValue;

			public String getIndexColumn() {
				return mIndexColumn;
			}

			public String getIndexValue() {
				return mIndexValue;
			}

			public String getTableName() {
				return mTableName;
			}

			public RollBackRecord(String tableName, String indexColumn, String indexValue) {
				mTableName = tableName;
				mIndexColumn = indexColumn;
				mIndexValue = indexValue;
			}
		}

		public RollBack() {
			mRollBackRecords = new LinkedList<RollBackRecord>();
		}

		public void roll() {
			//undo all transactions in mRollBackRecords
			//delete all records in mRollBackRecords
			for(RollBackRecord rbr : mRollBackRecords) {
				String query = "DELETE FROM "+rbr.getTableName()+" WHERE "+rbr.getIndexColumn()+"='"+rbr.getIndexValue()+"'";
			}
		}
	}

	public void updateProduct(JSONObject recordObject) throws JSONException, SQLException {
		String valuesPortion = "";

		recordObject.remove(DbSingleton.ProductSchema.COLUMN_LAST_MODIFIED);
		recordObject.put(DbSingleton.ProductSchema.COLUMN_LAST_MODIFIED, mDateFormat.format(new java.util.Date()));
		recordObject.remove("TMSRowGUID");

		//get column names
		JsonParser parser = new JsonParser();
		JsonObject recordGson = parser.parse(recordObject.toString()).getAsJsonObject();
		Set<Map.Entry<String, JsonElement>> entrySet = recordGson.entrySet();
		String productID = null;

		//construct the SQL query from the JSON elements.
        //the valuesPortion String will contain the values we want to update in the record
		{
			Iterator<Map.Entry<String, JsonElement>> iterator = entrySet.iterator();
			while(iterator.hasNext()) {
				Map.Entry<String, JsonElement> entry = iterator.next();
				if(entry.getKey().equals("metadata")) {
					//do nothing
				} else {
					JsonPrimitive value = entry.getValue().getAsJsonPrimitive();
					//System.out.println(entry.getKey()+" : "+value.getAsString());
					if(value.isString()) {
						if(!valuesPortion.equals("")) {
							valuesPortion+=",";
						}
						//System.out.println(entry.getKey()+" : '"+value.getAsString()+"'");
						valuesPortion += entry.getKey()+"=";
						valuesPortion += "'"+value.getAsString()+"'";
						if(entry.getKey().equals(DbSingleton.ProductSchema.COLUMN_ID)) {
							productID = value.getAsString();
						}
					} else if(value.isNumber()) {
						if(value.getAsString().contains(".")) {
							if(!valuesPortion.equals("")) {
								valuesPortion+=",";
							}
							//System.out.println(entry.getKey()+" : "+(new Double(value.getAsDouble()).toString()));
							valuesPortion += entry.getKey()+"=";
							valuesPortion += new Double(value.getAsDouble()).toString();
						} else {
							if(!valuesPortion.equals("")) {
								valuesPortion+=",";
							}
							//System.out.println(entry.getKey()+" : "+(new Long(value.getAsLong()).toString()));
							valuesPortion += entry.getKey()+"=";
							valuesPortion += value.getAsString();
						}
					} else if(value.isBoolean()) {
						if(!valuesPortion.equals("")) {
							valuesPortion+=",";
						}
						//System.out.println(entry.getKey()+" : "+(new Boolean(value.getAsBoolean()).toString()));
						if(value.getAsBoolean()) {
							valuesPortion += entry.getKey()+"=";
							valuesPortion += "1";
						} else {
							valuesPortion += entry.getKey()+"=";
							valuesPortion += "0";
						}
					}
				}
			}
		}

		//String insertQuery = "INSERT INTO "+DbSingleton.TICKET_DETAIL_TABLE_NAME+"("+columnsPortion+") VALUES ("+valuesPortion+")";
		if(productID==null) {
			throw new SQLException("no customer id found!");
		}
		String updateQuery = "UPDATE "+DbSingleton.PRODUCT_TABLE_NAME+" SET "+valuesPortion+" WHERE "+DbSingleton.ProductSchema.COLUMN_ID+"='"+productID+"'";
		//String identQuery = "SELECT IDENT_CURRENT ('"+DbSingleton.TICKET_DETAIL_TABLE_NAME+"') AS TicketDetailID";

		executeQuery(updateQuery);
	}

	public JSONObject parseUpdateProduct(JSONObject requestObject) throws SQLException, JSONException {
		JSONObject record = new JSONObject();
		JSONArray recordArray = requestObject.getJSONArray("records");
		String productID = null;
		for(int i=0; i<recordArray.length(); i++) {
			record = recordArray.getJSONObject(i);
			productID = record.getString(DbSingleton.ProductSchema.COLUMN_ID);
			updateProduct(record);
		}
		JSONObject responseObject = new JSONObject();
		responseObject.put("records", recordArray.length());
		responseObject.put(DbSingleton.ProductSchema.COLUMN_ID, productID);

		return record;
	}

	public void newProduct(JSONObject recordObject) throws JSONException, SQLException {
		String valuesPortion = "";
		String columnsPortion = "";

		recordObject.remove(DbSingleton.ProductSchema.COLUMN_LAST_MODIFIED);
		recordObject.put(DbSingleton.ProductSchema.COLUMN_LAST_MODIFIED, mDateFormat.format(new java.util.Date()));
		recordObject.remove("TMSRowGUID");

		//get column names
		JsonParser parser = new JsonParser();
		JsonObject recordGson = parser.parse(recordObject.toString()).getAsJsonObject();
		Set<Map.Entry<String, JsonElement>> entrySet = recordGson.entrySet();
		{
			Iterator<Map.Entry<String, JsonElement>> iterator = entrySet.iterator();
			while(iterator.hasNext()) {
				Map.Entry<String, JsonElement> entry = iterator.next();
				if(entry.getKey().equals("metadata")) {
					//do nothing
				} else {
					if(!columnsPortion.equals("")) {
						columnsPortion+=",";
					}
					columnsPortion+=entry.getKey();
				}
			}
		}

		//get values
		{
			Iterator<Map.Entry<String, JsonElement>> iterator = entrySet.iterator();
			while(iterator.hasNext()) {
				Map.Entry<String, JsonElement> entry = iterator.next();
				if(entry.getKey().equals("metadata")) {
					//do nothing
				} else {
					JsonPrimitive value = entry.getValue().getAsJsonPrimitive();
					if(value.isString()) {
						if(!valuesPortion.equals("")) {
							valuesPortion+=",";
						}
						valuesPortion += "'"+value.getAsString()+"'";
					} else if(value.isNumber()) {
						if(value.getAsString().contains(".")) {
							if(!valuesPortion.equals("")) {
								valuesPortion+=",";
							}
							valuesPortion += new Double(value.getAsDouble()).toString();
						} else {
							if(!valuesPortion.equals("")) {
								valuesPortion+=",";
							}
							valuesPortion += new Long(value.getAsLong()).toString();
						}
					} else if(value.isBoolean()) {
						if(!valuesPortion.equals("")) {
							valuesPortion+=",";
						}
						if(value.getAsBoolean())
							valuesPortion += "1";
						else
							valuesPortion += "0";
					}
				}
			}
		}

		String insertQuery = "INSERT INTO "+DbSingleton.PRODUCT_TABLE_NAME+"("+columnsPortion+") VALUES ("+valuesPortion+")";
		String identQuery = "SELECT IDENT_CURRENT ('"+DbSingleton.PRODUCT_TABLE_NAME+"') AS ProductID";

		executeQuery(insertQuery);
	}

	public JSONObject parseNewProduct(JSONObject requestObject) throws SQLException, JSONException {
		JSONObject record = new JSONObject();
		JSONArray recordArray = requestObject.getJSONArray("records");
		String productID = null;
		JSONObject responseObject = new JSONObject();
		for(int i=0; i<recordArray.length(); i++) {
			record = recordArray.getJSONObject(i);
			productID = record.getString(DbSingleton.ProductSchema.COLUMN_ID);
			String checkQuery = "SELECT * FROM Product WHERE ProductID='"+productID+"'";
			ResultSet checkResultSet = executeQuery(checkQuery);
			checkResultSet.last();
			int rowCount = checkResultSet.getRow();
			checkResultSet.first();
			if(rowCount<1)
				newProduct(record);
			else {
				//updateCustomer(record);
				responseObject.put("error", "Product already exists!");
			}
		}

		responseObject.put("records", recordArray.length());
		responseObject.put(DbSingleton.ProductSchema.COLUMN_ID, productID);

		return responseObject;
	}

	public void updateCustomer(JSONObject recordObject) throws JSONException, SQLException {
		String valuesPortion = "";

		String dob = null;
		try {
			dob = mDateFormat.format(new java.util.Date(recordObject.getLong(DbSingleton.CustomerSchema.COLUMN_DL_DOB)));
		} catch(JSONException jsone) {
			dob = recordObject.getString(DbSingleton.CustomerSchema.COLUMN_DL_DOB);
		} finally {
			recordObject.put(DbSingleton.CustomerSchema.COLUMN_DL_DOB, dob);
		}

		String issueDate = null;
		try {
			issueDate = mDateFormat.format(new java.util.Date(recordObject.getLong(DbSingleton.CustomerSchema.COLUMN_DL_ISSUE_DATE)));
		} catch(JSONException jsone) {
			issueDate = recordObject.getString(DbSingleton.CustomerSchema.COLUMN_DL_ISSUE_DATE);
		} finally {
			recordObject.put(DbSingleton.CustomerSchema.COLUMN_DL_ISSUE_DATE, issueDate);
		}

		String expirationDate = null;
		try {
			expirationDate = mDateFormat.format(new java.util.Date(recordObject.getLong(DbSingleton.CustomerSchema.COLUMN_DL_EXPIRATION_DATE)));
		} catch(JSONException jsone) {
			expirationDate = recordObject.getString(DbSingleton.CustomerSchema.COLUMN_DL_EXPIRATION_DATE);
		} finally {
			recordObject.put(DbSingleton.CustomerSchema.COLUMN_DL_EXPIRATION_DATE, expirationDate);
		}

		recordObject.remove(DbSingleton.CustomerSchema.COLUMN_LAST_MODIFIED);
		recordObject.put(DbSingleton.CustomerSchema.COLUMN_LAST_MODIFIED, mDateFormat.format(new java.util.Date()));
		recordObject.remove("TMSRowGUID");

		//get column names
		JsonParser parser = new JsonParser();
		JsonObject recordGson = parser.parse(recordObject.toString()).getAsJsonObject();
		Set<Map.Entry<String, JsonElement>> entrySet = recordGson.entrySet();
		String customerID = null;

		//get values
		{
			Iterator<Map.Entry<String, JsonElement>> iterator = entrySet.iterator();
			while(iterator.hasNext()) {
				Map.Entry<String, JsonElement> entry = iterator.next();
				if(entry.getKey().equals("metadata")) {
					//do nothing
				} else {
					JsonPrimitive value = entry.getValue().getAsJsonPrimitive();
					//System.out.println(entry.getKey()+" : "+value.getAsString());
					if(value.isString()) {
						if(!valuesPortion.equals("")) {
							valuesPortion+=",";
						}
						//System.out.println(entry.getKey()+" : '"+value.getAsString()+"'");
						valuesPortion += entry.getKey()+"=";
						valuesPortion += "'"+value.getAsString()+"'";
						if(entry.getKey().equals(DbSingleton.CustomerSchema.COLUMN_ID)) {
							customerID = value.getAsString();
						}
					} else if(value.isNumber()) {
						if(value.getAsString().contains(".")) {
							if(!valuesPortion.equals("")) {
								valuesPortion+=",";
							}
							//System.out.println(entry.getKey()+" : "+(new Double(value.getAsDouble()).toString()));
							valuesPortion += entry.getKey()+"=";
							valuesPortion += new Double(value.getAsDouble()).toString();
						} else {
							if(!valuesPortion.equals("")) {
								valuesPortion+=",";
							}
							//System.out.println(entry.getKey()+" : "+(new Long(value.getAsLong()).toString()));
							valuesPortion += entry.getKey()+"=";
							valuesPortion += value.getAsString();
						}
					} else if(value.isBoolean()) {
						if(!valuesPortion.equals("")) {
							valuesPortion+=",";
						}
						//System.out.println(entry.getKey()+" : "+(new Boolean(value.getAsBoolean()).toString()));
						if(value.getAsBoolean()) {
							valuesPortion += entry.getKey()+"=";
							valuesPortion += "1";
						} else {
							valuesPortion += entry.getKey()+"=";
							valuesPortion += "0";
						}
					}
				}
			}
		}

		//String insertQuery = "INSERT INTO "+DbSingleton.TICKET_DETAIL_TABLE_NAME+"("+columnsPortion+") VALUES ("+valuesPortion+")";
		if(customerID==null) {
			throw new SQLException("no customer id found!");
		}
		String updateQuery = "UPDATE "+DbSingleton.CUSTOMER_TABLE_NAME+" SET "+valuesPortion+" WHERE "+DbSingleton.CustomerSchema.COLUMN_ID+"='"+customerID+"'";
		//String identQuery = "SELECT IDENT_CURRENT ('"+DbSingleton.TICKET_DETAIL_TABLE_NAME+"') AS TicketDetailID";

		executeQuery(updateQuery);
	}

	public JSONObject parseUpdateCustomer(JSONObject requestObject) throws SQLException, JSONException {
		JSONObject record = new JSONObject();
		JSONArray recordArray = requestObject.getJSONArray("records");
		String customerID = null;
		for(int i=0; i<recordArray.length(); i++) {
			record = recordArray.getJSONObject(i);
			customerID = record.getString(DbSingleton.CustomerSchema.COLUMN_ID);
			updateCustomer(record);
		}
		JSONObject responseObject = new JSONObject();
		responseObject.put("records", recordArray.length());
		responseObject.put(DbSingleton.CustomerSchema.COLUMN_ID, customerID);

		return record;
	}

	public void newCustomer(JSONObject recordObject) throws JSONException, SQLException {
		String valuesPortion = "";
		String columnsPortion = "";

		String dob = null;
		try {
			dob = mDateFormat.format(new java.util.Date(recordObject.getLong(DbSingleton.CustomerSchema.COLUMN_DL_DOB)));
		} catch(JSONException jsone) {
			dob = recordObject.getString(DbSingleton.CustomerSchema.COLUMN_DL_DOB);
		} finally {
			recordObject.put(DbSingleton.CustomerSchema.COLUMN_DL_DOB, dob);
		}

		String issueDate = null;
		try {
			issueDate = mDateFormat.format(new java.util.Date(recordObject.getLong(DbSingleton.CustomerSchema.COLUMN_DL_ISSUE_DATE)));
		} catch(JSONException jsone) {
			issueDate = recordObject.getString(DbSingleton.CustomerSchema.COLUMN_DL_ISSUE_DATE);
		} finally {
			recordObject.put(DbSingleton.CustomerSchema.COLUMN_DL_ISSUE_DATE, issueDate);
		}

		String expirationDate = null;
		try {
			expirationDate = mDateFormat.format(new java.util.Date(recordObject.getLong(DbSingleton.CustomerSchema.COLUMN_DL_EXPIRATION_DATE)));
		} catch(JSONException jsone) {
			expirationDate = recordObject.getString(DbSingleton.CustomerSchema.COLUMN_DL_EXPIRATION_DATE);
		} finally {
			recordObject.put(DbSingleton.CustomerSchema.COLUMN_DL_EXPIRATION_DATE, expirationDate);
		}

		recordObject.remove(DbSingleton.CustomerSchema.COLUMN_LAST_MODIFIED);
		recordObject.put(DbSingleton.CustomerSchema.COLUMN_LAST_MODIFIED, mDateFormat.format(new java.util.Date()));
		recordObject.remove("TMSRowGUID");

		//get column names
		JsonParser parser = new JsonParser();
		JsonObject recordGson = parser.parse(recordObject.toString()).getAsJsonObject();
		Set<Map.Entry<String, JsonElement>> entrySet = recordGson.entrySet();
		{
			Iterator<Map.Entry<String, JsonElement>> iterator = entrySet.iterator();
			while(iterator.hasNext()) {
				Map.Entry<String, JsonElement> entry = iterator.next();
				if(entry.getKey().equals("metadata")) {
					//do nothing
				} else {
					if(!columnsPortion.equals("")) {
						columnsPortion+=",";
					}
					columnsPortion+=entry.getKey();
				}
			}
		}

		//get values
		{
			Iterator<Map.Entry<String, JsonElement>> iterator = entrySet.iterator();
			while(iterator.hasNext()) {
				Map.Entry<String, JsonElement> entry = iterator.next();
				if(entry.getKey().equals("metadata")) {
					//do nothing
				} else {
					JsonPrimitive value = entry.getValue().getAsJsonPrimitive();
					if(value.isString()) {
						if(!valuesPortion.equals("")) {
							valuesPortion+=",";
						}
						valuesPortion += "'"+value.getAsString()+"'";
					} else if(value.isNumber()) {
						if(value.getAsString().contains(".")) {
							if(!valuesPortion.equals("")) {
								valuesPortion+=",";
							}
							valuesPortion += new Double(value.getAsDouble()).toString();
						} else {
							if(!valuesPortion.equals("")) {
								valuesPortion+=",";
							}
							valuesPortion += new Long(value.getAsLong()).toString();
						}
					} else if(value.isBoolean()) {
						if(!valuesPortion.equals("")) {
							valuesPortion+=",";
						}
						if(value.getAsBoolean())
							valuesPortion += "1";
						else
							valuesPortion += "0";
					}
				}
			}
		}

		String insertQuery = "INSERT INTO "+DbSingleton.CUSTOMER_TABLE_NAME+"("+columnsPortion+") VALUES ("+valuesPortion+")";
		//String identQuery = "SELECT IDENT_CURRENT ('"+DbSingleton.CUSTOMER_TABLE_NAME+"') AS CustomerID";
		//String customerIsHaulerQuery = "SELECT * FROM "+DbSingleton.CUSTOMER_TABLE_NAME+" "
		executeQuery(insertQuery);
		/*
		ResultSet identResultSet = executeQuery(identQuery);
		identResultSet.first();
		//System.out.println(identResultSet.getRow());
		int customerID = identResultSet.getInt(1);
		//System.out.println(ticketID);
		return customerID;
		*/
	}

	public JSONObject parseNewCustomer(JSONObject requestObject) throws SQLException, JSONException {
		JSONObject record = new JSONObject();
		JSONArray recordArray = requestObject.getJSONArray("records");
		String customerID = null;
		JSONObject responseObject = new JSONObject();
		for(int i=0; i<recordArray.length(); i++) {
			record = recordArray.getJSONObject(i);
			customerID = record.getString(DbSingleton.CustomerSchema.COLUMN_ID);
			String checkQuery = "SELECT * FROM Customer WHERE CustomerID='"+customerID+"'";
			ResultSet checkResultSet = executeQuery(checkQuery);
			checkResultSet.last();
			int rowCount = checkResultSet.getRow();
			checkResultSet.first();
			if(rowCount<1)
				newCustomer(record);
			else {
				//updateCustomer(record);
				responseObject.put("error", "Customer already exists!");
			}
		}

		responseObject.put("records", recordArray.length());
		responseObject.put(DbSingleton.CustomerSchema.COLUMN_ID, customerID);

		return responseObject;
	}

	public JSONObject parseTicket(JSONObject requestObject) throws SQLException, JSONException {
		JSONObject responseObject = new JSONObject();
		JSONArray recordArray = requestObject.getJSONArray("records");
		Integer ticketID = null;
		for(int i=0; i<recordArray.length(); i++) {
			JSONObject record = recordArray.getJSONObject(i);
			JSONObject metaData = record.getJSONObject("metadata");
			String tableName = metaData.getString("table");
			if(tableName.equals(DbSingleton.TICKET_TABLE_NAME)) {
				ticketID = record.getInt(DbSingleton.TicketDetailSchema.COLUMN_TICKET_ID);
				if(ticketID==null)
					throw new JSONException("no ticket id found");
				if(ticketID<0) {
					ticketID = newTicket(record);
				} else {
					updateTicket(record);
				}
			}
		}

		//loop through all records, insert/update ticket details
		List<Integer> details = new ArrayList<Integer>();
		for(int i=0; i<recordArray.length(); i++) {
			JSONObject record = recordArray.getJSONObject(i);
			System.out.println(record.toString(5));
			JSONObject metaData = record.getJSONObject("metadata");
			String tableName = metaData.getString("table");
			if(tableName.equals(DbSingleton.TICKET_DETAIL_TABLE_NAME)) {
				Integer ticketDetailID = record.getInt(DbSingleton.TicketDetailSchema.COLUMN_TICKET_DETAIL_ID);
				if(ticketDetailID==null || ticketID==null)
					throw new JSONException("missing ticket id or ticket detail id");
				if(ticketDetailID<0) {
					ticketDetailID = newTicketDetail(ticketID, record);
				} else {
					updateTicketDetail(ticketID, record);
				}
				details.add(ticketDetailID);
			}
		}

		String deleteQuery = "DELETE FROM "+DbSingleton.TICKET_DETAIL_TABLE_NAME+" WHERE "+DbSingleton.TicketDetailSchema.COLUMN_TICKET_ID+"="+ticketID;
		for(Integer detailID : details) {
			deleteQuery+=" AND "+DbSingleton.TicketDetailSchema.COLUMN_TICKET_DETAIL_ID+"!="+detailID.toString();
		}
		executeQuery(deleteQuery);

		responseObject.put("records", recordArray.length());
		responseObject.put(DbSingleton.TicketSchema.COLUMN_TICKET_ID, ticketID);

		return responseObject;
	}

	public void updateTicket(JSONObject recordObject) throws SQLException, JSONException {
		//check for ticket existence
		//check for ticket detail existence
		String valuesPortion = "";

		String ticketDate = null;
		try {
			ticketDate = mDateFormat.format(new java.util.Date(recordObject.getLong(DbSingleton.TicketSchema.COLUMN_TICKET_DATE)));
		} catch(JSONException jsone) {
			ticketDate = recordObject.getString(DbSingleton.TicketSchema.COLUMN_TICKET_DATE);
		} finally {
			recordObject.put(DbSingleton.TicketSchema.COLUMN_TICKET_DATE, ticketDate);
		}

		String invoiceDate = null;
		try {
			invoiceDate = mDateFormat.format(new java.util.Date(recordObject.getLong(DbSingleton.TicketSchema.COLUMN_INVOICE_DATE)));
		} catch(JSONException jsone) {
			invoiceDate = recordObject.getString(DbSingleton.TicketSchema.COLUMN_INVOICE_DATE);
		} finally {
			recordObject.put(DbSingleton.TicketSchema.COLUMN_INVOICE_DATE, invoiceDate);
		}

		String voucherRedeemableDate = null;
		try {
			voucherRedeemableDate = mDateFormat.format(new java.util.Date(recordObject.getLong(DbSingleton.TicketSchema.COLUMN_VOUCHER_REDEEMABLE_DATE)));
		} catch(JSONException jsone) {
			voucherRedeemableDate = recordObject.getString(DbSingleton.TicketSchema.COLUMN_VOUCHER_REDEEMABLE_DATE);
		} finally {
			recordObject.put(DbSingleton.TicketSchema.COLUMN_VOUCHER_REDEEMABLE_DATE, voucherRedeemableDate);
		}

		String voucherExpirationDate = null;
		try {
			voucherExpirationDate = mDateFormat.format(new java.util.Date(recordObject.getLong(DbSingleton.TicketSchema.COLUMN_VOUCHER_EXPIRATION_DATE)));
		} catch(JSONException jsone) {
			voucherExpirationDate = recordObject.getString(DbSingleton.TicketSchema.COLUMN_VOUCHER_EXPIRATION_DATE);
		} finally {
			recordObject.put(DbSingleton.TicketSchema.COLUMN_VOUCHER_EXPIRATION_DATE, voucherExpirationDate);
		}

		String paymentDate = null;
		try {
			paymentDate = mDateFormat.format(new java.util.Date(recordObject.getLong(DbSingleton.TicketSchema.COLUMN_PAYMENT_DATE)));
		} catch(JSONException jsone) {
			paymentDate = recordObject.getString(DbSingleton.TicketSchema.COLUMN_PAYMENT_DATE);
		} finally {
			recordObject.put(DbSingleton.TicketSchema.COLUMN_PAYMENT_DATE, paymentDate);
		}

		String trailerLastTareDate = null;
		try {
			trailerLastTareDate = mDateFormat.format(new java.util.Date(recordObject.getLong(DbSingleton.TicketSchema.COLUMN_TRAILER_LAST_TARE_DATE)));
		} catch(JSONException jsone) {
			trailerLastTareDate = recordObject.getString(DbSingleton.TicketSchema.COLUMN_TRAILER_LAST_TARE_DATE);
		} finally {
			recordObject.put(DbSingleton.TicketSchema.COLUMN_TRAILER_LAST_TARE_DATE, trailerLastTareDate);
		}

		String truckLastTareDate = null;
		try {
			truckLastTareDate = mDateFormat.format(new java.util.Date(recordObject.getLong(DbSingleton.TicketSchema.COLUMN_TRUCK_LAST_TARE_DATE)));
		} catch(JSONException jsone) {
			truckLastTareDate = recordObject.getString(DbSingleton.TicketSchema.COLUMN_TRUCK_LAST_TARE_DATE);
		} finally {
			recordObject.put(DbSingleton.TicketSchema.COLUMN_TRUCK_LAST_TARE_DATE, truckLastTareDate);
		}

		recordObject.remove(DbSingleton.TicketSchema.COLUMN_LAST_MODIFIED);
		recordObject.put(DbSingleton.TicketSchema.COLUMN_LAST_MODIFIED, mDateFormat.format(new java.util.Date()));
		recordObject.remove("TMSRowGUID");

		//recordObject.remove(DbSingleton.TicketSchema.COLUMN_TICKET_NUMBER);
		//recordObject.put(DbSingleton.TicketSchema.COLUMN_TICKET_NUMBER, getNextTicketNumber());

		//get column names
		JsonParser parser = new JsonParser();
		JsonObject recordGson = parser.parse(recordObject.toString()).getAsJsonObject();
		Set<Map.Entry<String, JsonElement>> entrySet = recordGson.entrySet();
		Integer ticketID = null;

		//get values
		{
			Iterator<Map.Entry<String, JsonElement>> iterator = entrySet.iterator();
			while(iterator.hasNext()) {
				Map.Entry<String, JsonElement> entry = iterator.next();
				if(entry.getKey().equals("metadata")) {
					//do nothing
				} else {
					JsonPrimitive value = entry.getValue().getAsJsonPrimitive();
					//System.out.println(entry.getKey()+" : "+value.getAsString());
					if(value.isString()) {
						if(!valuesPortion.equals("")) {
							valuesPortion+=",";
						}
						//System.out.println(entry.getKey()+" : '"+value.getAsString()+"'");
						valuesPortion += entry.getKey()+"=";
						valuesPortion += "'"+value.getAsString()+"'";
					} else if(value.isNumber()) {
						if(value.getAsString().contains(".")) {
							if(!valuesPortion.equals("")) {
								valuesPortion+=",";
							}
							//System.out.println(entry.getKey()+" : "+(new Double(value.getAsDouble()).toString()));
							valuesPortion += entry.getKey()+"=";
							valuesPortion += new Double(value.getAsDouble()).toString();
						} else {
							//String ticketNumber = new Long(value.getAsLong()).toString();
							Long val = new Long(value.getAsLong());
							if(entry.getKey().equals(DbSingleton.TicketSchema.COLUMN_TICKET_ID)) {
								ticketID = val.intValue();
							} else {
								if(!valuesPortion.equals("")) {
									valuesPortion+=",";
								}
								//System.out.println(entry.getKey()+" : "+(new Long(value.getAsLong()).toString()));
								valuesPortion += entry.getKey()+"=";
								valuesPortion += val.toString();
							}
						}
					} else if(value.isBoolean()) {
						if(!valuesPortion.equals("")) {
							valuesPortion+=",";
						}
						//System.out.println(entry.getKey()+" : "+(new Boolean(value.getAsBoolean()).toString()));
						if(value.getAsBoolean()) {
							valuesPortion += entry.getKey()+"=";
							valuesPortion += "1";
						} else {
							valuesPortion += entry.getKey()+"=";
							valuesPortion += "0";
						}
					}
				}
			}
		}

		//String insertQuery = "INSERT INTO "+DbSingleton.TICKET_DETAIL_TABLE_NAME+"("+columnsPortion+") VALUES ("+valuesPortion+")";
		if(ticketID==null) {
			throw new SQLException("no ticket id found!");
		}
		String updateQuery = "UPDATE "+DbSingleton.TICKET_TABLE_NAME+" SET "+valuesPortion+" WHERE "+DbSingleton.TicketSchema.COLUMN_TICKET_ID+"="+ticketID;
		//String identQuery = "SELECT IDENT_CURRENT ('"+DbSingleton.TICKET_DETAIL_TABLE_NAME+"') AS TicketDetailID";

		executeQuery(updateQuery);
	}

	public void updateTicketDetail(Integer ticketID, JSONObject recordObject) throws SQLException, JSONException {

		//check for ticket detail existence
		String valuesPortion = "";

		recordObject.remove(DbSingleton.TicketDetailSchema.COLUMN_LAST_MODIFIED);
		recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_LAST_MODIFIED, mDateFormat.format(new java.util.Date()));
		recordObject.remove("TMSRowGUID");

		String grossDate = null;
		try {
			grossDate = mDateFormat.format(new java.util.Date(recordObject.getLong(DbSingleton.TicketDetailSchema.COLUMN_GROSS_LBS_DT)));
		} catch(JSONException jsone) {
			grossDate = recordObject.getString(DbSingleton.TicketDetailSchema.COLUMN_GROSS_LBS_DT);
		} finally {
			recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_GROSS_LBS_DT, grossDate);
		}

		String grossALTDate = null;
		try {
			grossALTDate = mDateFormat.format(new java.util.Date(recordObject.getLong(DbSingleton.TicketDetailSchema.COLUMN_ALT_GROSS_LBS_DT)));
		} catch(JSONException jsone) {
			grossALTDate = recordObject.getString(DbSingleton.TicketDetailSchema.COLUMN_ALT_GROSS_LBS_DT);
		} finally {
			recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_ALT_GROSS_LBS_DT, grossALTDate);
		}

		String tareDate = null;
		try {
			tareDate = mDateFormat.format(new java.util.Date(recordObject.getLong(DbSingleton.TicketDetailSchema.COLUMN_TARE_LBS_DT)));
		} catch(JSONException jsone) {
			tareDate = recordObject.getString(DbSingleton.TicketDetailSchema.COLUMN_TARE_LBS_DT);
		} finally {
			recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_TARE_LBS_DT, tareDate);
		}

		String tareALTDate = null;
		try {
			tareALTDate = mDateFormat.format(new java.util.Date(recordObject.getLong(DbSingleton.TicketDetailSchema.COLUMN_ALT_TARE_LBS_DT)));
		} catch(JSONException jsone) {
			tareALTDate = recordObject.getString(DbSingleton.TicketDetailSchema.COLUMN_ALT_TARE_LBS_DT);
		} finally {
			recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_ALT_TARE_LBS_DT, tareALTDate);
		}

		String deductDate = null;
		try {
			deductDate = mDateFormat.format(new java.util.Date(recordObject.getLong(DbSingleton.TicketDetailSchema.COLUMN_DEDUCT_LBS_DT)));
		} catch(JSONException jsone) {
			deductDate = recordObject.getString(DbSingleton.TicketDetailSchema.COLUMN_DEDUCT_LBS_DT);
		} finally {
			recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_DEDUCT_LBS_DT, deductDate);
		}

		String deductALTDate = null;
		try {
			deductALTDate = mDateFormat.format(new java.util.Date(recordObject.getLong(DbSingleton.TicketDetailSchema.COLUMN_ALT_DEDUCT_LBS_DT)));
		} catch(JSONException jsone) {
			deductALTDate = recordObject.getString(DbSingleton.TicketDetailSchema.COLUMN_ALT_DEDUCT_LBS_DT);
		} finally {
			recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_ALT_DEDUCT_LBS_DT, deductALTDate);
		}

		//recordObject.remove(DbSingleton.TicketSchema.COLUMN_TICKET_NUMBER);
		//recordObject.put(DbSingleton.TicketSchema.COLUMN_TICKET_NUMBER, getNextTicketNumber());

		//get column names
		JsonParser parser = new JsonParser();
		JsonObject recordGson = parser.parse(recordObject.toString()).getAsJsonObject();
		Set<Map.Entry<String, JsonElement>> entrySet = recordGson.entrySet();
		Integer ticketDetailID = null;

		//get values
		{
			Iterator<Map.Entry<String, JsonElement>> iterator = entrySet.iterator();
			while(iterator.hasNext()) {
				Map.Entry<String, JsonElement> entry = iterator.next();
				if(entry.getKey().equals("metadata")) {
					//do nothing
				} else {
					JsonPrimitive value = entry.getValue().getAsJsonPrimitive();
					//System.out.println(entry.getKey()+" : "+value.getAsString());
					if(value.isString()) {
						if(!valuesPortion.equals("")) {
							valuesPortion+=",";
						}
						//System.out.println(entry.getKey()+" : '"+value.getAsString()+"'");
						valuesPortion += entry.getKey()+"=";
						valuesPortion += "'"+value.getAsString()+"'";
					} else if(value.isNumber()) {
						if(value.getAsString().contains(".")) {
							if(!valuesPortion.equals("")) {
								valuesPortion+=",";
							}
							//System.out.println(entry.getKey()+" : "+(new Double(value.getAsDouble()).toString()));
							valuesPortion += entry.getKey()+"=";
							valuesPortion += new Double(value.getAsDouble()).toString();
						} else {
							Long val = new Long(value.getAsLong());
							if(entry.getKey().equals(DbSingleton.TicketDetailSchema.COLUMN_TICKET_DETAIL_ID)) {
								ticketDetailID = val.intValue();
							} else {
								if(!valuesPortion.equals("")) {
									valuesPortion+=",";
								}
								//System.out.println(entry.getKey()+" : "+(new Long(value.getAsLong()).toString()));
								valuesPortion += entry.getKey()+"=";
								//String ticketNumber = new Long(value.getAsLong()).toString();
								valuesPortion += val.toString();
							}
						}
					} else if(value.isBoolean()) {
						if(!valuesPortion.equals("")) {
							valuesPortion+=",";
						}
						//System.out.println(entry.getKey()+" : "+(new Boolean(value.getAsBoolean()).toString()));
						if(value.getAsBoolean()) {
							valuesPortion += entry.getKey()+"=";
							valuesPortion += "1";
						} else {
							valuesPortion += entry.getKey()+"=";
							valuesPortion += "0";
						}
					}
				}
			}
		}

		//String insertQuery = "INSERT INTO "+DbSingleton.TICKET_DETAIL_TABLE_NAME+"("+columnsPortion+") VALUES ("+valuesPortion+")";
		if(ticketDetailID==null) {
			throw new SQLException("no ticket detail id found!");
		}
		String updateQuery = "UPDATE "+DbSingleton.TICKET_DETAIL_TABLE_NAME+" SET "+valuesPortion+" WHERE "+DbSingleton.TicketDetailSchema.COLUMN_TICKET_DETAIL_ID+"="+ticketDetailID;
		//String identQuery = "SELECT IDENT_CURRENT ('"+DbSingleton.TICKET_DETAIL_TABLE_NAME+"') AS TicketDetailID";

		executeQuery(updateQuery);
	}

	public void calculateTicketDetail(Integer ticketID, JSONObject recordObject) throws SQLException, JSONException {
		//first, make sure we have a valid product
		String productQuery = "SELECT * FROM "+DbSingleton.PRODUCT_TABLE_NAME + " WHERE "+DbSingleton.ProductSchema.COLUMN_ID+"='"+recordObject.getString(DbSingleton.ProductSchema.COLUMN_ID)+"'";
		ResultSet productResultSet = executeQuery(productQuery);
		productResultSet.last();
		int rowCount = productResultSet.getRow();
		productResultSet.first();
		String productID = null;
		Double factor = null;
		if(rowCount<1) {
			throw new SQLException("no products defined!");
		} else {
			do {
				if(recordObject.getString(DbSingleton.TicketDetailSchema.COLUMN_PRODUCT_ID).equals(productResultSet.getString(DbSingleton.ProductSchema.COLUMN_ID))) {
					productID = productResultSet.getString(DbSingleton.ProductSchema.COLUMN_ID);
					factor = productResultSet.getDouble(DbSingleton.ProductSchema.COLUMN_FACTOR);
					break;
				}
			} while(productResultSet.next());
			if(productID==null) {
				throw new SQLException("no such product id");
			} else {
				recordObject.remove(DbSingleton.TicketDetailSchema.COLUMN_PRODUCT_NAME);
				recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_PRODUCT_NAME, productResultSet.getString(DbSingleton.ProductSchema.COLUMN_NAME));
				recordObject.remove(DbSingleton.TicketDetailSchema.COLUMN_INVENTORY_NAME);
				recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_INVENTORY_NAME, productResultSet.getString(DbSingleton.ProductSchema.COLUMN_INVENTORY_NAME));
				recordObject.remove(DbSingleton.TicketDetailSchema.COLUMN_PRICE_PER_UOM);
				recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_PRICE_PER_UOM, productResultSet.getString(DbSingleton.ProductSchema.COLUMN_T1_PRICE));
				recordObject.remove(DbSingleton.TicketDetailSchema.COLUMN_UOM);
				recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_UOM, productResultSet.getString(DbSingleton.ProductSchema.COLUMN_UOM));
				recordObject.remove(DbSingleton.TicketDetailSchema.COLUMN_FACTOR);
				recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_FACTOR, productResultSet.getString(DbSingleton.ProductSchema.COLUMN_FACTOR));
				recordObject.remove(DbSingleton.TicketDetailSchema.COLUMN_PRODUCT_TAXABLE);
				recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_PRODUCT_TAXABLE, (productResultSet.getBoolean(DbSingleton.ProductSchema.COLUMN_PRODUCT_TAXABLE))?1:0);
			}
		}

		String uom = recordObject.getString(DbSingleton.TicketDetailSchema.COLUMN_UOM);
		if(uom.equals("CUYD") || uom.equals("EACH")) {
			//grab units
			recordObject.remove(DbSingleton.TicketDetailSchema.COLUMN_NET_LBS);
			recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_NET_LBS, recordObject.getDouble(DbSingleton.TicketDetailSchema.COLUMN_UNITS));
		} else {
			//grab net weight
			if(recordObject.getDouble(DbSingleton.TicketDetailSchema.COLUMN_GROSS_LBS) < recordObject.getDouble(DbSingleton.TicketDetailSchema.COLUMN_TARE_LBS)) {
				recordObject.remove(DbSingleton.TicketDetailSchema.COLUMN_NET_LBS);
				Double net = recordObject.getDouble(DbSingleton.TicketDetailSchema.COLUMN_TARE_LBS) - recordObject.getDouble(DbSingleton.TicketDetailSchema.COLUMN_GROSS_LBS) - recordObject.getDouble(DbSingleton.TicketDetailSchema.COLUMN_DEDUCT_LBS);
				recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_NET_LBS, net);
			}
			recordObject.remove(DbSingleton.TicketDetailSchema.COLUMN_UNITS);
			if(factor!=0) {
				recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_UNITS, recordObject.getDouble(DbSingleton.TicketDetailSchema.COLUMN_NET_LBS)/factor);
			} else {
				recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_UNITS, 0);
			}
		}

		recordObject.remove(DbSingleton.TicketDetailSchema.COLUMN_UNIT_TOTAL);
		recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_UNIT_TOTAL, recordObject.getDouble(DbSingleton.TicketDetailSchema.COLUMN_UNITS)*recordObject.getDouble(DbSingleton.TicketDetailSchema.COLUMN_PRICE_PER_UOM));

		if(productResultSet.getDouble(DbSingleton.ProductSchema.COLUMN_MIN_COST) > 0) {
			if(recordObject.getDouble(DbSingleton.TicketDetailSchema.COLUMN_UNITS) < productResultSet.getDouble(DbSingleton.ProductSchema.COLUMN_MIN_COST) &&
					recordObject.getDouble(DbSingleton.TicketDetailSchema.COLUMN_UNITS) > 0) {
				recordObject.remove(DbSingleton.TicketDetailSchema.COLUMN_UNIT_TOTAL);
				recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_UNIT_TOTAL, recordObject.getDouble(DbSingleton.TicketDetailSchema.COLUMN_MIN_COST));
				recordObject.remove(DbSingleton.TicketDetailSchema.COLUMN_PRICE_PER_UOM);
				recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_PRICE_PER_UOM, recordObject.getDouble(DbSingleton.TicketDetailSchema.COLUMN_UNIT_TOTAL)/recordObject.getDouble(DbSingleton.TicketDetailSchema.COLUMN_UNITS));
			}
		}

		//String ticketQuery = "SELECT * FROM "+DbSingleton.TICKET_TABLE_NAME+" WHERE "+DbSingleton.TicketSchema.COLUMN_TICKET_ID+"="+recordObject.getInt(DbSingleton.TicketDetailSchema.COLUMN_TICKET_ID);
		String ticketQuery = "SELECT * FROM "+DbSingleton.TICKET_TABLE_NAME+" WHERE "+DbSingleton.TicketSchema.COLUMN_TICKET_ID+"="+ticketID;
		ResultSet ticketResultSet = executeQuery(ticketQuery);
		ticketResultSet.first();

		String query = "SELECT * FROM "+DbSingleton.CUSTOMER_TABLE_NAME+" WHERE "+DbSingleton.CustomerSchema.COLUMN_ID+"='"+ticketResultSet.getString(DbSingleton.TicketSchema.COLUMN_CUSTOMER_ID)+"'";
		ResultSet customerResultSet = executeQuery(query);
		customerResultSet.last();
		rowCount = customerResultSet.getRow();
		customerResultSet.first();
		if(customerResultSet.getBoolean(DbSingleton.CustomerSchema.COLUMN_TAXABLE)) {
			if(productResultSet.getBoolean(DbSingleton.ProductSchema.COLUMN_PRODUCT_TAXABLE)) {
				String iniQuery = "SELECT * FROM INISetting WHERE INIKey=TaxRate";
				ResultSet iniResultSet = executeQuery(iniQuery);
				iniResultSet.last();
				rowCount = iniResultSet.getRow();
				iniResultSet.first();
				if(rowCount<1) {
					throw new SQLException("could not get tax info for a taxable item!");
				} else {
					Double taxRate = iniResultSet.getDouble("INIValue");
					recordObject.remove(DbSingleton.TicketDetailSchema.COLUMN_UNIT_TAX);
					recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_UNIT_TAX, recordObject.getDouble(DbSingleton.TicketDetailSchema.COLUMN_NET_LBS)*taxRate);
				}
			}
		}

		recordObject.remove(DbSingleton.TicketDetailSchema.COLUMN_TOTAL);
		recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_TOTAL, recordObject.getDouble(DbSingleton.TicketDetailSchema.COLUMN_UNIT_TOTAL) + recordObject.getDouble(DbSingleton.TicketDetailSchema.COLUMN_UNIT_TAX));
	}

	public int newTicketDetail(Integer ticketID, JSONObject recordObject) throws SQLException, JSONException {

		recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_TIER, "T1");
		recordObject.remove(DbSingleton.TicketSchema.COLUMN_TMS_ROW_GUID);
		recordObject.remove(DbSingleton.TicketDetailSchema.COLUMN_TICKET_ID);
		recordObject.remove(DbSingleton.TicketDetailSchema.COLUMN_TICKET_DETAIL_ID);
		recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_TICKET_ID, ticketID);
		recordObject.remove(DbSingleton.TicketDetailSchema.COLUMN_RECONCILED);
		recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_RECONCILED, 0);
		recordObject.remove(DbSingleton.TicketDetailSchema.COLUMN_INVENTORIED);
		recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_INVENTORIED, 0);
		recordObject.remove(DbSingleton.TicketDetailSchema.COLUMN_INVOICE_NUMBER_REF);
		recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_INVOICE_NUMBER_REF, 0);
		recordObject.remove(DbSingleton.TicketDetailSchema.COLUMN_LAST_MODIFIED);
		recordObject.remove(DbSingleton.TicketDetailSchema.COLUMN_KIT_ID);
		recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_KIT_ID, 0);
		recordObject.remove(DbSingleton.TicketDetailSchema.COLUMN_TIER_CHANGED_BY_USER);
		recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_TIER_CHANGED_BY_USER, 0);
		recordObject.remove(DbSingleton.TicketDetailSchema.COLUMN_PRICE_PER_UOM_CHANGED_BY_USER);
		recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_PRICE_PER_UOM_CHANGED_BY_USER, 0);

		//format the datetime values in a way that ms sql will accept.
		String grossDT = null;
		String tareDT = null;
		String deductDT = null;
		String altGrossDT = null;
		String altTareDT = null;
		String altDeductDT = null;
		try {
			grossDT = mDateFormat.format(new java.util.Date(recordObject.getLong(DbSingleton.TicketDetailSchema.COLUMN_GROSS_LBS_DT)));
			tareDT = mDateFormat.format(new java.util.Date(recordObject.getLong(DbSingleton.TicketDetailSchema.COLUMN_TARE_LBS_DT)));
			deductDT = mDateFormat.format(new java.util.Date(recordObject.getLong(DbSingleton.TicketDetailSchema.COLUMN_DEDUCT_LBS_DT)));
			altGrossDT = mDateFormat.format(new java.util.Date(recordObject.getLong(DbSingleton.TicketDetailSchema.COLUMN_ALT_GROSS_LBS_DT)));
			altTareDT = mDateFormat.format(new java.util.Date(recordObject.getLong(DbSingleton.TicketDetailSchema.COLUMN_ALT_TARE_LBS_DT)));
			altDeductDT = mDateFormat.format(new java.util.Date(recordObject.getLong(DbSingleton.TicketDetailSchema.COLUMN_ALT_DEDUCT_LBS_DT)));
		} catch(JSONException jsone) {
			grossDT = recordObject.getString(DbSingleton.TicketDetailSchema.COLUMN_GROSS_LBS_DT);
			tareDT = recordObject.getString(DbSingleton.TicketDetailSchema.COLUMN_TARE_LBS_DT);
			deductDT = recordObject.getString(DbSingleton.TicketDetailSchema.COLUMN_DEDUCT_LBS_DT);
			altGrossDT = recordObject.getString(DbSingleton.TicketDetailSchema.COLUMN_ALT_GROSS_LBS_DT);
			altTareDT = recordObject.getString(DbSingleton.TicketDetailSchema.COLUMN_ALT_TARE_LBS_DT);
			altDeductDT = recordObject.getString(DbSingleton.TicketDetailSchema.COLUMN_ALT_DEDUCT_LBS_DT);
		}

		recordObject.remove(DbSingleton.TicketDetailSchema.COLUMN_GROSS_LBS_DT);
		recordObject.remove(DbSingleton.TicketDetailSchema.COLUMN_TARE_LBS_DT);
		recordObject.remove(DbSingleton.TicketDetailSchema.COLUMN_DEDUCT_LBS_DT);
		recordObject.remove(DbSingleton.TicketDetailSchema.COLUMN_ALT_GROSS_LBS_DT);
		recordObject.remove(DbSingleton.TicketDetailSchema.COLUMN_ALT_TARE_LBS_DT);
		recordObject.remove(DbSingleton.TicketDetailSchema.COLUMN_ALT_DEDUCT_LBS_DT);
		recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_GROSS_LBS_DT, grossDT);
		recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_TARE_LBS_DT, tareDT);
		recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_DEDUCT_LBS_DT, deductDT);
		recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_ALT_GROSS_LBS_DT, altGrossDT);
		recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_ALT_TARE_LBS_DT, altTareDT);
		recordObject.put(DbSingleton.TicketDetailSchema.COLUMN_ALT_DEDUCT_LBS_DT, altDeductDT);

		calculateTicketDetail(ticketID, recordObject);

		return insertNewTicketDetail(recordObject);
	}

	public int insertNewTicketDetail(JSONObject recordObject) throws SQLException, JSONException {
		String valuesPortion = "";
		String columnsPortion = "";

		//recordObject.remove(DbSingleton.TicketSchema.COLUMN_TICKET_NUMBER);
		//recordObject.put(DbSingleton.TicketSchema.COLUMN_TICKET_NUMBER, getNextTicketNumber());

		//get column names
		JsonParser parser = new JsonParser();
		JsonObject recordGson = parser.parse(recordObject.toString()).getAsJsonObject();
		Set<Map.Entry<String, JsonElement>> entrySet = recordGson.entrySet();
		{
			Iterator<Map.Entry<String, JsonElement>> iterator = entrySet.iterator();
			while(iterator.hasNext()) {
				Map.Entry<String, JsonElement> entry = iterator.next();
				if(entry.getKey().equals("metadata")) {
					//do nothing
				} else {
					JsonPrimitive value = entry.getValue().getAsJsonPrimitive();
					//if(value.isString() && value.getAsString().equals("")) {
						//don't want this null value/key pair
					//} else {
					if(!columnsPortion.equals("")) {
						columnsPortion+=",";
					}
					columnsPortion+=entry.getKey();
						//System.out.println(entry.getKey()+" : "+value.getAsString());
					//}
				}
			}
		}

		//get values
		{
			Iterator<Map.Entry<String, JsonElement>> iterator = entrySet.iterator();
			while(iterator.hasNext()) {
				Map.Entry<String, JsonElement> entry = iterator.next();
				if(entry.getKey().equals("metadata")) {
					//do nothing
				} else {
					JsonPrimitive value = entry.getValue().getAsJsonPrimitive();
					//System.out.println(entry.getKey()+" : "+value.getAsString());
					if(value.isString()) {
						if(!valuesPortion.equals("")) {
							valuesPortion+=",";
						}
						//System.out.println(entry.getKey()+" : '"+value.getAsString()+"'");
						valuesPortion += "'"+value.getAsString()+"'";
					} else if(value.isNumber()) {
						if(value.getAsString().contains(".")) {
							if(!valuesPortion.equals("")) {
								valuesPortion+=",";
							}
							//System.out.println(entry.getKey()+" : "+(new Double(value.getAsDouble()).toString()));
							valuesPortion += new Double(value.getAsDouble()).toString();
						} else {
							if(!valuesPortion.equals("")) {
								valuesPortion+=",";
							}
							//System.out.println(entry.getKey()+" : "+(new Long(value.getAsLong()).toString()));
							String ticketNumber = new Long(value.getAsLong()).toString();
							valuesPortion += ticketNumber;
						}
					} else if(value.isBoolean()) {
						if(!valuesPortion.equals("")) {
							valuesPortion+=",";
						}
						//System.out.println(entry.getKey()+" : "+(new Boolean(value.getAsBoolean()).toString()));
						if(value.getAsBoolean())
							valuesPortion += "1";
						else
							valuesPortion += "0";
					}
				}
			}
		}

		String insertQuery = "INSERT INTO "+DbSingleton.TICKET_DETAIL_TABLE_NAME+"("+columnsPortion+") VALUES ("+valuesPortion+")";
		String identQuery = "SELECT IDENT_CURRENT ('"+DbSingleton.TICKET_DETAIL_TABLE_NAME+"') AS TicketDetailID";

		executeQuery(insertQuery);
		ResultSet identResultSet = executeQuery(identQuery);
		identResultSet.first();
		//System.out.println(identResultSet.getRow());
		int ticketDetailID = identResultSet.getInt(1);
		//System.out.println(ticketID);

		return ticketDetailID;
	}

	public int newTicket(JSONObject recordObject) throws SQLException, JSONException {
		String ticketDate = null;
		try {
			ticketDate = mDateFormat.format(new java.util.Date(recordObject.getLong(DbSingleton.TicketSchema.COLUMN_TICKET_DATE)));
		} catch(JSONException jsone) {
			ticketDate = recordObject.getString(DbSingleton.TicketSchema.COLUMN_TICKET_DATE);
		}

		if(recordObject.getString(DbSingleton.TicketSchema.COLUMN_SIGNATURE_PICTURE).equals("")) {
			recordObject.remove(DbSingleton.TicketSchema.COLUMN_SIGNATURE_PICTURE);
		}
		recordObject.remove(DbSingleton.TicketSchema.COLUMN_TMS_ROW_GUID);
		recordObject.remove(DbSingleton.TicketSchema.COLUMN_TICKET_ID);
		recordObject.remove(DbSingleton.TicketSchema.COLUMN_TICKET_DATE);
		recordObject.remove(DbSingleton.TicketSchema.COLUMN_CHECK_NUMBER);
		recordObject.remove(DbSingleton.TicketSchema.COLUMN_VOIDED);
		recordObject.remove(DbSingleton.TicketSchema.COLUMN_COMPLETED);
		recordObject.remove(DbSingleton.TicketSchema.COLUMN_INVOICE_CLEARED);
		recordObject.remove(DbSingleton.TicketSchema.COLUMN_TICKET_TYPE);
		recordObject.remove(DbSingleton.TicketSchema.COLUMN_SITE);
		recordObject.remove(DbSingleton.TicketSchema.COLUMN_STATION_ID);
		recordObject.remove(DbSingleton.TicketSchema.COLUMN_INVOICE_NUMBER);
		recordObject.remove(DbSingleton.TicketSchema.COLUMN_INVOICE_DATE);
		recordObject.remove(DbSingleton.TicketSchema.COLUMN_LAST_MODIFIED);
		recordObject.remove(DbSingleton.TicketSchema.COLUMN_VOUCHER_REDEEMABLE_DATE);
		recordObject.remove(DbSingleton.TicketSchema.COLUMN_VOUCHER_EXPIRATION_DATE);
		recordObject.remove(DbSingleton.TicketSchema.COLUMN_HOLD_CHECK);
		recordObject.remove(DbSingleton.TicketSchema.COLUMN_PAYMENT_DATE);
		recordObject.remove(DbSingleton.TicketSchema.COLUMN_INDUSTRIAL);
		recordObject.put(DbSingleton.TicketSchema.COLUMN_INDUSTRIAL, 0);
		recordObject.put(DbSingleton.TicketSchema.COLUMN_CHECK_NUMBER, 0);
		recordObject.put(DbSingleton.TicketSchema.COLUMN_VOIDED, 0);
		recordObject.put(DbSingleton.TicketSchema.COLUMN_COMPLETED, 0);
		recordObject.put(DbSingleton.TicketSchema.COLUMN_INVOICE_CLEARED, 0);
		recordObject.put(DbSingleton.TicketSchema.COLUMN_TICKET_TYPE, "S");
		recordObject.put(DbSingleton.TicketSchema.COLUMN_SITE, "SITE1");
		recordObject.put(DbSingleton.TicketSchema.COLUMN_STATION_ID, 1);
		recordObject.put(DbSingleton.TicketSchema.COLUMN_INVOICE_DATE, ticketDate);
		recordObject.put(DbSingleton.TicketSchema.COLUMN_VOUCHER_REDEEMABLE_DATE, ticketDate);
		recordObject.put(DbSingleton.TicketSchema.COLUMN_VOUCHER_EXPIRATION_DATE, ticketDate);
		recordObject.put(DbSingleton.TicketSchema.COLUMN_PAYMENT_DATE, ticketDate);
		recordObject.put(DbSingleton.TicketSchema.COLUMN_TICKET_DATE, ticketDate);
		recordObject.put(DbSingleton.TicketSchema.COLUMN_HOLD_CHECK, 0);
		if(recordObject.getBoolean(DbSingleton.TicketSchema.COLUMN_PENDING)) {
			recordObject.remove(DbSingleton.TicketSchema.COLUMN_PENDING);
			recordObject.put(DbSingleton.TicketSchema.COLUMN_PENDING, 1);
		} else {
			recordObject.remove(DbSingleton.TicketSchema.COLUMN_PENDING);
			recordObject.put(DbSingleton.TicketSchema.COLUMN_PENDING, 0);
		}
		if(recordObject.getBoolean(DbSingleton.TicketSchema.COLUMN_TICKET_PRINTED)) {
			recordObject.remove(DbSingleton.TicketSchema.COLUMN_TICKET_PRINTED);
			recordObject.put(DbSingleton.TicketSchema.COLUMN_TICKET_PRINTED, 1);
		} else {
			recordObject.remove(DbSingleton.TicketSchema.COLUMN_TICKET_PRINTED);
			recordObject.put(DbSingleton.TicketSchema.COLUMN_TICKET_PRINTED, 0);
		}

		if(!recordObject.getString(DbSingleton.TicketSchema.COLUMN_DIRECTION).equals("O") || !recordObject.getString(DbSingleton.TicketSchema.COLUMN_DIRECTION).equals("I")) {
			recordObject.remove(DbSingleton.TicketSchema.COLUMN_DIRECTION);
			recordObject.put(DbSingleton.TicketSchema.COLUMN_DIRECTION, "I");
		}

		/////////////////CUSTOMER SCOPE: anything that requires customer table is done in here.
		String query = "SELECT * FROM "+DbSingleton.CUSTOMER_TABLE_NAME+" WHERE "+DbSingleton.CustomerSchema.COLUMN_ID+"='"+recordObject.getString(DbSingleton.TicketSchema.COLUMN_CUSTOMER_ID)+"'";
		ResultSet customerResultSet = executeQuery(query);
		customerResultSet.last();
		int rowCount = customerResultSet.getRow();
		customerResultSet.first();
		if(rowCount<1)
			throw new SQLException(LOG_TAG+": no such customer id!");
		/*
		int rowCount = 0;
		try {
			rowCount = java.lang.reflect.Array.getLength(customerResultSet.getArray(0).getArray());
		} catch(SQLException sqle) { }
		if(rowCount<1)
			throw new SQLException(LOG_TAG+": no such customer id!");
		*/
		if(recordObject.getString(DbSingleton.TicketSchema.COLUMN_CUSTOMER_NAME)==null || recordObject.getString(DbSingleton.TicketSchema.COLUMN_CUSTOMER_NAME).equals("")) {
			recordObject.remove(DbSingleton.TicketSchema.COLUMN_CUSTOMER_NAME);
			recordObject.put(DbSingleton.TicketSchema.COLUMN_CUSTOMER_NAME, customerResultSet.getString(DbSingleton.CustomerSchema.COLUMN_NAME));
		}
		if(recordObject.getString(DbSingleton.TicketSchema.COLUMN_CUSTOMER_ADDRESS)==null || recordObject.getString(DbSingleton.TicketSchema.COLUMN_CUSTOMER_ADDRESS).equals("")) {
			recordObject.remove(DbSingleton.TicketSchema.COLUMN_CUSTOMER_ADDRESS);
			recordObject.put(DbSingleton.TicketSchema.COLUMN_CUSTOMER_ADDRESS, customerResultSet.getString(DbSingleton.CustomerSchema.COLUMN_ADDRESS));
		}
		if(recordObject.getString(DbSingleton.TicketSchema.COLUMN_CUSTOMER_CITY)==null || recordObject.getString(DbSingleton.TicketSchema.COLUMN_CUSTOMER_CITY).equals("")) {
			recordObject.remove(DbSingleton.TicketSchema.COLUMN_CUSTOMER_CITY);
			recordObject.put(DbSingleton.TicketSchema.COLUMN_CUSTOMER_CITY, customerResultSet.getString(DbSingleton.CustomerSchema.COLUMN_CITY));
		}
		if(recordObject.getString(DbSingleton.TicketSchema.COLUMN_CUSTOMER_STATE)==null || recordObject.getString(DbSingleton.TicketSchema.COLUMN_CUSTOMER_STATE).equals("")) {
			recordObject.remove(DbSingleton.TicketSchema.COLUMN_CUSTOMER_STATE);
			recordObject.put(DbSingleton.TicketSchema.COLUMN_CUSTOMER_STATE, customerResultSet.getString(DbSingleton.CustomerSchema.COLUMN_STATE));
		}
		if(recordObject.getString(DbSingleton.TicketSchema.COLUMN_CUSTOMER_ZIP)==null || recordObject.getString(DbSingleton.TicketSchema.COLUMN_CUSTOMER_ZIP).equals("")) {
			recordObject.remove(DbSingleton.TicketSchema.COLUMN_CUSTOMER_ZIP);
			recordObject.put(DbSingleton.TicketSchema.COLUMN_CUSTOMER_ZIP, customerResultSet.getString(DbSingleton.CustomerSchema.COLUMN_ZIP));
		}
		if(recordObject.getString(DbSingleton.TicketSchema.COLUMN_CUSTOMER_ACCOUNT_TYPE)==null || recordObject.getString(DbSingleton.TicketSchema.COLUMN_CUSTOMER_ACCOUNT_TYPE).equals("")) {
			recordObject.remove(DbSingleton.TicketSchema.COLUMN_CUSTOMER_ACCOUNT_TYPE);
			recordObject.put(DbSingleton.TicketSchema.COLUMN_CUSTOMER_ACCOUNT_TYPE, customerResultSet.getString(DbSingleton.CustomerSchema.COLUMN_ACCOUNT_TYPE));
		}
		if(recordObject.getString(DbSingleton.TicketSchema.COLUMN_CUSTOMER_TYPE_CODE)==null || recordObject.getString(DbSingleton.TicketSchema.COLUMN_CUSTOMER_TYPE_CODE).equals("")) {
			recordObject.remove(DbSingleton.TicketSchema.COLUMN_CUSTOMER_TYPE_CODE);
			recordObject.put(DbSingleton.TicketSchema.COLUMN_CUSTOMER_TYPE_CODE, customerResultSet.getString(DbSingleton.CustomerSchema.COLUMN_TYPE_CODE));
		}
		if(recordObject.getString(DbSingleton.TicketSchema.COLUMN_COUNTY)==null || recordObject.getString(DbSingleton.TicketSchema.COLUMN_COUNTY).equals("")) {
			recordObject.remove(DbSingleton.TicketSchema.COLUMN_COUNTY);
			recordObject.put(DbSingleton.TicketSchema.COLUMN_COUNTY, customerResultSet.getString(DbSingleton.CustomerSchema.COLUMN_COUNTY));
		}
		if(recordObject.getString(DbSingleton.TicketSchema.COLUMN_BUSINESS_NAME_ID)==null || recordObject.getString(DbSingleton.TicketSchema.COLUMN_BUSINESS_NAME_ID).equals("")) {
			recordObject.remove(DbSingleton.TicketSchema.COLUMN_BUSINESS_NAME_ID);
			recordObject.put(DbSingleton.TicketSchema.COLUMN_BUSINESS_NAME_ID, customerResultSet.getString(DbSingleton.CustomerSchema.COLUMN_BUSINESS_NAME_ID));
		}
		if(recordObject.getString(DbSingleton.TicketSchema.COLUMN_DEALER_LICENSE_NUMBER)==null || recordObject.getString(DbSingleton.TicketSchema.COLUMN_DEALER_LICENSE_NUMBER).equals("")) {
			recordObject.remove(DbSingleton.TicketSchema.COLUMN_DEALER_LICENSE_NUMBER);
			recordObject.put(DbSingleton.TicketSchema.COLUMN_DEALER_LICENSE_NUMBER, customerResultSet.getString(DbSingleton.CustomerSchema.COLUMN_DEALER_LICENSE_NUMBER));
		}
		int reconciliationType = customerResultSet.getInt(DbSingleton.CustomerSchema.COLUMN_RECONCILIATION_TYPE);
		switch(reconciliationType) {
			case 0:
				recordObject.put(DbSingleton.TicketSchema.COLUMN_RECONCILIATION_TYPE, reconciliationType);
				break;
			case 1:
				if(recordObject.getString(DbSingleton.TicketSchema.COLUMN_DIRECTION).equals("I"))
					recordObject.put(DbSingleton.TicketSchema.COLUMN_RECONCILIATION_TYPE, reconciliationType);
				else
					recordObject.put(DbSingleton.TicketSchema.COLUMN_RECONCILIATION_TYPE, 0);
				break;
			case 2:
				if(recordObject.getString(DbSingleton.TicketSchema.COLUMN_DIRECTION).equals("O"))
					recordObject.put(DbSingleton.TicketSchema.COLUMN_RECONCILIATION_TYPE, reconciliationType);
				else
					recordObject.put(DbSingleton.TicketSchema.COLUMN_RECONCILIATION_TYPE, 0);
				break;
			case 3:
				if(recordObject.getString(DbSingleton.TicketSchema.COLUMN_DIRECTION).equals("I"))
					recordObject.put(DbSingleton.TicketSchema.COLUMN_RECONCILIATION_TYPE, 1);
				else
					recordObject.put(DbSingleton.TicketSchema.COLUMN_RECONCILIATION_TYPE, 2);
				break;
			default:
				throw new SQLException("unknown reconciliation type!  this shouldn't even be possible.  how did you do this?  please call support.");
		}
		/*
		if(reconciliationType==null || recordObject.getString(DbSingleton.TicketSchema.COLUMN_RECONCILIATION_TYPE).equals("")) {
			recordObject.remove(DbSingleton.TicketSchema.COLUMN_RECONCILIATION_TYPE);
			recordObject.put(DbSingleton.TicketSchema.COLUMN_RECONCILIATION_TYPE, customerResultSet.getInt(DbSingleton.CustomerSchema.COLUMN_RECONCILIATION_TYPE));
			if()
		}
		*/
		recordObject.remove(DbSingleton.TicketSchema.COLUMN_CUSTOMER_TAXABLE);
		if(customerResultSet.getBoolean(DbSingleton.CustomerSchema.COLUMN_TAXABLE))
			recordObject.put(DbSingleton.TicketSchema.COLUMN_CUSTOMER_TAXABLE, 1);
		else
			recordObject.put(DbSingleton.TicketSchema.COLUMN_CUSTOMER_TAXABLE, 0);
		recordObject.remove(DbSingleton.TicketSchema.COLUMN_VALID_ADDRESS);
		if(customerResultSet.getBoolean(DbSingleton.CustomerSchema.COLUMN_VALID_ADDRESS))
			recordObject.put(DbSingleton.TicketSchema.COLUMN_VALID_ADDRESS, 1);
		else
			recordObject.put(DbSingleton.TicketSchema.COLUMN_VALID_ADDRESS, 0);
		recordObject.remove(DbSingleton.TicketSchema.COLUMN_CUSTOMER_INVOICEABLE);
		if(customerResultSet.getBoolean(DbSingleton.CustomerSchema.COLUMN_INVOICEABLE)) {
			recordObject.put(DbSingleton.TicketSchema.COLUMN_CUSTOMER_INVOICEABLE, 1);
			recordObject.put(DbSingleton.TicketSchema.COLUMN_INVOICE_NUMBER, 0);	//TODO: CustomerAccountType: -1 for CASH|CASHONLY, 0 for CHARGE
		} else {
			recordObject.put(DbSingleton.TicketSchema.COLUMN_CUSTOMER_INVOICEABLE, 0);
			recordObject.put(DbSingleton.TicketSchema.COLUMN_INVOICE_NUMBER, -1);	//TODO: CustomerAccountType: -1 for CASH|CASHONLY, 0 for CHARGE
		}
		recordObject.remove(DbSingleton.TicketSchema.COLUMN_REGISTERED);
		if(customerResultSet.getBoolean(DbSingleton.CustomerSchema.COLUMN_REGISTERED))
			recordObject.put(DbSingleton.TicketSchema.COLUMN_REGISTERED, 1);
		else
			recordObject.put(DbSingleton.TicketSchema.COLUMN_REGISTERED, 0);
		//////////////////

		//////////////////HAULER
		//NOTE: the code below is for mobile 2 only, since customer is always hauler
		if(recordObject.getString(DbSingleton.TicketSchema.COLUMN_HAULER_ID)==null || recordObject.getString(DbSingleton.TicketSchema.COLUMN_HAULER_ID).equals("")) {
			if(customerResultSet.getString(DbSingleton.CustomerSchema.COLUMN_DEFAULT_HAULER_ID)==null || customerResultSet.getString(DbSingleton.CustomerSchema.COLUMN_DEFAULT_HAULER_ID).equals("")) {
				recordObject.remove(DbSingleton.TicketSchema.COLUMN_HAULER_ID);
				recordObject.put(DbSingleton.TicketSchema.COLUMN_HAULER_ID, "CASH");
			} else {
				recordObject.remove(DbSingleton.TicketSchema.COLUMN_HAULER_ID);
				recordObject.put(DbSingleton.TicketSchema.COLUMN_HAULER_ID, customerResultSet.getString(DbSingleton.CustomerSchema.COLUMN_DEFAULT_HAULER_ID));
			}
		} else if(!customerResultSet.getString(DbSingleton.CustomerSchema.COLUMN_REQUIRE_HAULER_ID).equals("0")) {
			if(recordObject.getString(DbSingleton.TicketSchema.COLUMN_HAULER_ID)==null || recordObject.getString(DbSingleton.TicketSchema.COLUMN_HAULER_ID).equals("")) {
				throw new SQLException("HAULER IS A REQUIRED FIELD!");
			}
		}
		String haulerQuery = "SELECT * FROM Hauler WHERE HaulerID='"+recordObject.getString(DbSingleton.TicketSchema.COLUMN_HAULER_ID)+"'";
		ResultSet haulerResultSet = executeQuery(haulerQuery);
		haulerResultSet.last();
		rowCount = haulerResultSet.getRow();
		haulerResultSet.first();
		if(rowCount>0) {
			recordObject.remove(DbSingleton.TicketSchema.COLUMN_HAULER_NAME);
			recordObject.put(DbSingleton.TicketSchema.COLUMN_HAULER_NAME, haulerResultSet.getString("HaulerName"));
			recordObject.remove(DbSingleton.TicketSchema.COLUMN_HAULER_ADDRESS);
			recordObject.put(DbSingleton.TicketSchema.COLUMN_HAULER_ADDRESS, haulerResultSet.getString("HaulerAddress"));
			recordObject.remove(DbSingleton.TicketSchema.COLUMN_HAULER_CITY);
			recordObject.put(DbSingleton.TicketSchema.COLUMN_HAULER_CITY, haulerResultSet.getString("HaulerCity"));
			recordObject.remove(DbSingleton.TicketSchema.COLUMN_HAULER_STATE);
			recordObject.put(DbSingleton.TicketSchema.COLUMN_HAULER_STATE, haulerResultSet.getString("HaulerState"));
			recordObject.remove(DbSingleton.TicketSchema.COLUMN_HAULER_ZIP);
			recordObject.put(DbSingleton.TicketSchema.COLUMN_HAULER_ZIP, haulerResultSet.getString("HaulerZip"));
		} else {
			haulerQuery = "SELECT * FROM Hauler WHERE HaulerID='CASH'";
			haulerResultSet = executeQuery(haulerQuery);
			haulerResultSet.last();
			rowCount = haulerResultSet.getRow();
			haulerResultSet.first();
			if(rowCount>0) {
				recordObject.remove(DbSingleton.TicketSchema.COLUMN_HAULER_NAME);
				recordObject.put(DbSingleton.TicketSchema.COLUMN_HAULER_NAME, haulerResultSet.getString("HaulerName"));
				recordObject.remove(DbSingleton.TicketSchema.COLUMN_HAULER_ADDRESS);
				recordObject.put(DbSingleton.TicketSchema.COLUMN_HAULER_ADDRESS, haulerResultSet.getString("HaulerAddress"));
				recordObject.remove(DbSingleton.TicketSchema.COLUMN_HAULER_CITY);
				recordObject.put(DbSingleton.TicketSchema.COLUMN_HAULER_CITY, haulerResultSet.getString("HaulerCity"));
				recordObject.remove(DbSingleton.TicketSchema.COLUMN_HAULER_STATE);
				recordObject.put(DbSingleton.TicketSchema.COLUMN_HAULER_STATE, haulerResultSet.getString("HaulerState"));
				recordObject.remove(DbSingleton.TicketSchema.COLUMN_HAULER_ZIP);
				recordObject.put(DbSingleton.TicketSchema.COLUMN_HAULER_ZIP, haulerResultSet.getString("HaulerZip"));
			} else {
				if(customerResultSet.getString(DbSingleton.CustomerSchema.COLUMN_REQUIRE_HAULER_ID).equals("0")) {
					recordObject.remove(DbSingleton.TicketSchema.COLUMN_HAULER_NAME);
					recordObject.remove(DbSingleton.TicketSchema.COLUMN_HAULER_ADDRESS);
					recordObject.remove(DbSingleton.TicketSchema.COLUMN_HAULER_CITY);
					recordObject.remove(DbSingleton.TicketSchema.COLUMN_HAULER_STATE);
					recordObject.remove(DbSingleton.TicketSchema.COLUMN_HAULER_ZIP);
				} else {
					throw new SQLException("hauler is required, but there is no CASH hauler defined.  you must specify a hauler.");
				}
			}
		}
		////////////////////

		////////////////////TRUCK
		if(recordObject.getString(DbSingleton.TicketSchema.COLUMN_TRUCK_ID)==null || recordObject.getString(DbSingleton.TicketSchema.COLUMN_TRUCK_ID).equals("")) {
			if(customerResultSet.getString(DbSingleton.CustomerSchema.COLUMN_DEFAULT_TRUCK_ID)==null || customerResultSet.getString(DbSingleton.CustomerSchema.COLUMN_DEFAULT_TRUCK_ID).equals("")) {
				recordObject.remove(DbSingleton.TicketSchema.COLUMN_TRUCK_ID);
				recordObject.put(DbSingleton.TicketSchema.COLUMN_TRUCK_ID, "CASH:CASH");
			} else {
				recordObject.remove(DbSingleton.TicketSchema.COLUMN_TRUCK_ID);
				recordObject.put(DbSingleton.TicketSchema.COLUMN_TRUCK_ID, customerResultSet.getString(DbSingleton.CustomerSchema.COLUMN_DEFAULT_TRUCK_ID));
			}
		} else if(!customerResultSet.getString(DbSingleton.CustomerSchema.COLUMN_REQUIRE_TRUCK_ID).equals("0")) {
			//throw something, because this is a required field
			if(recordObject.getString(DbSingleton.TicketSchema.COLUMN_TRUCK_ID)==null || recordObject.getString(DbSingleton.TicketSchema.COLUMN_TRUCK_ID).equals("")) {
				throw new SQLException("TRUCK IS A REQUIRED FIELD!");
			}
		}
		String truckQuery = "SELECT * FROM Truck WHERE TruckID='"+recordObject.getString(DbSingleton.TicketSchema.COLUMN_TRUCK_ID)+"'";
		ResultSet truckResultSet = executeQuery(truckQuery);
		truckResultSet.last();
		rowCount = truckResultSet.getRow();
		truckResultSet.first();
		if(rowCount>0) {
			recordObject.remove(DbSingleton.TicketSchema.COLUMN_TRUCK_NUMBER);
			recordObject.put(DbSingleton.TicketSchema.COLUMN_TRUCK_NUMBER, truckResultSet.getString(DbSingleton.TicketSchema.COLUMN_TRUCK_NUMBER));
			recordObject.remove(DbSingleton.TicketSchema.COLUMN_TRUCK_LAST_TARE_DATE);
			recordObject.put(DbSingleton.TicketSchema.COLUMN_TRUCK_LAST_TARE_DATE, truckResultSet.getString(DbSingleton.TicketSchema.COLUMN_TRUCK_LAST_TARE_DATE));
			recordObject.remove(DbSingleton.TicketSchema.COLUMN_TRUCK_TARE_WEIGHT);
			recordObject.put(DbSingleton.TicketSchema.COLUMN_TRUCK_TARE_WEIGHT, truckResultSet.getString(DbSingleton.TicketSchema.COLUMN_TRUCK_TARE_WEIGHT));
		}
		////////////////////

		////////////////////TRAILER
		if(recordObject.getString(DbSingleton.TicketSchema.COLUMN_TRAILER_ID)==null || recordObject.getString(DbSingleton.TicketSchema.COLUMN_TRAILER_ID).equals("")) {
			if(customerResultSet.getString(DbSingleton.CustomerSchema.COLUMN_DEFAULT_TRAILER_ID)==null || customerResultSet.getString(DbSingleton.CustomerSchema.COLUMN_DEFAULT_TRAILER_ID).equals("")) {
				recordObject.remove(DbSingleton.TicketSchema.COLUMN_TRAILER_ID);
				recordObject.put(DbSingleton.TicketSchema.COLUMN_TRAILER_ID, "CASH:CASH");
			} else {
				recordObject.remove(DbSingleton.TicketSchema.COLUMN_TRAILER_ID);
				recordObject.put(DbSingleton.TicketSchema.COLUMN_TRAILER_ID, customerResultSet.getString(DbSingleton.CustomerSchema.COLUMN_DEFAULT_TRAILER_ID));
			}
		} else if(!customerResultSet.getString(DbSingleton.CustomerSchema.COLUMN_REQUIRE_TRAILER_ID).equals("0")) {
			if(recordObject.getString(DbSingleton.TicketSchema.COLUMN_TRAILER_ID)==null || recordObject.getString(DbSingleton.TicketSchema.COLUMN_TRAILER_ID).equals("")) {
				throw new SQLException("TRAILER IS A REQUIRED FIELD!");
			}
		}
		String trailerQuery = "SELECT * FROM Trailer WHERE TrailerID='"+recordObject.getString(DbSingleton.TicketSchema.COLUMN_TRAILER_ID)+"'";
		//JSONArray trailerResultSet = parseQueryResult(executeQuery(trailerQuery), "Trailer");
		ResultSet trailerResultSet = executeQuery(trailerQuery);
		trailerResultSet.last();
		rowCount = trailerResultSet.getRow();
		trailerResultSet.first();
		if(rowCount>0) {
			recordObject.remove(DbSingleton.TicketSchema.COLUMN_TRAILER_NUMBER);
			recordObject.put(DbSingleton.TicketSchema.COLUMN_TRAILER_NUMBER, trailerResultSet.getString(DbSingleton.TicketSchema.COLUMN_TRAILER_NUMBER));
			recordObject.remove(DbSingleton.TicketSchema.COLUMN_TRAILER_LAST_TARE_DATE);
			recordObject.put(DbSingleton.TicketSchema.COLUMN_TRAILER_LAST_TARE_DATE, trailerResultSet.getString(DbSingleton.TicketSchema.COLUMN_TRAILER_LAST_TARE_DATE));
			recordObject.remove(DbSingleton.TicketSchema.COLUMN_TRAILER_TARE_WEIGHT);
			recordObject.put(DbSingleton.TicketSchema.COLUMN_TRAILER_TARE_WEIGHT, trailerResultSet.getString(DbSingleton.TicketSchema.COLUMN_TRAILER_TARE_WEIGHT));
		}
		////////////////////

		////////////////////DRIVER
		if(recordObject.getString(DbSingleton.TicketSchema.COLUMN_DRIVER_ID)==null || recordObject.getString(DbSingleton.TicketSchema.COLUMN_DRIVER_ID).equals("")) {
			if(customerResultSet.getString(DbSingleton.CustomerSchema.COLUMN_DEFAULT_DRIVER_ID)==null || customerResultSet.getString(DbSingleton.CustomerSchema.COLUMN_DEFAULT_DRIVER_ID).equals("")) {
				recordObject.remove(DbSingleton.TicketSchema.COLUMN_DRIVER_ID);
				recordObject.put(DbSingleton.TicketSchema.COLUMN_DRIVER_ID, "CASH");
			} else {
				recordObject.remove(DbSingleton.TicketSchema.COLUMN_DRIVER_ID);
				recordObject.put(DbSingleton.TicketSchema.COLUMN_DRIVER_ID, customerResultSet.getString(DbSingleton.CustomerSchema.COLUMN_DEFAULT_DRIVER_ID));
			}
		} else if(!customerResultSet.getString(DbSingleton.CustomerSchema.COLUMN_REQUIRE_DRIVER_ID).equals("0")) {
			if(recordObject.getString(DbSingleton.TicketSchema.COLUMN_DRIVER_ID)==null || recordObject.getString(DbSingleton.TicketSchema.COLUMN_DRIVER_ID).equals("")) {
				throw new SQLException("DRIVER IS A REQUIRED FIELD!");
			}
		}
		String driverQuery = "SELECT * FROM Driver WHERE DriverID='"+recordObject.getString(DbSingleton.TicketSchema.COLUMN_DRIVER_ID)+"'";
		ResultSet driverResultSet = executeQuery(driverQuery);
		driverResultSet.last();
		rowCount=driverResultSet.getRow();
		driverResultSet.first();
		if(rowCount>0) {
			recordObject.remove(DbSingleton.TicketSchema.COLUMN_DRIVER_NAME);
			recordObject.put(DbSingleton.TicketSchema.COLUMN_DRIVER_NAME, driverResultSet.getString(DbSingleton.TicketSchema.COLUMN_DRIVER_NAME));
		}
		////////////////////

		return insertNewTicket(recordObject);
	}

	public int insertNewTicket(JSONObject recordObject) throws SQLException, JSONException {
		String valuesPortion = "";
		String columnsPortion = "";

		recordObject.remove(DbSingleton.TicketSchema.COLUMN_TICKET_NUMBER);
		recordObject.put(DbSingleton.TicketSchema.COLUMN_TICKET_NUMBER, getNextTicketNumber());

		//get column names
		JsonParser parser = new JsonParser();
		JsonObject recordGson = parser.parse(recordObject.toString()).getAsJsonObject();
		Set<Map.Entry<String, JsonElement>> entrySet = recordGson.entrySet();
		{
			Iterator<Map.Entry<String, JsonElement>> iterator = entrySet.iterator();
			while(iterator.hasNext()) {
				Map.Entry<String, JsonElement> entry = iterator.next();
				if(entry.getKey().equals("metadata") || entry.getKey().equals("TicketID")) {
					//do nothing
				} else {
					JsonPrimitive value = entry.getValue().getAsJsonPrimitive();
					//if(value.isString() && value.getAsString().equals("")) {
						//don't want this null value/key pair
					//} else {
					if(!columnsPortion.equals("")) {
						columnsPortion+=",";
					}
					columnsPortion+=entry.getKey();
						//System.out.println(entry.getKey()+" : "+value.getAsString());
					//}
				}
			}
		}

		//get values
		{
			Iterator<Map.Entry<String, JsonElement>> iterator = entrySet.iterator();
			while(iterator.hasNext()) {
				Map.Entry<String, JsonElement> entry = iterator.next();
				if(entry.getKey().equals("metadata") || entry.getKey().equals("TicketID")) {
					//do nothing
				} else {
					JsonPrimitive value = entry.getValue().getAsJsonPrimitive();
					//System.out.println(entry.getKey()+" : "+value.getAsString());
					if(value.isString()) {
						if(!valuesPortion.equals("")) {
							valuesPortion+=",";
						}
						//System.out.println(entry.getKey()+" : '"+value.getAsString()+"'");
						valuesPortion += "'"+value.getAsString()+"'";
					} else if(value.isNumber()) {
						if(value.getAsString().contains(".")) {
							if(!valuesPortion.equals("")) {
								valuesPortion+=",";
							}
							//System.out.println(entry.getKey()+" : "+(new Double(value.getAsDouble()).toString()));
							valuesPortion += new Double(value.getAsDouble()).toString();
						} else {
							if(!valuesPortion.equals("")) {
								valuesPortion+=",";
							}
							//System.out.println(entry.getKey()+" : "+(new Long(value.getAsLong()).toString()));
							String ticketNumber = new Long(value.getAsLong()).toString();
							valuesPortion += ticketNumber;
						}
					} else if(value.isBoolean()) {
						if(!valuesPortion.equals("")) {
							valuesPortion+=",";
						}
						if(value.getAsBoolean())
							valuesPortion += "1";
						else
							valuesPortion += "0";
					}
				}
			}
		}

		String insertQuery = "INSERT INTO "+DbSingleton.TICKET_TABLE_NAME+"("+columnsPortion+") VALUES ("+valuesPortion+")";
		String identQuery = "SELECT IDENT_CURRENT ('"+DbSingleton.TICKET_TABLE_NAME+"') AS TicketID";

		executeQuery(insertQuery);
		ResultSet identResultSet = executeQuery(identQuery);
		identResultSet.first();
		//System.out.println(identResultSet.getRow());
		int ticketID = identResultSet.getInt(1);
		//System.out.println(ticketID);

		////////////////////TICKET NUMBER CHECK
		boolean uniqueTicketNumber = false;
		while(!uniqueTicketNumber) {
			String ticketNumberCheckQuery = "SELECT * FROM "+DbSingleton.TICKET_TABLE_NAME+" WHERE "+DbSingleton.TicketSchema.COLUMN_TICKET_NUMBER+"="+recordObject.getInt(DbSingleton.TicketSchema.COLUMN_TICKET_NUMBER);
			ResultSet ticketNumberCheckResultSet = executeQuery(ticketNumberCheckQuery);
			int rowCount=0;
			ticketNumberCheckResultSet.last();
			rowCount=ticketNumberCheckResultSet.getRow();
			ticketNumberCheckResultSet.first();
			if(rowCount<1)
				throw new SQLException(LOG_TAG+": wat...");
			else if(rowCount>1) {
				uniqueTicketNumber = false;
			} else {
				uniqueTicketNumber = true;
			}
			if(!uniqueTicketNumber) {
				//update the ticket for the next iteration of this loop, attempting, again, to get a unique ticket number
				String uniqueTicketNumberQuery = "UPDATE "+DbSingleton.TICKET_TABLE_NAME+" SET "+DbSingleton.TicketSchema.COLUMN_TICKET_NUMBER+"="+getNextTicketNumber()+" WHERE "+DbSingleton.TicketSchema.COLUMN_TICKET_ID+"="+ticketID;
				executeQuery(uniqueTicketNumberQuery);
			}
		}
		////////////////////

		return ticketID;
	}

	public int getNextTicketNumber() throws SQLException, JSONException {
		String ticketNumberQuery = "SELECT MAX("+DbSingleton.TicketSchema.COLUMN_TICKET_NUMBER+") FROM "+DbSingleton.TICKET_TABLE_NAME;
		System.out.println("querying ticket number...");
		ResultSet ticketNumberResultSet = executeQuery(ticketNumberQuery);
		int ticketNumber = 0;
		System.out.println("getting row count...");
		ticketNumberResultSet.last();
		int rowCount = ticketNumberResultSet.getRow();
		ticketNumberResultSet.first();
		if(rowCount<1) {
			//throw new SQLException(LOG_TAG+": wat...");
			//this means there aren't any tickets yet.
			//TODO: use the "beginning ticket number" value from the database, or 0
		} else {
			ticketNumber = ticketNumberResultSet.getInt(1);
		}
		System.out.println("next ticket number: "+ticketNumber);

		return ticketNumber+1;
	}

	public JSONObject parseWeight(JSONObject requestObject) throws SQLException, JSONException {
		String host = null;
		int port = 0;
		int timeout = 0;
		int termChar = 0;
		int weightBeginPos = 0;
		int weightEndPos = 0;
		int statusBeginPos = 0;
		int statusEndPos = 0;
		int normalChar = 0;
		int motionChar = 0;
		int bufferLength = 0;
		String overCapacity = null;
		if(requestObject.has("host") && requestObject.has("port")) {
			host = requestObject.getString("host");
			port = requestObject.getInt("port");
			return getCondecWeight(host, port);
		} else if(requestObject.has("scale_id")) {
			ResultSet rs = null;
			Integer scaleID = requestObject.getInt("scale_id");
			rs = executeQuery("SELECT * FROM INISetting WHERE INIKey='Scale"+scaleID+"OverCapacityWeight'");
			rs.first();
			overCapacity = rs.getString("INIValue");
			if(overCapacity.equals("0")) {
				overCapacity = "--------";
			}
			rs = executeQuery("SELECT * FROM INISetting WHERE INIKey='Scale"+scaleID+"IPAddress'");
			rs.first();
			host = rs.getString("INIValue");
			rs = executeQuery("SELECT * FROM INISetting WHERE INIKey='Scale"+scaleID+"IPPort'");
			rs.first();
			port = rs.getInt("INIValue");
			rs = executeQuery("SELECT * FROM INISetting WHERE INIKey='Scale"+scaleID+"Timeout'");
			rs.first();
			timeout = rs.getInt("INIValue");
			rs = executeQuery("SELECT * FROM INISetting WHERE INIKey='Scale"+scaleID+"TerminationChar'");
			rs.first();
			termChar = rs.getInt("INIValue");
			rs = executeQuery("SELECT * FROM INISetting WHERE INIKey='Scale"+scaleID+"WeightBeginPos'");
			rs.first();
			weightBeginPos = rs.getInt("INIValue");
			rs = executeQuery("SELECT * FROM INISetting WHERE INIKey='Scale"+scaleID+"WeightEndPos'");
			rs.first();
			weightEndPos = rs.getInt("INIValue");
			rs = executeQuery("SELECT * FROM INISetting WHERE INIKey='Scale"+scaleID+"StatisBeginPos' OR INIKey='Scale"+scaleID+"StatusBeginPos'");
			rs.first();
			statusBeginPos = rs.getInt("INIValue");
			rs = executeQuery("SELECT * FROM INISetting WHERE INIKey='Scale"+scaleID+"StatisEndPos' OR INIKey='Scale"+scaleID+"StatusEndPos'");
			rs.first();
			statusEndPos = rs.getInt("INIValue");
			rs = executeQuery("SELECT * FROM INISetting WHERE INIKey='Scale"+scaleID+"NormalChar'");
			rs.first();
			normalChar = rs.getInt("INIValue");
			rs = executeQuery("SELECT * FROM INISetting WHERE INIKey='Scale"+scaleID+"BufferLength'");
			rs.first();
			bufferLength = rs.getInt("INIValue");
			rs = executeQuery("SELECT * FROM INISetting WHERE INIKey='Scale"+scaleID+"MotionChar'");
			rs.first();
			motionChar = rs.getInt("INIValue");
			if(host != null &&
				port != 0 &&
				timeout != 0 &&
				termChar != 0 &&
				weightBeginPos != 0 &&
				weightEndPos != 0 &&
				statusBeginPos != 0 &&
				statusEndPos != 0 &&
				normalChar != 0 &&
				motionChar != 0 &&
				bufferLength != 0 &&
				overCapacity != null){
				return getWeight(host, port, bufferLength, weightBeginPos, weightEndPos, termChar, statusBeginPos, statusEndPos, normalChar, motionChar, timeout, overCapacity);
			} else {
				return getCondecWeight();
			}
		} else {
			return getCondecWeight();
		}
	}

	public JSONObject getCondecWeight(String host, int port) {
		return getWeight(host, port, 14, 2, 9, 10, 12, 12, 32, 77, 3, "-------");
	}

	public JSONObject getCondecWeight() {
		return getCondecWeight("localhost", 3001);
	}

	public JSONObject getWeight(final String host, final int port, final int numChars, final int weightBeginPos, final int weightEndPos, final int termChar, final int statusBeginPos, final int statusEndPos, final int normalChar, final int motionChar, int timeout, final String overcapacity) {
		//Thread socketThread = new Thread() {
		//	public void run() {
		//cannot run in thread
		Socket connection = null;
		JSONObject returnObject = new JSONObject();
		try {
			System.out.println("opening socket");
			connection = new Socket(host, port);
			System.out.println("getting stream");
			InputStreamReader reader = new InputStreamReader(connection.getInputStream());
			System.out.println("creating character buffer");
			boolean success = false;
			java.util.Date beginDate = new java.util.Date();
			java.util.Date endDate = (java.util.Date)beginDate.clone();
			endDate.setSeconds(endDate.getSeconds()+timeout);
			while(!success) {
				beginDate = new java.util.Date();
				if(beginDate.after(endDate)) {
					returnObject.put("error", "the operation timed out");
					//out.println(returnObject.toString(5));
					return returnObject;
				}
				String string = new String();
				int count=0;
				while(reader.read()!=(char)(termChar)) {
					if(count>5000) {
						break;
					}
					count++;
				}
				if(count>5000) {
					returnObject.put("error", "termchar was not found in the stream.  are you sure the scale is set up correctly?");
					//out.println(returnObject.toString(5));
					return returnObject;
				}
				for(int i=0; i<numChars; i++) {
					string += (char)(reader.read());
				}
				//get negative marker also
				String weight = string.substring(weightBeginPos-1, weightEndPos);
				String status = string.substring(statusBeginPos-1, statusEndPos);
				String uom = string.substring(9,11);
				System.out.println("weight: '"+weight+"'");
				System.out.println("status: '"+status+"'");
				System.out.println("uom   : '"+uom+"'");

				if(string.contains(overcapacity)) {
					returnObject.put("error", "Over Capacity!");
					return returnObject;
				} else {
					System.out.println("extracting weight value");
					returnObject.put("weight", new Integer(weight).toString());
					System.out.println("checking motion");
					if(status.charAt(0)==((char)(normalChar))) {
						returnObject.put("motion", false);
					} else if(status.charAt(0)==((char)(motionChar))) {
						returnObject.put("motion", true);
					} else {
						returnObject.put("error", "Invalid Motion Char!\n(check web service settings)");
					}
					System.out.println("checking uom");
					if(uom.equals("LB")) {
						returnObject.put("uom", "lbs");
					} else if(uom.equals("KG")) {
						returnObject.put("uom", "kg");
					} else {
						//unknown weight type, no cause for error here.
					}
				}
				System.out.println("sending resultant json");
				//out.println(returnObject.toString(5));
				success = true;
			}
		} catch(Exception e) {
			System.out.println("ERROR");
			System.out.println("ERROR: could not connect to scale: "+e.toString());
			try {
				returnObject.put("error", "could not connect to scale: "+e.toString());
				return returnObject;
				//out.println(returnObject.toString(5));
			} catch(Exception je) {
				//out.println("parser error?");
				returnObject.put("error", je.toString());
				return returnObject;
			}
		} finally {
			try {
				connection.close();
			} catch(Exception e) {
				//don't care at this point.
			}
			return returnObject;
		}
		//	}
		//};

		//socketThread.start();
	}

	protected class QueryJoinObject {
		public final String mTable;
		public final String mJoinTable;
		public final String mLinkOn;
		public final String mLinkColumn;

		public QueryJoinObject(String table, String joinTable, String linkOn, String linkColumn) {
			mTable = table;
			mJoinTable = joinTable;
			mLinkOn = linkOn;
			mLinkColumn = linkColumn;
		}
	}

	public JSONObject parseQuery(JSONObject requestObject, OutputStream os) throws JSONException, SQLException, IOException {
		String query = "";
		String tableName = "";

		JSONArray selectionColumns = requestObject.getJSONArray("selection");
		query+="SELECT "+selectionColumns.getString(0);
		for(int i=1; i<selectionColumns.length(); i++) {
			query+=","+selectionColumns.getString(i);
		}
		query+=" FROM ";
		tableName = requestObject.getString("table");
		query+=tableName;
		if(requestObject.has("where")) {
			query+=" WHERE ";
			query+=requestObject.getString("where");
		}
		query+=";";
		
		JSONObject responseObject = new JSONObject();
		JSONArray resultJSONArray;

		//System.out.println(query);
		if(os!=null) {
			os.write(new String("{\"result_set\":[").getBytes());
			parseQueryResults(executeQuery(query), tableName, os, false);
		}

		resultJSONArray = parseQueryResults(executeQuery(query), tableName);
		//System.out.println(resultSet.toString(5));
		if(requestObject.has("join")) {
			JSONArray joinArray = requestObject.getJSONArray("join");
			List<String> tableList = new LinkedList<String>();
			tableList.add(tableName);
			for(int h=0; h<joinArray.length(); h++) {
				//find the next link table
				JSONObject joinObject = null;
				for(int j=0; j<joinArray.length(); j++) {
					for(int k=0; k<tableList.size(); k++) {
						if(joinArray.getJSONObject(j).getString("table").equals(tableList.get(k))) {
							//break;	//this table has already been joined
						} else if(joinArray.getJSONObject(j).getString("link_table").equals(tableList.get(k))) {
							joinObject = joinArray.getJSONObject(j);
						}
					}
				}
				if(joinObject==null) {
					throw new JSONException("join syntax was incorrect, no valid joins were found");
				}
				for(int i=0; i<resultJSONArray.length(); i++) {
					//we now know the table to join, now search through the results looking for the link_table and query on each
					if(joinObject.getString("link_table").equals(resultJSONArray.getJSONObject(i).getJSONObject("metadata").getString("table"))) {
						//this result contains the correct link_table.  query against it's link_column value
						JsonObject linkObject = new JsonParser().parse(resultJSONArray.getJSONObject(i).toString()).getAsJsonObject();
						JsonPrimitive linkValue = null;
						if(linkObject.has(joinObject.getString("link_column"))) {
							linkValue = linkObject.getAsJsonPrimitive(joinObject.getString("link_column"));
						} else {
							//just skip this result and notify the log console.
							//however, this is most likely an error and should be fixed.
							System.out.println("link column did not contain the following column (please fix): "+joinObject.get("link_column"));
						}

						String predicate = "1=1";
						if(linkValue.isBoolean()) {
							if(linkValue.getAsBoolean()==true) {
								predicate = joinObject.getString("column")+"=1";
							} else {
								predicate = joinObject.getString("column")+"=0";
							}
						}
						if(linkValue.isNumber()) {
							predicate = joinObject.getString("column")+"="+linkValue.getAsString();
						}
						if(linkValue.isString()) {
							predicate = joinObject.getString("column")+"='"+linkValue.getAsString()+"'";
						}
						String joinQuery = "";
						if(joinObject.has("selection")) {
							JSONArray joinSelectionColumns = joinObject.getJSONArray("selection");
							joinQuery+="SELECT "+joinSelectionColumns.getString(0);
							for(int k=1; k<joinSelectionColumns.length(); k++) {
								joinQuery+=","+joinSelectionColumns.getString(k);
							}
						} else {
							joinQuery+="SELECT *";
						}
						//build and execute query, adding it to the result set
						joinQuery += " FROM "+ joinObject.getString("table") +" WHERE "+predicate;
						if(joinObject.has("where")) {
							String whereClause = joinObject.getString("where");
							joinQuery += " AND "+whereClause;
						}
						joinQuery+=";";
						//System.out.println("join query: "+joinQuery);
						//JSONArray parsedResult = parseQueryResults(executeQuery(joinQuery), joinObject.getString("table"));
						//System.out.println("join parsed result: "+parsedResult.toString(5));
						if(os!=null)
							parseQueryResults(executeQuery(query), joinObject.getString("table"), os, true);
						else
							concatArray(resultJSONArray, parseQueryResults(executeQuery(query), joinObject.getString("table")));
					}
				}
				tableList.add(joinObject.getString("table"));
			}
		}
		if(os!=null)
			os.write(new String("]}\n").getBytes());
		responseObject.put("result_set", resultJSONArray);
		//System.out.println(responseObject.toString(5));
		return responseObject;
	}

	public ResultSet executeQuery(String query) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		System.out.println("query: "+query);
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			if(mSQLPort!=null && mSQLHost!=null && mDbName!=null && mDbPass!=null && mDbUser!=null && !mSQLPort.equals("") && !mSQLHost.equals("") && !mDbName.equals("") && !mDbPass.equals("") && !mDbUser.equals("")) {
				conn = DriverManager.getConnection("jdbc:sqlserver://"+mSQLHost+":"+mSQLPort+";databaseName="+mDbName+";user="+mDbUser+";password="+mDbPass+";");
				//System.out.println("DB connect: jdbc:sqlserver://"+mSQLHost+":"+mSQLPort+";databaseName=TMSV73;user=sa;password=keith;");
			} else {
				throw new IllegalArgumentException("config file error.  something was missing and a default value wasn't used.  this is a programmer error.  please contact support.");
			}
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(query);
            //NOTE: if we close this immediately, we will lose the ResultSet.  the Statement will close itself anyway
            //      after the garbage collector notices it has no references.
			//stmt.close();
		} catch(Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
			return null;
		}
		return rs;
	}

	private JSONArray concatArray(JSONArray... arrs) throws JSONException {
		JSONArray result = new JSONArray();
		for (JSONArray arr : arrs) {
			for (int i = 0; i < arr.length(); i++) {
				result.put(arr.get(i));
			}
		}
		return result;
	}

	public JSONArray parseQueryResults(ResultSet rs, String table) throws SQLException, JSONException {
		JSONArray resultJSONArray = new JSONArray();
		ResultSetMetaData rsmd = rs.getMetaData();
		int columns = rsmd.getColumnCount();
		while(rs.next()) {
			JSONObject result = new JSONObject();
			JSONObject resultMeta = new JSONObject();
			resultMeta.put("table", table);
			result.put("metadata", resultMeta);
			for(int i=1; i<=columns; i++) {
				//out.println("<td>"+rs.getString(i)+"</td>");
				int type = rsmd.getColumnType(i);
				//result.put(rsmd.getColumnName(i), rs.get)
				switch(type) {
					case Types.BIT:
						result.put(rsmd.getColumnName(i), rs.getBoolean(i));
						break;
					case Types.TINYINT:
						result.put(rsmd.getColumnName(i), rs.getInt(i));
						break;
					case Types.SMALLINT:
						result.put(rsmd.getColumnName(i), rs.getInt(i));
						break;
					case Types.INTEGER:
						//System.out.println(rsmd.getColumnName(i) + "  type: "+type);
						result.put(rsmd.getColumnName(i), rs.getInt(i));
						break;
					case Types.BIGINT:
						result.put(rsmd.getColumnName(i), rs.getInt(i));
						break;
					case Types.FLOAT:
						result.put(rsmd.getColumnName(i), rs.getFloat(i));
						break;
					case Types.REAL:
						result.put(rsmd.getColumnName(i), rs.getDouble(i));
						break;
					case Types.DOUBLE:
						result.put(rsmd.getColumnName(i), rs.getDouble(i));
						break;
					case Types.NUMERIC:
						result.put(rsmd.getColumnName(i), rs.getDouble(i));
						break;
					case Types.DECIMAL:
						result.put(rsmd.getColumnName(i), rs.getDouble(i));
						break;
					case Types.CHAR:
						result.put(rsmd.getColumnName(i), rs.getString(i));
						break;
					case Types.VARCHAR:
						result.put(rsmd.getColumnName(i), rs.getString(i));
						break;
					case Types.LONGVARCHAR:
						result.put(rsmd.getColumnName(i), rs.getString(i));
						break;
					case Types.DATE:
					{
						java.util.Date date = rs.getDate(i);
						result.put(rsmd.getColumnName(i), date.getTime());
						break;
					}
					case Types.TIME:
					{
						java.util.Date date = rs.getDate(i);
						result.put(rsmd.getColumnName(i), date.getTime());
						break;
					}
					case Types.TIMESTAMP:
					{
						java.util.Date date = rs.getDate(i);
						result.put(rsmd.getColumnName(i), date.getTime());
						break;
					}
					case Types.BINARY:
						result.put(rsmd.getColumnName(i), rs.getInt(i));
						break;
					case Types.VARBINARY:
						result.put(rsmd.getColumnName(i), rs.getInt(i));
						break;
					case Types.LONGVARBINARY:
						result.put(rsmd.getColumnName(i), rs.getLong(i));
						break;
					case Types.NULL:
						result.put(rsmd.getColumnName(i), "");
						break;
					case Types.BOOLEAN:
						result.put(rsmd.getColumnName(i), rs.getBoolean(i));
						break;
					case Types.ROWID:
						result.put(rsmd.getColumnName(i), rs.getInt(i));
						break;
					case Types.NCHAR:
						result.put(rsmd.getColumnName(i), rs.getString(i));
						break;
					case Types.NVARCHAR:
						result.put(rsmd.getColumnName(i), rs.getString(i));
						break;
					case Types.LONGNVARCHAR:
						result.put(rsmd.getColumnName(i), rs.getString(i));
						break;
					case Types.SQLXML:
					case Types.NCLOB:
					case Types.DATALINK:
					case Types.REF:
					case Types.OTHER:
					case Types.JAVA_OBJECT:
					case Types.DISTINCT:
					case Types.STRUCT:
					case Types.ARRAY:
					case Types.BLOB:
					case Types.CLOB:
					default:
						result.put(rsmd.getColumnName(i), rs.getString(i));
						break;
				}
			}
			//if(table.equals("Ticket"))
				//System.out.println(result.toString(5));
			resultJSONArray.put(result);
		}
		return resultJSONArray;
	}

	public void parseQueryResults(ResultSet rs, String table, OutputStream os, boolean append) throws SQLException, JSONException, IOException {
		//JSONArray resultJSONArray = new JSONArray();
		ResultSetMetaData rsmd = rs.getMetaData();
		int columns = rsmd.getColumnCount();
		rs.last();
		int rows = rs.getRow();
		os.write(new String("total rows: "+rows).getBytes());
		rs.first();
		int rowCount = 0;
		while(rs.next()) {
			if(!rs.isFirst() || append) {
				os.write(new String(",\n").getBytes());
				os.write(new String(""+rowCount).getBytes());
			}
			if(rowCount>=69)
				System.out.println("break point");
			rowCount++;
			JSONObject result = new JSONObject();
			JSONObject resultMeta = new JSONObject();
			resultMeta.put("table", table);
			result.put("metadata", resultMeta);
			for(int i=1; i<=columns; i++) {
				//out.println("<td>"+rs.getString(i)+"</td>");
				int type = rsmd.getColumnType(i);
				//result.put(rsmd.getColumnName(i), rs.get)
				switch(type) {
					case Types.BIT:
						result.put(rsmd.getColumnName(i), rs.getBoolean(i));
						break;
					case Types.TINYINT:
						result.put(rsmd.getColumnName(i), rs.getInt(i));
						break;
					case Types.SMALLINT:
						result.put(rsmd.getColumnName(i), rs.getInt(i));
						break;
					case Types.INTEGER:
						//System.out.println(rsmd.getColumnName(i) + "  type: "+type);
						result.put(rsmd.getColumnName(i), rs.getInt(i));
						break;
					case Types.BIGINT:
						result.put(rsmd.getColumnName(i), rs.getInt(i));
						break;
					case Types.FLOAT:
						result.put(rsmd.getColumnName(i), rs.getFloat(i));
						break;
					case Types.REAL:
						result.put(rsmd.getColumnName(i), rs.getDouble(i));
						break;
					case Types.DOUBLE:
						result.put(rsmd.getColumnName(i), rs.getDouble(i));
						break;
					case Types.NUMERIC:
						result.put(rsmd.getColumnName(i), rs.getDouble(i));
						break;
					case Types.DECIMAL:
						result.put(rsmd.getColumnName(i), rs.getDouble(i));
						break;
					case Types.CHAR:
						result.put(rsmd.getColumnName(i), rs.getString(i));
						break;
					case Types.VARCHAR:
						result.put(rsmd.getColumnName(i), rs.getString(i));
						break;
					case Types.LONGVARCHAR:
						result.put(rsmd.getColumnName(i), rs.getString(i));
						break;
					case Types.DATE:
					{
						java.util.Date date = rs.getDate(i);
						result.put(rsmd.getColumnName(i), date.getTime());
						break;
					}
					case Types.TIME:
					{
						java.util.Date date = rs.getDate(i);
						result.put(rsmd.getColumnName(i), date.getTime());
						break;
					}
					case Types.TIMESTAMP:
					{
						java.util.Date date = rs.getDate(i);
						result.put(rsmd.getColumnName(i), date.getTime());
						break;
					}
					case Types.BINARY:
						result.put(rsmd.getColumnName(i), rs.getInt(i));
						break;
					case Types.VARBINARY:
						result.put(rsmd.getColumnName(i), rs.getInt(i));
						break;
					case Types.LONGVARBINARY:
						result.put(rsmd.getColumnName(i), rs.getLong(i));
						break;
					case Types.NULL:
						result.put(rsmd.getColumnName(i), "");
						break;
					case Types.BOOLEAN:
						result.put(rsmd.getColumnName(i), rs.getBoolean(i));
						break;
					case Types.ROWID:
						result.put(rsmd.getColumnName(i), rs.getInt(i));
						break;
					case Types.NCHAR:
						result.put(rsmd.getColumnName(i), rs.getString(i));
						break;
					case Types.NVARCHAR:
						result.put(rsmd.getColumnName(i), rs.getString(i));
						break;
					case Types.LONGNVARCHAR:
						result.put(rsmd.getColumnName(i), rs.getString(i));
						break;
					case Types.SQLXML:
					case Types.NCLOB:
					case Types.DATALINK:
					case Types.REF:
					case Types.OTHER:
					case Types.JAVA_OBJECT:
					case Types.DISTINCT:
					case Types.STRUCT:
					case Types.ARRAY:
					case Types.BLOB:
					case Types.CLOB:
					default:
						result.put(rsmd.getColumnName(i), rs.getString(i));
						break;
				}
			}
			//if(table.equals("Ticket"))
				//System.out.println(result.toString(5));
			//if(result.getInt("TicketNumber")==126868)
			//	System.out.println("break point");
			//resultJSONArray.put(result);
			os.write(result.toString(5).getBytes());
		}
		//return resultJSONArray;
	}

}