/**
 * 
 */
package co.uk.tusksolutions.tchat.android;

import org.jivesoftware.smack.Connection;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.PowerManager;
import android.widget.Toast;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.dbHelper.TChatDBHelper;
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
	public static PendingIntent connectionMonitoringOperation;

	@Override
	public void onCreate() {
		super.onCreate();
		Constants.CURRENT_SERVER = Constants.STAGING_SERVER;

		TChatApplication.mContext = getBaseContext();
		tChatDBHelper = new TChatDBHelper(TChatApplication.getContext());
		TChatApplication.getTChatDBWritable();

		mUserModel = new UserModel();

		if (getUserModel().getUsername() == null) {
			/*
			 * Show Login screen
			 */
			startActivity(new Intent(this, LoginActivity.class)
					.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
			return;

		} else {
			/*
			 * Username present. Its safe to commence login using default
			 * password
			 */
			if (isMainServiceRunning == false) {
				startService(new Intent(this, MainService.class));
			}
		}
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

	public synchronized static SQLiteDatabase getTChatDBWritable() {
		return getTChatDBHelper().getWritableDatabase();
	}

	public synchronized SQLiteDatabase getTChatDBReadable() {
		return getTChatDBHelper().getReadableDatabase();
	}

	public static UserModel getUserModel() {
		return mUserModel;
	}

	@Override
	public void onTerminate() {
	}

}