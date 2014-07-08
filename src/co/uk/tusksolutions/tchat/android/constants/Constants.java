package co.uk.tusksolutions.tchat.android.constants;

public class Constants {

	/**
	 * Server End Points
	 */
	public static String CURRENT_SERVER = null;
	public static String PROXY_SERVER = null;
	public static final String STAGING_SERVER = "uat.yookoschat.com";
	public static final String PRODUCTION_SERVER = "yookoschat.com";
	public static String HTTP_SCHEME = "https://";
	public static String PROXY_PATH = "/service/proxy/proxy.yookos.php?path=/people/";
	public static String RECENTS_ENDPOINT = "/mobileservices/v1/get_recents.php";
	public static String CLOUD_STORAGE_ENDPOINT = "/mobileservices/v1/store_message.php";

	/**
	 * Login Actions
	 */
	public static String LOGIN_SUCCESSFUL = "co.uk.tusksolutions.tchat.android.action.LOGIN_SUCCESSFUL";
	public static String LOGIN_UNSUCCESSFUL = "co.uk.tusksolutions.tchat.android.action.LOGIN_UNSUCCESSFUL";

	/**
	 * Chat / Packet Actions
	 */
	public static final String CHAT_MESSAGE_RECEIVED = "co.uk.tusksolutions.tchat.android.action.CHAT_MESSAGE_RECEIVED";
	public static String CHAT_MESSAGE_READY = "co.uk.tusksolutions.tchat.android.action.CHAT_MESSAGE_READY";
	public static String OPEN_FOR_NEW_CHAT_RECEIVED = "co.uk.tusksolutions.tchat.android.action.OPEN_FOR_NEW_CHAT_RECEIVED";

	/**
	 * Network Connection Actions
	 */
	public static String CONNECTION_CLOSED_IN_ERROR = "co.uk.tusksolutions.tchat.android.action.CONNECTION_LOST_IN_ERROR";
	public static String CONNECTION_CLOSED_BY_USER = "co.uk.tusksolutions.tchat.android.action.CONNECTION_LOST_BY_USER_ACTION";
	public static String RECONNECTING = "co.uk.tusksolutions.tchat.android.action.RECONNECTING";
	public static String RECONNECTING_FAILED = "co.uk.tusksolutions.tchat.android.action.RECONNECTING_FAILED";
	public static String RECONNECTION_SUCCESSFULL = "co.uk.tusksolutions.tchat.android.action.RECONNECTION_SUCCESSFULL";
	public static String ROSTER_UPDATED = "co.uk.tusksolutions.tchat.android.action.ROSTER_UPDATED";
	public static String ROSTER_EMPTY = "co.uk.tusksolutions.tchat.android.action.ROSTER_EMPTY";
	public static String RECENTS_EMPTY = "co.uk.tusksolutions.tchat.android.action.RECENTS_EMPTY";
	public static String RECENTS_UPDATED = "co.uk.tusksolutions.tchat.android.action.RECENTS_UPDATED";
	public static String CLOUD_SAVE_SUCCESS = "co.uk.tusksolutions.tchat.android.action.CLOUD_SAVE_SUCCESS";
	public static String CLOUD_SAVE_ERROR = "co.uk.tusksolutions.tchat.android.action.CLOUD_SAVE_ERROR";

}
