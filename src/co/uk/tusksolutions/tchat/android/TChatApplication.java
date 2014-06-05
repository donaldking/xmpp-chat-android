/**
 * 
 */
package co.uk.tusksolutions.tchat.android;

import org.jivesoftware.smack.Connection;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.widget.Toast;
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

	@Override
	public void onCreate() {
		super.onCreate();
		TChatApplication.mContext = getBaseContext();

		if (isMainServiceRunning == false) {
			startService(new Intent(this, MainService.class));
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

	@Override
	public void onTerminate() {
	}

}