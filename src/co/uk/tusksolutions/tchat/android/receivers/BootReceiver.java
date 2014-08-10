package co.uk.tusksolutions.tchat.android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.xmpp.XMPPConnectionManager;

public class BootReceiver extends BroadcastReceiver {

	static final String TAG = "BootReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			/**
			 * Fires the main service service when boot completed intent is
			 * received.
			 */
			if (TChatApplication.getUserModel().getUsername() != null
					&& TChatApplication.getUserModel().getPassword() != null) {
				if(TChatApplication.isNetworkAvailable())
				{

				XMPPConnectionManager.connect(TChatApplication.getUserModel()
						.getUsername(), TChatApplication.getUserModel()
						.getPassword());

				}
			
				}
		}

	}
}
