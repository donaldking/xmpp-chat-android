package co.uk.tusksolutions.tchat.android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.xmpp.XMPPConnectionManager;

public class GenericBroadcastReceiver extends BroadcastReceiver {

	static final String TAG = "GenericBroadcastReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equalsIgnoreCase(
				Constants.CONNECTION_CLOSED_IN_ERROR)) {
			TChatApplication.connection = null;

			/**
			 * Sets previously online friends as offline so we know when they
			 * come back online.
			 */
			TChatApplication.getRosterModel().setAllOffline();

		} else if (intent.getAction().equalsIgnoreCase(
				Constants.CONNECTION_CLOSED_BY_USER)) {
			TChatApplication.connection = null;

			/**
			 * Sets previously online friends as offline so we know when they
			 * come back online.
			 */
			TChatApplication.getRosterModel().setAllOffline();

		} else if (intent.getAction().equalsIgnoreCase(Constants.RECONNECTING)) {

		} else if (intent.getAction().equalsIgnoreCase(
				Constants.RECONNECTING_FAILED)) {

			/**
			 * Sets previously online friends as offline so we know when they
			 * come back online.
			 */
			TChatApplication.getRosterModel().setAllOffline();

		} else if (intent.getAction().equalsIgnoreCase(
				Constants.RECONNECTION_SUCCESSFULL)) {

		}
		if (intent.getAction().equalsIgnoreCase(
				"android.net.conn.CONNECTIVITY_CHANGE")) {

			if (TChatApplication.isNetworkAvailable()) {
				if (TChatApplication.connection == null
						&& TChatApplication.getUserModel().getUsername() != null) {
					try {
						XMPPConnectionManager.connect(TChatApplication
								.getUserModel().getUsername(), TChatApplication
								.getUserModel().getPassword());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}

	}

}
