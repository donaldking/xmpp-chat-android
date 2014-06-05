package co.uk.tusksolutions.tchat.android.xmpp;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import android.app.IntentService;
import android.content.Intent;
import android.provider.Settings.Secure;
import android.util.Log;
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
		
		if (TChatApplication.isMainServiceRunning==false) {
			Log.i(TAG, "Main Service not running...");
			startService(new Intent(TChatApplication.getContext(),MainService.class));
			return;
		}
		
		if (TChatApplication.connection == null) {
			
			Log.i(TAG, "Make new connection..");
			connectAndLogin("donaldking", "default", Constants.STAGING_SERVER);
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
			Log.i(TAG, "Logged in as " + TChatApplication.connection.getUser());
		} catch (Exception e) {
			e.printStackTrace();
		}

		/**
		 * We initialise the presence manager class which takes care of our
		 * presence, roster and roster entries.
		 */
		new XMPPPresenceManager();

		/**
		 * We initialise packet manager class with this connection object which
		 * sets up our listeners.
		 */
		new XMPPPacketManager();
	}
}