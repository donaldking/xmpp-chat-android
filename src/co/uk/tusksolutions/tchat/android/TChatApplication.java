/**
 * 
 */
package co.uk.tusksolutions.tchat.android;

import org.jivesoftware.smack.Connection;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
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

	@Override
	public void onCreate() {
		super.onCreate();
		TChatApplication.mContext = this;

		if (isMainServiceRunning == false) {
			Toast.makeText(getApplicationContext(),
					(String) TAG + " onCreate - Trigger connection...",
					Toast.LENGTH_LONG).show();
			startService(new Intent(this, MainService.class));
		} else if (isMainServiceRunning == true) {
			Toast.makeText(getApplicationContext(),
					(String) TAG + " onCreate - Main Service Running...",
					Toast.LENGTH_LONG).show();
		}
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