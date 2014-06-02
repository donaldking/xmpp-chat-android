/**
 * 
 */
package co.uk.tusksolutions.tchat.android;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.xmpp.XMPPConnectionManager;

public class TChatApplication extends Application {

	final static String TAG = "TChatApplication";
	private boolean loginStatus;
	public boolean isDebug = true;
	private static Context mContext;

	@Override
	public void onCreate() {
		super.onCreate();
		TChatApplication.mContext = this;
		Toast.makeText(getApplicationContext(), (String) TAG + " onCreate",
				Toast.LENGTH_LONG).show();
		startService(new Intent(this, XMPPConnectionManager.class));
		sendBroadcast(new Intent(Constants.START_LISTENING_FOR_PACKETS));

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
}