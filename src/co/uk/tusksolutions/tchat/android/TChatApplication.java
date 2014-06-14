/**
 * 
 */
package co.uk.tusksolutions.tchat.android;

import org.jivesoftware.smack.Connection;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PowerManager;
import android.widget.Toast;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.dbHelper.TChatDBHelper;
import co.uk.tusksolutions.tchat.android.models.RosterModel;
import co.uk.tusksolutions.tchat.android.models.UserModel;
import co.uk.tusksolutions.tchat.android.services.MainService;

public class TChatApplication extends Application {

	final static String TAG = "TChatApplication";

	private static CHAT_STATUS_ENUM chatActivityStatus = CHAT_STATUS_ENUM.NOT_VISIBLE;
	private boolean loginStatus;
	public boolean isDebug = true;
	private static Context mContext;
	public static Connection connection;
	public static boolean isMainServiceRunning;
	private static PowerManager.WakeLock wakeLock;

	private static TChatDBHelper tChatDBHelper;
	public SQLiteDatabase tChatDBWritable;
	public SQLiteDatabase tChatDBReadable;
	private static UserModel mUserModel;
	private static RosterModel mRosterModel;
	public static PendingIntent connectionMonitoringOperation;

	@Override
	public void onCreate() {
		super.onCreate();
		Constants.CURRENT_SERVER = Constants.STAGING_SERVER;
		Constants.PROXY_SERVER = Constants.HTTP_SCHEME
				+ Constants.CURRENT_SERVER + Constants.PROXY_PATH;

		TChatApplication.mContext = getBaseContext();
		tChatDBHelper = new TChatDBHelper(TChatApplication.getContext());
		TChatApplication.getTChatDBWritable();
		mUserModel = new UserModel();
		mRosterModel = new RosterModel();

		if (isNetworkAvailable()) {
			/*
			 * Only start service automatically if we have username present in
			 * the database. Otherwise, do nothing!
			 */
			if (isMainServiceRunning == false
					&& TChatApplication.getUserModel().getUsername() != null) {
				startService(new Intent(this, MainService.class));
			}
		} else {
			Toast.makeText(TChatApplication.getContext(),
					"No Internet connection", Toast.LENGTH_LONG).show();
		}

	}

	private static boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager) TChatApplication
				.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null
				&& activeNetwork.isConnectedOrConnecting();

		return isConnected;
	}

	public static void acquireWakeLock() {
		PowerManager pm = (PowerManager) getContext().getSystemService(
				Context.POWER_SERVICE);
		wakeLock = pm
				.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My wakelook");
		wakeLock.acquire();
		Toast acquire = Toast.makeText(getContext(), "Wake Lock ON",
				Toast.LENGTH_SHORT);
		acquire.show();
	}

	public static void releaseWakeLock() {
		wakeLock.release();
		Toast release = Toast.makeText(getContext(), "Wake Lock OFF",
				Toast.LENGTH_SHORT);
		release.show();
	}

	public static Context getContext() {
		return mContext;
	}

	public void setLoginStatus(boolean status) {
		this.loginStatus = status;
	}

	public boolean getLoginStatus() {
		return loginStatus;
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

	public static UserModel getUserModel() {
		return mUserModel;
	}

	public static RosterModel getRosterModel() {
		return mRosterModel;
	}

	public synchronized static SQLiteDatabase getTChatDBWritable() {
		return getTChatDBHelper().getWritableDatabase();
	}

	public synchronized static SQLiteDatabase getTChatDBReadable() {
		return getTChatDBHelper().getReadableDatabase();
	}

	@Override
	public void onTerminate() {
	}

	public static void tearDownAndLogout() {
		try {
			TChatApplication.getRosterModel().deleteRosterRecords();
			TChatApplication.getContext().sendBroadcast(
					new Intent(Constants.LOGIN_UNSUCCESSFUL));
			TChatApplication.getContext().stopService(
					new Intent(TChatApplication.getContext(), MainService.class));
			TChatApplication.getUserModel().deleteProfile();
			AlarmManager alarmManager = (AlarmManager) TChatApplication
					.getContext().getSystemService(Context.ALARM_SERVICE);
			alarmManager.cancel(TChatApplication.connectionMonitoringOperation);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}