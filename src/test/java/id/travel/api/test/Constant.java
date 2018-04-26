package id.travel.api.test;

public class Constant {

	public static final String VIEW_LOGIN = "login";
	public static final String VIEW_SHOP = "shop";
	public static final String VIEW_CART_DETAIL = "cart_detail";
	public static final String VIEW_AGENT_SHIPMENT = "shippment_address";
	public static final String VIEW_MY_PROFILE = "my_profile";
	public static final String VIEW_CONFIRM_PAGE = "confirm_page";
	public static final String VIEW_MY_TRANSACTION = "my_transaction";
	public static final String ADMIN_TRX_STATS = "adm_trx_stats";
	public static final String VIEW_USERS_ADMIN = "users_management";
	public static final String VIEW_USERS_TRANSACTION = "transactions";
	public static final String VIEW_WALLET_TRANSACTION = "wallet_transaction";
	public static final String VIEW_SHOP_ITEM = "shop_item";
	
	public static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	public static final String PLACES_API_GEOCODE = "https://maps.googleapis.com/maps/api/geocode";
	public static final String API_KEY = "AIzaSyAFj7YunZys5V1taEviGXN6p6-bc2McR9M";
	public static final String OUT_JSON = "/json";
	public static final String GMAP_API_KEY = "AIzaSyBtcT4UkbfN9JV_4haAAYdQnhS-3wsedpk";
	public static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	public static final String VALIDATOR_REGEX_URL = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
	public static final String VALIDATOR_REGEX_AMOUNT = "\\d+";

	public static final String SECRET = "w4rung1k4n_6r3g_oSbxNOGBs9_X0X0";
	public static final String TOKEN_PREFIX = "Bearer ";
	
	
	public static final String WS_URL ="http://localhost:8888/";
	public static final String WS_LOGIN_URL = WS_URL+"login";
	public static final String WS_GET_ALL_USER_URL = WS_URL+"admin/user";
	public static final String WS_CREATE_USER_AGENT_URL = WS_URL+"admin/user/agent";
	public static final String WS_CHECK_USER_AS_ADMIN_URL = WS_URL+"admin/user";
	public static final String WS_UPDATE_USER_AGENT_URL = WS_URL+"admin/user";
	public static final String WS_DELETE_USER_URL = WS_URL+"admin/user";
	
	public static final String WS_UPDATE_SELF_USER_URL = WS_URL+"user";
	public static final String WS_CHECK_USER_AS_USER_URL = WS_URL+"user";
	public static final String WS_CREATE_USER_CUSTOMER_URL = WS_URL+"admin/user/customer";
	public static final String WS_UPDATE_CHANGE_PWD_URL = WS_URL+"user/change_password";
	
	public static final String AGENT_DATA_KEY_PRICE_PER_KM = "price_per_km";
	
}
