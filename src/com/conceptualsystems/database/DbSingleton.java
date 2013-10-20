package com.conceptualsystems.database;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DbSingleton {
	private static final DbSingleton instance = new DbSingleton();
	public static final String LOG_TAG = "DbSingleton.java";

	public static final String DATABASE_NAME = "smsmobile2";
	public static final String SORT_ASC = " ASC";
	public static final String SORT_DESC = " DESC";

	public static final String UOM_EACH = "EACH";
	public static final String UOM_CUYD = "CUYD";
	public static final String UOM_LBS = "LBS";
	public static final String UOM_100WT = "100WT";
	public static final String UOM_LTONS = "LTONS";
	public static final String UOM_TONS = "TONS";
	public static final String UOM_OZT = "OZT";

	public static final String CUSTOMER_TABLE_NAME = "Customer";
	public static final String PRODUCT_TABLE_NAME = "Product";
	public static final String ACCOUNT_TYPES_TABLE_NAME = "AccountType";
	public static final String LOCATION_TABLE_NAME = "Location";
	public static final String UOM_TABLE_NAME = "UOM";
	public static final String VIN_COLOR_TABLE_NAME = "VINColor";
	public static final String VIN_ISSUE_TABLE_NAME = "VINIssue";
	public static final String VIN_MAKE_MODEL_TABLE_NAME = "VINMakeModel";
	public static final String COUNTY_TABLE_NAME = "County";
	public static final String TICKET_TABLE_NAME = "Ticket";
	public static final String TICKET_DETAIL_TABLE_NAME = "TicketDetail";

	public final Map<String, Schema> DatabaseTables = new HashMap<String, Schema>();

	private DbSingleton() {
		DatabaseTables.put(CUSTOMER_TABLE_NAME, new CustomerSchema());
		DatabaseTables.put(PRODUCT_TABLE_NAME, new ProductSchema());
		DatabaseTables.put(ACCOUNT_TYPES_TABLE_NAME, new AccountTypesSchema());
		//DatabaseTables.put(LOCATION_TABLE_NAME, new LocationSchema());
		DatabaseTables.put(UOM_TABLE_NAME, new UOMSchema());
		DatabaseTables.put(TICKET_TABLE_NAME, new TicketSchema());
		//DatabaseTables.put(VIN_COLOR_TABLE_NAME, new VinColorSchema());
		DatabaseTables.put(TICKET_DETAIL_TABLE_NAME, new TicketDetailSchema());
		DatabaseTables.put(COUNTY_TABLE_NAME, new CountySchema());
	}

	public static DbSingleton getInstance() {
		return instance;
	}
/*
	public void updateOrInsert(ContentValues cv, String tableName) {
		DbInsertOrUpdateThread thread = new DbInsertOrUpdateThread(mDb, cv, tableName);
		thread.start();
	}

	public void updateOrInsertNonThread(ContentValues cv, String tableName) {
		String indexColumnName = DatabaseTables.get(tableName).getIndexColumn();
		String value = String.valueOf(cv.get(indexColumnName));
		if(mDb.update(tableName, cv, indexColumnName + "=?", new String[]{value})<1) {
			//Log.i(LOG_TAG, "attempting insert...");
			if(mDb.insert(tableName, null, cv)>0) {
				//Log.i(LOG_TAG, "insert succeeded: " + TABLE_NAME + "\n" + cv.toString());
			} else {
				//Log.i(LOG_TAG, "insert failed: " + TABLE_NAME + "\n" + cv.toString());
			}
		}
	}
*/
	public final class UOMSchema extends Schema {
		public static final String COLUMN_UOM = "UOM";
		public static final String COLUMN_FACTOR = "Factor";
		public static final String COLUMN_LAST_MODIFIED = "LastModified";

		public UOMSchema() {
			super(UOM_TABLE_NAME);
			addColumn(COLUMN_UOM, TYPE_TEXT + " " + TYPE_NOT_NULL);
			addColumn(COLUMN_FACTOR, TYPE_REAL);
			addColumn(COLUMN_LAST_MODIFIED, TYPE_INTEGER + " " + TYPE_NOT_NULL);
			setIndexColumn(COLUMN_UOM);
		}
	}

	public final class ProductSchema extends Schema {
		public static final String COLUMN_ID = "ProductID";
		public static final String COLUMN_NAME = "ProductName";
		public static final String COLUMN_UOM = "UOM";
		public static final String COLUMN_T1_PRICE = "T1Price";
		public static final String COLUMN_T2_PRICE = "T2Price";
		public static final String COLUMN_T3_PRICE = "T3Price";
		public static final String COLUMN_T4_PRICE = "T4Price";
		public static final String COLUMN_T5_PRICE = "T5Price";
		public static final String COLUMN_T6_PRICE = "T6Price";
		public static final String COLUMN_T1_MAX_WEIGHT = "T1MaxWeight";
		public static final String COLUMN_T2_MAX_WEIGHT = "T2MaxWeight";
		public static final String COLUMN_T3_MAX_WEIGHT = "T3MaxWeight";
		public static final String COLUMN_T4_MAX_WEIGHT = "T4MaxWeight";
		public static final String COLUMN_T5_MAX_WEIGHT = "T5MaxWeight";
		public static final String COLUMN_T6_MAX_WEIGHT = "T6MaxWeight";
		public static final String COLUMN_FACTOR = "Factor";
		public static final String COLUMN_SHOW_HISTORY = "ShowHistory";
		public static final String COLUMN_MIN_COST = "MinCost";
		public static final String COLUMN_PRODUCT_NOTE = "ProductNote";
		public static final String COLUMN_PRODUCT_TAXABLE = "ProductTaxable";
		public static final String COLUMN_PAY_BY_CHECK = "PayByCheck";
		public static final String COLUMN_SHOW_IN_POLICE_REPORT = "ShowInPoliceReport";
		public static final String COLUMN_QBCOA_NAME = "QBCOAName";
		public static final String COLUMN_INVENTORY_NAME = "InventoryName";
		public static final String COLUMN_ALLOW_AUTO_ATTENDANT = "AllowAutoAttendant";
		public static final String COLUMN_USE_TIER_WEIGHTS = "UseTierWeights";
		public static final String COLUMN_SALE_PRICE = "SalePrice";
		public static final String COLUMN_MAX_SPREAD = "MaxSpread";
		public static final String COLUMN_MIN_SPREAD = "MinSpread";
		public static final String COLUMN_LAST_MODIFIED = "LastModified";
		public static final String COLUMN_DASHBOARD_NAME = "DasboardName";

		public ProductSchema() {
			super(PRODUCT_TABLE_NAME);
			addColumn(COLUMN_ID, TYPE_TEXT);
			addColumn(COLUMN_NAME, TYPE_TEXT);
			addColumn(COLUMN_UOM, TYPE_TEXT);
			addColumn(COLUMN_T1_PRICE, TYPE_REAL);
			addColumn(COLUMN_T2_PRICE, TYPE_REAL);
			addColumn(COLUMN_T3_PRICE, TYPE_REAL);
			addColumn(COLUMN_T4_PRICE, TYPE_REAL);
			addColumn(COLUMN_T5_PRICE, TYPE_REAL);
			addColumn(COLUMN_T6_PRICE, TYPE_REAL);
			addColumn(COLUMN_T1_MAX_WEIGHT, TYPE_REAL);
			addColumn(COLUMN_T2_MAX_WEIGHT, TYPE_REAL);
			addColumn(COLUMN_T3_MAX_WEIGHT, TYPE_REAL);
			addColumn(COLUMN_T4_MAX_WEIGHT, TYPE_REAL);
			addColumn(COLUMN_T5_MAX_WEIGHT, TYPE_REAL);
			addColumn(COLUMN_T6_MAX_WEIGHT, TYPE_REAL);
			addColumn(COLUMN_FACTOR, TYPE_REAL);
			addColumn(COLUMN_SHOW_HISTORY, TYPE_BOOLEAN);
			addColumn(COLUMN_MIN_COST, TYPE_REAL);
			addColumn(COLUMN_PRODUCT_NOTE, TYPE_TEXT);
			addColumn(COLUMN_PRODUCT_TAXABLE, TYPE_BOOLEAN);
			addColumn(COLUMN_PAY_BY_CHECK, TYPE_BOOLEAN);
			addColumn(COLUMN_SHOW_IN_POLICE_REPORT, TYPE_BOOLEAN);
			addColumn(COLUMN_QBCOA_NAME, TYPE_TEXT);
			addColumn(COLUMN_INVENTORY_NAME, TYPE_TEXT);
			addColumn(COLUMN_ALLOW_AUTO_ATTENDANT, TYPE_BOOLEAN);
			addColumn(COLUMN_USE_TIER_WEIGHTS, TYPE_BOOLEAN);
			addColumn(COLUMN_SALE_PRICE, TYPE_REAL);
			addColumn(COLUMN_MAX_SPREAD, TYPE_REAL);
			addColumn(COLUMN_MIN_SPREAD, TYPE_REAL);
			addColumn(COLUMN_LAST_MODIFIED, TYPE_INTEGER + " " + TYPE_NOT_NULL);
			addColumn(COLUMN_DASHBOARD_NAME, TYPE_TEXT);
			setIndexColumn(COLUMN_ID);
		}
	}

	public final class CustomerSchema extends Schema {
		public static final String COLUMN_NAME = "CustomerName";
		public static final String COLUMN_ID = "CustomerID";
		public static final String COLUMN_ADDRESS = "CustomerAddress";
		public static final String COLUMN_CITY = "CustomerCity";
		public static final String COLUMN_STATE = "CustomerState";
		public static final String COLUMN_ZIP = "CustomerZip";
		public static final String COLUMN_PHONE = "CustomerTelephone";
		public static final String COLUMN_FAX = "CustomerFax";
		public static final String COLUMN_CONTACT = "CustomerContact";
		public static final String COLUMN_ACCOUNT_TYPE = "CustomerAccountType";
		public static final String COLUMN_TYPE_CODE = "CustomerTypeCode";
		public static final String COLUMN_TAXABLE = "CustomerTaxable";
		public static final String COLUMN_INVOICEABLE = "CustomerInvoiceable";
		public static final String COLUMN_ACTIVE = "CustomerActive";
		public static final String COLUMN_COUNTY = "County";
		public static final String COLUMN_ALLOW_AUTO_ATTENDANT = "AllowAutoAttendant";
		public static final String COLUMN_TEMPLATE_NAME = "TemplateName";
		public static final String COLUMN_RECEIPT_COPIES = "ReceiptCopies";
		public static final String COLUMN_REQUEST_TEMPLATE = "RequestTemplate";
		public static final String COLUMN_REQUEST_TEMPLATE_ON_LAST_COPY = "RequestTemplateOnLastCopy";
		public static final String COLUMN_REQUIRE_HAULER_ID = "RequireHaulerID";
		public static final String COLUMN_REQUIRE_TRUCK_ID = "RequireTruckID";
		public static final String COLUMN_REQUIRE_TRAILER_ID = "RequireTrailerID";
		public static final String COLUMN_REQUIRE_DRIVER_ID = "RequireDriverID";
		public static final String COLUMN_DEFAULT_HAULER_ID = "DefaultHaulerID";
		public static final String COLUMN_DEFAULT_TRUCK_ID = "DefaultTruckID";
		public static final String COLUMN_DEFAULT_TRAILER_ID = "DefaultTrailerID";
		public static final String COLUMN_DEFAULT_DRIVER_ID = "DefaultDriverID";
		public static final String COLUMN_USE_TRUCK_TARE_WEIGHT = "UseTruckTareWeight";
		public static final String COLUMN_USE_TRAILER_TARE_WEIGHT = "UseTrailerTareWeight";
		public static final String COLUMN_DEALER_LICENSE_NUMBER = "DealerLicenseNumber";
		public static final String COLUMN_BUSINESS_NAME_ID = "BusinessNameID";
		public static final String COLUMN_REGISTERED = "Registered";
		public static final String COLUMN_VALID_ADDRESS = "ValidAddress";
		public static final String COLUMN_RECONCILIATION_TYPE = "ReconciliationType";
		public static final String COLUMN_DL_SEX = "DL_Sex";
		public static final String COLUMN_DL_HEIGHT = "DL_Height";
		public static final String COLUMN_DL_CLASS = "DL_Class";
		public static final String COLUMN_DL_WEIGHT = "DL_Weight";
		public static final String COLUMN_DL_HAIR = "DL_Hair";
		public static final String COLUMN_DL_EYES = "DL_Eyes";
		public static final String COLUMN_DL_RACE = "DL_Race";
		public static final String COLUMN_DL_DOB = "DL_DOB";
		public static final String COLUMN_DL_ISSUE_DATE = "DL_IssueDate";
		public static final String COLUMN_DL_EXPIRATION_DATE = "DL_ExpirationDate";
		public static final String COLUMN_CUSTOMER_PHONE_2 = "CustomerTelephone2";
		public static final String COLUMN_CUSTOMER_MOBILE = "CustomerMobile";
		public static final String COLUMN_CUSTOMER_NOTES = "CustomerNotes";
		public static final String COLUMN_CUSTOMER_EMAIL = "CustomerEmailAddress";
		public static final String COLUMN_QB_TYPE = "QBType";
		public static final String COLUMN_INDUSTRIAL = "Industrial";
		public static final String COLUMN_LAST_MODIFIED = "LastModified";
		public static final String COLUMN_TMS_ROW_GUID = "TMSRowGUID";

		public CustomerSchema() {
			super(CUSTOMER_TABLE_NAME);
			/////////set up columns
			addColumn(COLUMN_NAME, TYPE_TEXT);
			addColumn(COLUMN_ID, TYPE_TEXT);
			addColumn(COLUMN_ADDRESS, TYPE_TEXT);
			addColumn(COLUMN_CITY, TYPE_TEXT);
			addColumn(COLUMN_STATE, TYPE_TEXT);
			addColumn(COLUMN_ZIP, TYPE_TEXT);
			addColumn(COLUMN_PHONE, TYPE_TEXT);
			addColumn(COLUMN_FAX, TYPE_TEXT);
			addColumn(COLUMN_CONTACT, TYPE_TEXT);
			addColumn(COLUMN_ACCOUNT_TYPE, TYPE_TEXT);
			addColumn(COLUMN_TYPE_CODE, TYPE_TEXT);
			addColumn(COLUMN_TAXABLE, TYPE_BOOLEAN);
			addColumn(COLUMN_INVOICEABLE, TYPE_BOOLEAN);
			addColumn(COLUMN_ACTIVE, TYPE_BOOLEAN);
			addColumn(COLUMN_COUNTY, TYPE_TEXT);
			addColumn(COLUMN_ALLOW_AUTO_ATTENDANT, TYPE_BOOLEAN);
			addColumn(COLUMN_TEMPLATE_NAME, TYPE_TEXT);
			addColumn(COLUMN_RECEIPT_COPIES, TYPE_INTEGER);
			addColumn(COLUMN_REQUEST_TEMPLATE, TYPE_BOOLEAN);
			addColumn(COLUMN_REQUEST_TEMPLATE_ON_LAST_COPY, TYPE_BOOLEAN);
			addColumn(COLUMN_REQUIRE_HAULER_ID, TYPE_BOOLEAN);
			addColumn(COLUMN_REQUIRE_TRUCK_ID, TYPE_BOOLEAN);
			addColumn(COLUMN_REQUIRE_TRAILER_ID, TYPE_BOOLEAN);
			addColumn(COLUMN_REQUIRE_DRIVER_ID, TYPE_BOOLEAN);
			addColumn(COLUMN_DEFAULT_HAULER_ID, TYPE_TEXT);
			addColumn(COLUMN_DEFAULT_TRUCK_ID, TYPE_TEXT);
			addColumn(COLUMN_DEFAULT_TRAILER_ID, TYPE_TEXT);
			addColumn(COLUMN_DEFAULT_DRIVER_ID, TYPE_TEXT);
			addColumn(COLUMN_USE_TRUCK_TARE_WEIGHT, TYPE_BOOLEAN);
			addColumn(COLUMN_USE_TRAILER_TARE_WEIGHT, TYPE_BOOLEAN);
			addColumn(COLUMN_DEALER_LICENSE_NUMBER, TYPE_TEXT);
			addColumn(COLUMN_BUSINESS_NAME_ID, TYPE_TEXT);
			addColumn(COLUMN_REGISTERED, TYPE_BOOLEAN);
			addColumn(COLUMN_VALID_ADDRESS, TYPE_BOOLEAN);
			addColumn(COLUMN_RECONCILIATION_TYPE, TYPE_INTEGER);
			addColumn(COLUMN_DL_SEX, TYPE_TEXT);
			addColumn(COLUMN_DL_HEIGHT, TYPE_TEXT);
			addColumn(COLUMN_DL_CLASS, TYPE_TEXT);
			addColumn(COLUMN_DL_WEIGHT, TYPE_TEXT);
			addColumn(COLUMN_DL_HAIR, TYPE_TEXT);
			addColumn(COLUMN_DL_EYES, TYPE_TEXT);
			addColumn(COLUMN_DL_RACE, TYPE_TEXT);
			addColumn(COLUMN_DL_DOB, TYPE_INTEGER);
			addColumn(COLUMN_DL_ISSUE_DATE, TYPE_INTEGER);
			addColumn(COLUMN_DL_EXPIRATION_DATE, TYPE_INTEGER);
			addColumn(COLUMN_CUSTOMER_PHONE_2, TYPE_TEXT);
			addColumn(COLUMN_CUSTOMER_MOBILE, TYPE_TEXT);
			addColumn(COLUMN_CUSTOMER_NOTES, TYPE_TEXT);
			addColumn(COLUMN_CUSTOMER_EMAIL, TYPE_TEXT);
			addColumn(COLUMN_QB_TYPE, TYPE_INTEGER);
			addColumn(COLUMN_INDUSTRIAL, TYPE_BOOLEAN);
			addColumn(COLUMN_LAST_MODIFIED, TYPE_INTEGER + " " + TYPE_NOT_NULL);
			addColumn(COLUMN_TMS_ROW_GUID, TYPE_TEXT);
			setIndexColumn(COLUMN_ID);
		}
	}

	public final class AccountTypesSchema extends Schema {
		public static final String COLUMN_ACCOUNT_TYPE = "AccountType";
		public static final String COLUMN_LAST_MODIFIED = "LastModified";
		public static final String COLUMN_TMS_ROW_GUID = "TMSRowGUID";

		public AccountTypesSchema() {
			super(ACCOUNT_TYPES_TABLE_NAME);
			/////////set up columns
			addColumn(COLUMN_ACCOUNT_TYPE, TYPE_TEXT);
			addColumn(COLUMN_LAST_MODIFIED, TYPE_INTEGER + " " + TYPE_NOT_NULL);
			addColumn(COLUMN_TMS_ROW_GUID, TYPE_TEXT);
			setIndexColumn(COLUMN_ACCOUNT_TYPE);
		}
	}

	public final class CountySchema extends Schema {
		public static final String COLUMN_COUNTY = "County";
		public static final String COLUMN_LAST_MODIFIED = "LastModified";
		public static final String COLUMN_TMS_ROW_GUID = "TMSRowGUID";

		public CountySchema() {
			super(COUNTY_TABLE_NAME);
			/////////set up columns
			addColumn(COLUMN_COUNTY, TYPE_TEXT);
			addColumn(COLUMN_LAST_MODIFIED, TYPE_INTEGER + " " + TYPE_NOT_NULL);
			addColumn(COLUMN_TMS_ROW_GUID, TYPE_TEXT);
			setIndexColumn(COLUMN_COUNTY);
		}
	}

	public final class TicketDetailSchema extends Schema {
		public static final String COLUMN_TICKET_DETAIL_ID = "TicketDetailID";
		public static final String COLUMN_TICKET_ID = "TicketID";
		public static final String COLUMN_PRODUCT_ID = "ProductID";
		public static final String COLUMN_PRODUCT_NAME = "ProductName";
		public static final String COLUMN_INVENTORY_NAME = "InventoryName";
		public static final String COLUMN_TIER = "Tier";
		public static final String COLUMN_PRICE_PER_UOM = "PricePerUOM";
		public static final String COLUMN_UOM = "UOM";
		public static final String COLUMN_UNITS = "Units";
		public static final String COLUMN_UNIT_TOTAL = "UnitTotal";
		public static final String COLUMN_UNIT_TAX = "UnitTax";
		public static final String COLUMN_TOTAL = "Total";
		public static final String COLUMN_MIN_COST = "MinCost";
		public static final String COLUMN_FACTOR = "Factor";
		public static final String COLUMN_GROSS_LBS = "GrossLBS";
		public static final String COLUMN_TARE_LBS = "TareLBS";
		public static final String COLUMN_DEDUCT_LBS = "DeductLBS";
		public static final String COLUMN_NET_LBS = "NetLBS";
		public static final String COLUMN_ALT_GROSS_LBS = "ALT_GrossLBS";
		public static final String COLUMN_ALT_TARE_LBS = "ALT_TareLBS";
		public static final String COLUMN_ALT_DEDUCT_LBS = "ALT_DeductLBS";
		public static final String COLUMN_ALT_NET_LBS = "ALT_NetLBS";
		public static final String COLUMN_PRODUCT_TAXABLE = "ProductTaxable";
		public static final String COLUMN_TIER_CHANGED_BY_USER = "TierChangedByUser";
		public static final String COLUMN_PRICE_PER_UOM_CHANGED_BY_USER = "PricePerUOMChangedByUser";
		public static final String COLUMN_UNITS_CHANGED_BY_USER = "UnitsChangedByUser";
		public static final String COLUMN_IS_TRUCK_SCALE_WEIGHED = "IsTruckScaleWeighed";
		public static final String COLUMN_IS_FLOOR_SCALE_WEIGHED = "IsFloorScaleWeighed";
		public static final String COLUMN_IS_BASED_ON_NET_TONS = "IsBasedOnNetTons";
		public static final String COLUMN_PAY_BY_CHECK = "PayByCheck";
		public static final String COLUMN_RECONCILED = "Reconciled";
		public static final String COLUMN_INVENTORIED = "Inventoried";
		public static final String COLUMN_GROSS_LBS_MODIFIED = "GrossLBSModified";
		public static final String COLUMN_TARE_LBS_MODIFIED = "TareLBSModified";
		public static final String COLUMN_DEDUCT_LBS_MODIFIED = "DeductLBSModified";
		public static final String COLUMN_ALT_GROSS_LBS_MODIFIED = "ALT_GrossLBSModified";
		public static final String COLUMN_ALT_TARE_LBS_MODIFIED = "ALT_TareLBSModified";
		public static final String COLUMN_ALT_DEDUCT_LBS_MODIFIED = "ALT_DeductLBSModified";
		public static final String COLUMN_GROSS_LBS_DT = "GrossLBSDT";
		public static final String COLUMN_TARE_LBS_DT = "TareLBSDT";
		public static final String COLUMN_DEDUCT_LBS_DT = "DeductLBSDT";
		public static final String COLUMN_ALT_GROSS_LBS_DT = "ALT_GrossLBSDT";
		public static final String COLUMN_ALT_TARE_LBS_DT = "ALT_TareLBSDT";
		public static final String COLUMN_ALT_DEDUCT_LBS_DT = "ALT_DeductLBSDT";
		public static final String COLUMN_INVOICE_NUMBER_REF = "InvoiceNumberRef";
		public static final String COLUMN_GROSS_SNAPSHOTS = "GrossSnapShots";
		public static final String COLUMN_TARE_SNAPSHOTS = "TareSnapShots";
		public static final String COLUMN_CUBIC_YARDS = "CubicYards";
		public static final String COLUMN_PAYMENT = "Payment";
		public static final String COLUMN_LAST_MODIFIED = "LastModified";
		public static final String COLUMN_KIT_ID = "KitID";
		public static final String COLUMN_TMS_ROW_GUID = "TMSRowGUID";

		public TicketDetailSchema() {
			super(TICKET_DETAIL_TABLE_NAME);
			///////set up columns
			addColumn(COLUMN_TICKET_DETAIL_ID, TYPE_INTEGER);
			addColumn(COLUMN_TICKET_ID, TYPE_INTEGER + " " + TYPE_NOT_NULL);
			addColumn(COLUMN_PRODUCT_ID, TYPE_TEXT + " " + TYPE_NOT_NULL);
			addColumn(COLUMN_PRODUCT_NAME, TYPE_TEXT);
			addColumn(COLUMN_INVENTORY_NAME, TYPE_TEXT);
			addColumn(COLUMN_TIER, TYPE_TEXT);
			addColumn(COLUMN_PRICE_PER_UOM, TYPE_REAL);
			addColumn(COLUMN_UOM, TYPE_TEXT);
			addColumn(COLUMN_UNITS, TYPE_REAL);
			addColumn(COLUMN_UNIT_TOTAL, TYPE_REAL);
			addColumn(COLUMN_UNIT_TAX, TYPE_REAL);
			addColumn(COLUMN_TOTAL, TYPE_REAL);
			addColumn(COLUMN_MIN_COST, TYPE_REAL);
			addColumn(COLUMN_FACTOR, TYPE_REAL);
			addColumn(COLUMN_GROSS_LBS, TYPE_INTEGER);
			addColumn(COLUMN_TARE_LBS, TYPE_INTEGER);
			addColumn(COLUMN_DEDUCT_LBS, TYPE_INTEGER);
			addColumn(COLUMN_NET_LBS, TYPE_INTEGER);
			addColumn(COLUMN_ALT_GROSS_LBS, TYPE_INTEGER);
			addColumn(COLUMN_ALT_TARE_LBS, TYPE_INTEGER);
			addColumn(COLUMN_ALT_DEDUCT_LBS, TYPE_INTEGER);
			addColumn(COLUMN_ALT_NET_LBS, TYPE_INTEGER);
			addColumn(COLUMN_PRODUCT_TAXABLE, TYPE_BOOLEAN);
			addColumn(COLUMN_TIER_CHANGED_BY_USER, TYPE_BOOLEAN);
			addColumn(COLUMN_PRICE_PER_UOM_CHANGED_BY_USER, TYPE_BOOLEAN);
			addColumn(COLUMN_UNITS_CHANGED_BY_USER, TYPE_BOOLEAN);
			addColumn(COLUMN_IS_TRUCK_SCALE_WEIGHED, TYPE_BOOLEAN);
			addColumn(COLUMN_IS_FLOOR_SCALE_WEIGHED, TYPE_BOOLEAN);
			addColumn(COLUMN_IS_BASED_ON_NET_TONS, TYPE_BOOLEAN);
			addColumn(COLUMN_PAY_BY_CHECK, TYPE_BOOLEAN);
			addColumn(COLUMN_RECONCILED, TYPE_BOOLEAN);
			addColumn(COLUMN_INVENTORIED, TYPE_BOOLEAN);
			addColumn(COLUMN_GROSS_LBS_MODIFIED, TYPE_BOOLEAN);
			addColumn(COLUMN_TARE_LBS_MODIFIED, TYPE_BOOLEAN);
			addColumn(COLUMN_DEDUCT_LBS_MODIFIED, TYPE_BOOLEAN);
			addColumn(COLUMN_ALT_GROSS_LBS_MODIFIED, TYPE_BOOLEAN);
			addColumn(COLUMN_ALT_TARE_LBS_MODIFIED, TYPE_BOOLEAN);
			addColumn(COLUMN_ALT_DEDUCT_LBS_MODIFIED, TYPE_BOOLEAN);
			addColumn(COLUMN_GROSS_LBS_DT, TYPE_INTEGER);
			addColumn(COLUMN_TARE_LBS_DT, TYPE_INTEGER);
			addColumn(COLUMN_DEDUCT_LBS_DT, TYPE_INTEGER);
			addColumn(COLUMN_ALT_GROSS_LBS_DT, TYPE_INTEGER);
			addColumn(COLUMN_ALT_TARE_LBS_DT, TYPE_INTEGER);
			addColumn(COLUMN_ALT_DEDUCT_LBS_DT, TYPE_INTEGER);
			addColumn(COLUMN_INVOICE_NUMBER_REF, TYPE_INTEGER);
			addColumn(COLUMN_GROSS_SNAPSHOTS, TYPE_TEXT);
			addColumn(COLUMN_TARE_SNAPSHOTS, TYPE_TEXT);
			addColumn(COLUMN_CUBIC_YARDS, TYPE_REAL);
			addColumn(COLUMN_PAYMENT, TYPE_REAL);
			addColumn(COLUMN_LAST_MODIFIED, TYPE_INTEGER);
			addColumn(COLUMN_KIT_ID, TYPE_INTEGER);
			addColumn(COLUMN_TMS_ROW_GUID, TYPE_TEXT);
			setIndexColumn(COLUMN_TICKET_DETAIL_ID);
		}
	}

	public final class TicketSchema extends Schema {
		public static final String TABLE_NAME = TICKET_TABLE_NAME;
		public static final String COLUMN_TICKET_ID = "TicketID";
		public static final String COLUMN_SITE = "Site";
		public static final String COLUMN_STATION_ID = "StationID";
		public static final String COLUMN_TICKET_NUMBER = "TicketNumber";
		public static final String COLUMN_TICKET_DATE = "TicketDate";
		public static final String COLUMN_TICKET_TYPE = "TicketType";
		public static final String COLUMN_DIRECTION = "Direction";
		public static final String COLUMN_WEIGHER = "Weigher";
		public static final String COLUMN_CUSTOMER_ID = "CustomerID";
		public static final String COLUMN_CUSTOMER_NAME = "CustomerName";
		public static final String COLUMN_CUSTOMER_ADDRESS = "CustomerAddress";
		public static final String COLUMN_CUSTOMER_CITY = "CustomerCity";
		public static final String COLUMN_CUSTOMER_STATE = "CustomerState";
		public static final String COLUMN_CUSTOMER_ZIP = "CustomerZip";
		public static final String COLUMN_CUSTOMER_ACCOUNT_TYPE = "CustomerAccountType";
		public static final String COLUMN_CUSTOMER_TYPE_CODE = "CustomerTypeCode";
		public static final String COLUMN_CUSTOMER_TAXABLE = "CustomerTaxable";
		public static final String COLUMN_CUSTOMER_INVOICEABLE = "CustomerInvoiceable";
		public static final String COLUMN_COUNTY = "County";
		public static final String COLUMN_HAULER_ID = "HaulerID";
		public static final String COLUMN_HAULER_NAME = "HaulerName";
		public static final String COLUMN_HAULER_ADDRESS = "HaulerAddress";
		public static final String COLUMN_HAULER_CITY = "HaulerCity";
		public static final String COLUMN_HAULER_STATE = "HaulerState";
		public static final String COLUMN_HAULER_ZIP = "HaulerZip";
		public static final String COLUMN_DRIVER_ID = "DriverID";
		public static final String COLUMN_DRIVER_NAME = "DriverName";
		public static final String COLUMN_TRUCK_ID	 = "TruckID";
		public static final String COLUMN_TRUCK_NUMBER = "TruckNumber";
		public static final String COLUMN_TRUCK_TARE_WEIGHT = "TruckTareWeight";
		public static final String COLUMN_TRUCK_LAST_TARE_DATE = "TruckLastTareDate";
		public static final String COLUMN_TRAILER_ID = "TrailerID";
		public static final String COLUMN_TRAILER_NUMBER = "TrailerNumber";
		public static final String COLUMN_TRAILER_TARE_WEIGHT = "TrailerTareWeight";
		public static final String COLUMN_TRAILER_LAST_TARE_DATE = "TrailerLastTareDate";
		public static final String COLUMN_CONTRACT_ID = "ContractID";
		public static final String COLUMN_CONTRACT_NAME = "ContractName";
		public static final String COLUMN_LOCATION_NAME = "LocationName";
		public static final String COLUMN_BUSINESS_UNIT = "BusinessUnit";
		public static final String COLUMN_JOB_NUMBER = "JobNumber";
		public static final String COLUMN_PERMIT_NUMBER = "PermitNumber";
		public static final String COLUMN_INVOICE_NUMBER = "InvoiceNumber";
		public static final String COLUMN_INVOICE_CLEARED = "InvoiceCleared";
		public static final String COLUMN_INVOICE_DATE = "InvoiceDate";
		public static final String COLUMN_COMPLETED = "Completed";
		public static final String COLUMN_REGISTERED = "Registered";
		public static final String COLUMN_VALID_ADDRESS = "ValidAddress";
		public static final String COLUMN_VOIDED = "Voided";
		public static final String COLUMN_TICKET_PRINTED = "TicketPrinted";
		public static final String COLUMN_RECONCILIATION_TYPE = "ReconciliationType";
		public static final String COLUMN_NOTE = "Note";
		public static final String COLUMN_DEALER_LICENSE_NUMBER = "DealerLicenseNumber";
		public static final String COLUMN_BUSINESS_NAME_ID = "BusinessNameID";
		public static final String COLUMN_TOW_MAKE_MODEL = "TOWMakeModel";
		public static final String COLUMN_TOW_LICENSE_NUMBER = "TOWLicenseNumber";
		public static final String COLUMN_VIN_NUMBER_1 = "VINNumber1";
		public static final String COLUMN_VIN_NUMBER_2 = "VINNumber2";
		public static final String COLUMN_VIN_NUMBER_3 = "VINNumber3";
		public static final String COLUMN_VIN_NUMBER_4 = "VINNumber4";
		public static final String COLUMN_VIN_NUMBER_5 = "VINNumber5";
		public static final String COLUMN_VIN_NUMBER_6 = "VINNumber6";
		public static final String COLUMN_VIN_NUMBER_7 = "VINNumber7";
		public static final String COLUMN_VIN_NUMBER_8 = "VINNumber8";
		public static final String COLUMN_VIN_MAKE_MODEL_1 = "VINMakeModel1";
		public static final String COLUMN_VIN_MAKE_MODEL_2 = "VINMakeModel2";
		public static final String COLUMN_VIN_MAKE_MODEL_3 = "VINMakeModel3";
		public static final String COLUMN_VIN_MAKE_MODEL_4 = "VINMakeModel4";
		public static final String COLUMN_VIN_MAKE_MODEL_5 = "VINMakeModel5";
		public static final String COLUMN_VIN_MAKE_MODEL_6 = "VINMakeModel6";
		public static final String COLUMN_VIN_MAKE_MODEL_7 = "VINMakeModel7";
		public static final String COLUMN_VIN_MAKE_MODEL_8 = "VINMakeModel8";
		public static final String COLUMN_PAYMENT_METHOD = "PaymentMethod";
		public static final String COLUMN_CHECK_NUMBER = "CheckNumber";
		public static final String COLUMN_BANK_NAME = "BankName";
		public static final String COLUMN_CHECK_PAY_TO_ORDER = "CheckPayToOrder";
		public static final String COLUMN_VOUCHER_REDEEMABLE_DATE = "VoucherRedeemableDate";
		public static final String COLUMN_VOUCHER_EXPIRATION_DATE = "VoucherExpirationDate";
		public static final String COLUMN_HOLD_CHECK = "HoldCheck";
		public static final String COLUMN_PENDING = "Pending";
		public static final String COLUMN_INDUSTRIAL = "Industrial";
		public static final String COLUMN_SIGNATURE_PICTURE = "SignaturePicture";
		public static final String COLUMN_SIGNATURE_SNAPSHOT = "SignatureSnapShot";
		public static final String COLUMN_PAYMENT_DATE = "PaymentDate";
		public static final String COLUMN_CASH_NUMBER = "CashNumber";
		public static final String COLUMN_CASH_DRAWER_NAME = "CashDrawerName";
		public static final String COLUMN_LAST_MODIFIED = "LastModified";
		public static final String COLUMN_TMS_ROW_GUID = "TMSRowGUID";

		public TicketSchema() {
			super(TICKET_TABLE_NAME);
			/////////set up columns
			addColumn(COLUMN_TICKET_ID, TYPE_INTEGER);
			addColumn(COLUMN_SITE, TYPE_TEXT);
			addColumn(COLUMN_STATION_ID, TYPE_INTEGER + " " + TYPE_NOT_NULL);
			addColumn(COLUMN_TICKET_NUMBER, TYPE_INTEGER + " " + TYPE_NOT_NULL);
			addColumn(COLUMN_TICKET_DATE, TYPE_INTEGER);
			addColumn(COLUMN_TICKET_TYPE, TYPE_TEXT);
			addColumn(COLUMN_DIRECTION, TYPE_TEXT);
			addColumn(COLUMN_WEIGHER, TYPE_TEXT);
			addColumn(COLUMN_CUSTOMER_ID, TYPE_TEXT + " " + TYPE_NOT_NULL);
			addColumn(COLUMN_CUSTOMER_NAME, TYPE_TEXT);
			addColumn(COLUMN_CUSTOMER_ADDRESS, TYPE_TEXT);
			addColumn(COLUMN_CUSTOMER_CITY, TYPE_TEXT);
			addColumn(COLUMN_CUSTOMER_STATE, TYPE_TEXT);
			addColumn(COLUMN_CUSTOMER_ZIP, TYPE_TEXT);
			addColumn(COLUMN_CUSTOMER_ACCOUNT_TYPE, TYPE_TEXT);
			addColumn(COLUMN_CUSTOMER_TYPE_CODE, TYPE_TEXT);
			addColumn(COLUMN_CUSTOMER_TAXABLE, TYPE_BOOLEAN);
			addColumn(COLUMN_CUSTOMER_INVOICEABLE, TYPE_BOOLEAN);
			addColumn(COLUMN_COUNTY, TYPE_TEXT);
			addColumn(COLUMN_HAULER_ID, TYPE_TEXT);
			addColumn(COLUMN_HAULER_NAME, TYPE_TEXT);
			addColumn(COLUMN_HAULER_ADDRESS, TYPE_TEXT);
			addColumn(COLUMN_HAULER_CITY, TYPE_TEXT);
			addColumn(COLUMN_HAULER_STATE, TYPE_TEXT);
			addColumn(COLUMN_HAULER_ZIP, TYPE_TEXT);
			addColumn(COLUMN_DRIVER_ID, TYPE_TEXT);
			addColumn(COLUMN_DRIVER_NAME, TYPE_TEXT);
			addColumn(COLUMN_TRUCK_ID, TYPE_TEXT);
			addColumn(COLUMN_TRUCK_NUMBER, TYPE_TEXT);
			addColumn(COLUMN_TRUCK_TARE_WEIGHT, TYPE_INTEGER);
			addColumn(COLUMN_TRUCK_LAST_TARE_DATE, TYPE_INTEGER);
			addColumn(COLUMN_TRAILER_ID, TYPE_TEXT);
			addColumn(COLUMN_TRAILER_NUMBER, TYPE_TEXT);
			addColumn(COLUMN_TRAILER_TARE_WEIGHT, TYPE_INTEGER);
			addColumn(COLUMN_TRAILER_LAST_TARE_DATE, TYPE_INTEGER);
			addColumn(COLUMN_CONTRACT_ID, TYPE_TEXT);
			addColumn(COLUMN_CONTRACT_NAME, TYPE_TEXT);
			addColumn(COLUMN_LOCATION_NAME, TYPE_TEXT);
			addColumn(COLUMN_BUSINESS_UNIT, TYPE_TEXT);
			addColumn(COLUMN_JOB_NUMBER, TYPE_TEXT);
			addColumn(COLUMN_PERMIT_NUMBER, TYPE_TEXT);
			addColumn(COLUMN_INVOICE_NUMBER, TYPE_INTEGER);
			addColumn(COLUMN_INVOICE_CLEARED, TYPE_BOOLEAN);
			addColumn(COLUMN_INVOICE_DATE, TYPE_INTEGER);
			addColumn(COLUMN_COMPLETED, TYPE_BOOLEAN);
			addColumn(COLUMN_REGISTERED, TYPE_BOOLEAN);
			addColumn(COLUMN_VALID_ADDRESS, TYPE_BOOLEAN);
			addColumn(COLUMN_VOIDED, TYPE_BOOLEAN);
			addColumn(COLUMN_TICKET_PRINTED, TYPE_BOOLEAN);
			addColumn(COLUMN_RECONCILIATION_TYPE, TYPE_INTEGER);
			addColumn(COLUMN_NOTE, TYPE_TEXT);
			addColumn(COLUMN_DEALER_LICENSE_NUMBER, TYPE_TEXT);
			addColumn(COLUMN_BUSINESS_NAME_ID, TYPE_TEXT);
			addColumn(COLUMN_TOW_MAKE_MODEL, TYPE_TEXT);
			addColumn(COLUMN_TOW_LICENSE_NUMBER, TYPE_TEXT);
			addColumn(COLUMN_VIN_NUMBER_1, TYPE_TEXT);
			addColumn(COLUMN_VIN_NUMBER_2, TYPE_TEXT);
			addColumn(COLUMN_VIN_NUMBER_3, TYPE_TEXT);
			addColumn(COLUMN_VIN_NUMBER_4, TYPE_TEXT);
			addColumn(COLUMN_VIN_NUMBER_5, TYPE_TEXT);
			addColumn(COLUMN_VIN_NUMBER_6, TYPE_TEXT);
			addColumn(COLUMN_VIN_NUMBER_7, TYPE_TEXT);
			addColumn(COLUMN_VIN_NUMBER_8, TYPE_TEXT);
			addColumn(COLUMN_VIN_MAKE_MODEL_1, TYPE_TEXT);
			addColumn(COLUMN_VIN_MAKE_MODEL_2, TYPE_TEXT);
			addColumn(COLUMN_VIN_MAKE_MODEL_3, TYPE_TEXT);
			addColumn(COLUMN_VIN_MAKE_MODEL_4, TYPE_TEXT);
			addColumn(COLUMN_VIN_MAKE_MODEL_5, TYPE_TEXT);
			addColumn(COLUMN_VIN_MAKE_MODEL_6, TYPE_TEXT);
			addColumn(COLUMN_VIN_MAKE_MODEL_7, TYPE_TEXT);
			addColumn(COLUMN_VIN_MAKE_MODEL_8, TYPE_TEXT);
			addColumn(COLUMN_PAYMENT_METHOD, TYPE_TEXT);
			addColumn(COLUMN_CHECK_NUMBER, TYPE_INTEGER);
			addColumn(COLUMN_BANK_NAME, TYPE_TEXT);
			addColumn(COLUMN_CHECK_PAY_TO_ORDER, TYPE_TEXT);
			addColumn(COLUMN_VOUCHER_REDEEMABLE_DATE, TYPE_INTEGER);
			addColumn(COLUMN_VOUCHER_EXPIRATION_DATE, TYPE_INTEGER);
			addColumn(COLUMN_HOLD_CHECK, TYPE_BOOLEAN);
			addColumn(COLUMN_PENDING, TYPE_BOOLEAN);
			addColumn(COLUMN_INDUSTRIAL, TYPE_BOOLEAN);
			addColumn(COLUMN_SIGNATURE_PICTURE, TYPE_TEXT);
			addColumn(COLUMN_SIGNATURE_SNAPSHOT, TYPE_TEXT);
			addColumn(COLUMN_PAYMENT_DATE, TYPE_INTEGER);
			addColumn(COLUMN_CASH_NUMBER, TYPE_INTEGER);
			addColumn(COLUMN_CASH_DRAWER_NAME, TYPE_TEXT);
			addColumn(COLUMN_LAST_MODIFIED, TYPE_INTEGER);
			addColumn(COLUMN_TMS_ROW_GUID, TYPE_TEXT);
			setIndexColumn(COLUMN_TICKET_ID);
		}
	}
}