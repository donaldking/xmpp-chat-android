/**
 * 
 */
package co.uk.tusksolutions.tchat.android;

import java.util.UUID;

import org.jivesoftware.smack.XMPPConnection;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Log;
import co.uk.tusksolutions.gcm.APIRegisterPushNotifications;
import co.uk.tusksolutions.gcm.APIUnRegisterPushNotifications;
import co.uk.tusksolutions.tchat.android.activities.LoginActivity;
import co.uk.tusksolutions.tchat.android.activities.MainActivity;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.dbHelper.TChatDBHelper;
import co.uk.tusksolutions.tchat.android.models.ChatMessagesModel;
import co.uk.tusksolutions.tchat.android.models.RecentsModel;
import co.uk.tusksolutions.tchat.android.models.RosterModel;
import co.uk.tusksolutions.tchat.android.models.UserModel;
import co.uk.tusksolutions.tchat.android.xmpp.XMPPConnectionManager;

public class TChatApplication extends Application {

	final static String TAG = "TChatApplication";

	private static CHAT_STATUS_ENUM chatActivityStatus = CHAT_STATUS_ENUM.NOT_VISIBLE;
	private static Context mContext;
	public static XMPPConnection connection;
	private static TChatDBHelper tChatDBHelper;
	public SQLiteDatabase tChatDBWritable;
	public SQLiteDatabase tChatDBReadable;
	private static UserModel mUserModel;
	private static RosterModel mRosterModel;
	private static RecentsModel mRecentsModel;
	
	private static ChatMessagesModel mChatMessagesModel;
	public static String chatSessionBuddy;
	public static int CHAT_SECTION_QUERY_ACTION;
	public static int CHATROOM_SECTION_QUERY_ACTION;
	public static Handler presenceDialogHandler;
	public static boolean chatNotificationSound = true;
	public static boolean showLastSeenOnline = true;

	@Override
	public void onCreate() {
		super.onCreate();
		// Constants.CURRENT_SERVER = Constants.DEVELOPMENT_SERVER;
		Constants.CURRENT_SERVER = Constants.STAGING_SERVER;
		// Constants.CURRENT_SERVER = Constants.PRODUCTION_SERVER;
		Constants.PROXY_SERVER = Constants.HTTP_SCHEME
				+ Constants.CURRENT_SERVER + Constants.PROXY_PATH;

		TChatApplication.mContext = getApplicationContext();
		tChatDBHelper = new TChatDBHelper(TChatApplication.getContext());
		TChatApplication.getTChatDBWritable();
		mUserModel = new UserModel();
		mRosterModel = new RosterModel();
		mRecentsModel = new RecentsModel();
		mChatMessagesModel = new ChatMessagesModel();
		;

		/**
		 * This method makes sure we have network and can login. If so, send us
		 * TO_USER MainActivity, otherwise, send us TO_USER LoginActivity
		 */
		if (!isNetworkAvailable()) {
			Log.d(TAG, "No internet connection");
		}
	}

	public static boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager) TChatApplication
				.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null
				&& activeNetwork.isConnectedOrConnecting();

		return isConnected;
	}

	public static String getMid() {
		return UUID.randomUUID().toString();
	}

	public static String getCurrentJid() {
		return getUserModel().getUsername() + "@" + Constants.CURRENT_SERVER;
	}

	public static Context getContext() {
		return mContext;
	}

	public static void setChatActivityStatus(CHAT_STATUS_ENUM currentStatus) {
		chatActivityStatus = currentStatus;
	}

	public static CHAT_STATUS_ENUM getChatActivityStatus() {

		return chatActivityStatus;
	}

	public enum CHAT_STATUS_ENUM {
		VISIBLE, NOT_VISIBLE,
	}

	public synchronized static TChatDBHelper getTChatDBHelper() {
		return tChatDBHelper;
	}

	public static boolean isChatNotificationSound() {
		return chatNotificationSound;
	}

	public static void setChatNotificationSound(boolean chatNotificationSound) {
		TChatApplication.chatNotificationSound = chatNotificationSound;
	}

	public static boolean isShowLastSeenOnline() {
		return showLastSeenOnline;
	}

	public static void setShowLastSeenOnline(boolean showLastSeenOnline) {
		TChatApplication.showLastSeenOnline = showLastSeenOnline;
	}

	public static UserModel getUserModel() {
		return mUserModel;
	}

	public static RosterModel getRosterModel() {
		return mRosterModel;
	}

	public static RecentsModel getRecentsModel() {
		return mRecentsModel;
	}

	public static ChatMessagesModel getChatMessagesModel() {
		return mChatMessagesModel;
	}


	public synchronized static SQLiteDatabase getTChatDBWritable() {
		return getTChatDBHelper().getWritableDatabase();
	}

	public synchronized static SQLiteDatabase getTChatDBReadable() {
		return getTChatDBHelper().getReadableDatabase();
	}

	public static void reconnect() {
		XMPPConnectionManager.connect(TChatApplication.getUserModel()
				.getUsername(), TChatApplication.getUserModel().getPassword());
	}

	public static void tearDownAndLogout() {
		TChatApplication.unRegisterForPush();
		TChatApplication.getRosterModel().deleteRosterRecords();
		TChatApplication.getRecentsModel().deleteRecents();
		TChatApplication.getChatMessagesModel().deleteAllChats();
		TChatApplication.getUserModel().deleteProfile();
	
		try {
			TChatApplication.connection.disconnect();
			TChatApplication.connection = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			
			TChatApplication.getContext().startActivity(
					new Intent(TChatApplication.getContext(),
							LoginActivity.class)
							.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
			
			
		}
	}

	public static void registerForPush(String registrationId) {
		APIRegisterPushNotifications regObject = new APIRegisterPushNotifications();
		String device_id = Secure.getString(TChatApplication.getContext()
				.getContentResolver(), Secure.ANDROID_ID);

		regObject.doRegisterPushNotifications(registrationId, TChatApplication
				.getUserModel().getUsername(), device_id);
	}

	public static void unRegisterForPush() {
		APIUnRegisterPushNotifications unRegObject = new APIUnRegisterPushNotifications();
		String device_id = Secure.getString(TChatApplication.getContext()
				.getContentResolver(), Secure.ANDROID_ID);

		unRegObject.doUnRegisterPushNotifications(device_id);
	}

	

	
}