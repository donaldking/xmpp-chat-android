package co.uk.tusksolutions.tchat.android.constants;

public class Constants {

	/**
	 * Server End Points
	 */
	public static String CURRENT_SERVER = null;
	public static String PROXY_SERVER = null;
	public static String XMPP_RESOURCE = null;
	public static String SENDER_ID = "637301192685";
	public static final String DEVELOPMENT_SERVER = "dev.yookoschat.com";
	public static final String STAGING_SERVER = "uat.yookoschat.com";
	public static final String PRODUCTION_SERVER = "yookoschat.com";
	public static String HTTP_SCHEME = "https://";
	public static String PROXY_PATH = "/service/proxy/proxy.yookos.php?path=/people/";
	public static String RECENTS_ENDPOINT = "/mobileservices/v1/get_recents.php";
	public static String CLOUD_STORAGE_ENDPOINT = "/mobileservices/v1/store_message.php";
	public static String REGISTER_PUSH_ENDPOINT = "/mobileservices/v1/register_device_gcm.php";
	public static String UNREGISTER_PUSH_ENDPOINT = "/mobileservices/v1/deactivate_device_gcm.php";
	public static String CHAT_MESSAGES_ENDPOINT = "/mobileservices/v1/get_message.php";
	public static final String GET_PROFILE_ENDPOINT = "/mobileservices/v1/get_profile.php";
	public static final String GET_GROUPS_ENDPOINT = "/mobileservices/v1/get_groups_for_user.php";
	public static final String GET_CHATROOMS_ENDPOINT = "/mobileservices/v1/get_chat_rooms.php";
	public static final String GET_LAST_ONLINE_TIME_ENDPOINT = "/mobileservices/v1/get_last_online_time.php";
	public static final String UPDATE_LAST_ONLINE_PRIVACY_ENDPOINT = "/mobileservices/v1/update_last_online_privacy.php";
	public static final String CREATE_GROUP_ENDPOINT = "/mobileservices/v1/create_group.php";
	public static final String ADD_USER_TO_GROUP_ENDPOINT = "/mobileservices/v1/add_user_to_group.php";
	public static final String UPLOAD_FILE_ENDPOINT = "/mobileservices/v1/upload.php";
    public static final String CLEAR_CHAT_ENDPOINT="/mobileservices/v1/delete_chat.php";
    public static final String DELETE_GROUP_ENDPOINT="/mobileservices/v1/delete_group.php";
    
    public static final String DOWNLOAD_CHAT_HISTORY_EXCEL_ENDPOINT="/mobileservices/v1/chat_download_excel.php";
    public static final String DOWNLOAD_CHAT_HISTORY_TEXT_ENDPOINT="/mobileservices/v1/chat_download_text.php";
	public static String KICK_USER_FROM_GROUP_ENDPOINT = "/mobileservices/v1/kick_user_out_of_group.php";
	public static String KICK_USER_FROM_CHAT_ROOM_ENDPOINT = "/mobileservices/v1/kick_user_out_of_chat_room.php";
    public static String CREATE_CHATROOM_ENDPOINT="/mobileservices/v1/create_chat_room.php";
	public static String GET_CHAT_ROOM_PARTICIPANTS_ENDPOINT="/mobileservices/v1/get_chat_room_participants.php";
    
    /**
	 * Login Actions
	 */
	public static String LOGIN_SUCCESSFUL = "co.uk.tusksolutions.tchat.android.action.LOGIN_SUCCESSFUL";
	public static String LOGIN_UNSUCCESSFUL = "co.uk.tusksolutions.tchat.android.action.LOGIN_UNSUCCESSFUL";

	/**
	 * Chat / Packet Actions
	 */
	public static final String MESSAGE_RECEIVED = "co.uk.tusksolutions.tchat.android.action.CHAT_MESSAGE_RECEIVED";

	public static String MESSAGE_READY = "co.uk.tusksolutions.tchat.android.action.CHAT_MESSAGE_READY";
	public static String OPEN_FOR_NEW_CHAT_RECEIVED = "co.uk.tusksolutions.tchat.android.action.OPEN_FOR_NEW_CHAT_RECEIVED";
	public static String CHAT_MESSAGE_EMPTY = "co.uk.tusksolutions.tchat.android.action.CHAT_MESSAGE_EMPTY";

	public static final String USER_PRESENCE_CHANGED = "co.uk.tusksolutions.tchat.android.action.USER_PRESENCE_CHANGED";

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
	public static String GROUPS_UPDATED = "co.uk.tusksolutions.tchat.android.action.GROUPS_UPDATED";
	public static String GROUPS_NOT_UPDATED = "co.uk.tusksolutions.tchat.android.action.GROUPS_NOT_UPDATED";
	public static String DISPLAY_MESSAGE_ACTION = "co.uk.tusksolutions.tchat.android.action.DISPLAY_MESSAGE";
	public static String PROFILE_UPDATED = "co.uk.tusksolutions.tchat.android.action.PROFILE_UPDATED";
	public static String PROFILE_NOT_UPDATED = "co.uk.tusksolutions.tchat.android.action.PROFILE_NOT_UPDATED";
	public static String LAST_ONLINE_TIME_STATE_CHANGED = "co.uk.tusksolutions.tchat.android.action.LAST_ONLINE_TIME_STATE_CHANGED";
	public static String BANNED_FROM_ROOM = "co.uk.tusksolutions.tchat.android.action.BANNED_FROM_ROOM";

}
