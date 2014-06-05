package co.uk.tusksolutions.tchat.android.xmpp;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.Toast;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.services.MainService;

public class XMPPConnectionManager extends IntentService {

	static final String TAG = "XMPPConnectionManager";

	public XMPPConnectionManager() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		Log.i(TAG, "onHandleIntent");

		/**
		 * if Main Service was stopped.
		 */
		if (TChatApplication.isMainServiceRunning == false) {
			Log.i(TAG, "Main Service not running...");
			startService(new Intent(TChatApplication.getContext(),
					MainService.class));
		}
		/**
		 * if no connection object
		 */
		if (TChatApplication.connection == null) {

			Log.i(TAG, "Make new connection..");
			connectAndLogin("donaldking", "default", Constants.STAGING_SERVER);
			return;
		}
		/**
		 * if Main Service is running but connection object is null. This could
		 * be may be network connection was lost.
		 */
		if (TChatApplication.isMainServiceRunning == true
				&& TChatApplication.connection == null) {

			Log.i(TAG, "Make new connection..");
			connectAndLogin("donaldking", "default", Constants.STAGING_SERVER);
			return;
		}
		Log.i(TAG, "Connection valid... Do nothing.");
	}

	private void connectAndLogin(String username, String password, String server) {
		try {
			TChatApplication.connection = new XMPPConnection(server);
			TChatApplication.connection.connect();
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		try {
			TChatApplication.connection.login(
					username,
					password,
					"TChat-Android-"
							+ Secure.getString(TChatApplication.getContext()
									.getContentResolver(), Secure.ANDROID_ID));

			/**
			 * We initialise the presence manager class which takes care of our
			 * presence, roster and roster entries.
			 */
			new XMPPPresenceManager();

			/**
			 * We initialise packet manager class with this connection object
			 * which sets up our listeners.
			 */
			new XMPPPacketManager();

			/*
			 * Show Toast who is logged in
			 */
			new Handler(Looper.getMainLooper()).post(new Runnable() {
				@Override
				public void run() { // Show who we are logged in as
					Toast.makeText(
							TChatApplication.getContext(),
							(String) TAG + " Logged in as: "
									+ TChatApplication.connection.getUser(),
							Toast.LENGTH_LONG).show();
				}
			});

		} catch (Exception e) {
			/**
			 * Error connecting. This could be Internet off or some issue. We
			 * need to remove connection object.
			 */
			TChatApplication.connection = null;
			e.printStackTrace();
		}
	}
}