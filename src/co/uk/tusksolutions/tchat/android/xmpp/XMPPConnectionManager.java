package co.uk.tusksolutions.tchat.android.xmpp;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import android.app.IntentService;
import android.content.Intent;
import android.provider.Settings.Secure;
import android.util.Log;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.constants.Constants;

public class XMPPConnectionManager extends IntentService {

	static final String TAG = "XMPPConnectionManager";
	public static Connection connection;

	public XMPPConnectionManager() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (connection == null) {
			connectAndLogin("donaldking", "default", Constants.STAGING_SERVER);
		}
	}

	private void connectAndLogin(String username, String password, String server) {
		try {
			connection = new XMPPConnection(server);
			connection.connect();
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		try {
			connection.login(
					username,
					password,
					"TChat-Android-"
							+ Secure.getString(TChatApplication.getContext()
									.getContentResolver(), Secure.ANDROID_ID));
			Log.i(TAG, "Logged in as " + connection.getUser());
		} catch (Exception e) {
			e.printStackTrace();
		}

		/**
		 * We initialise the presence manager class which takes care of our
		 * presence, roster and roster entries.
		 */
		new XMPPPresenceManager(connection);

		/**
		 * We initialise packet manager class with this connection object which
		 * sets up our listeners.
		 */
		new XMPPPacketManager(connection);

	}
}