package co.uk.tusksolutions.tchat.android.receivers;

import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class GenericBroadcastReceiver extends BroadcastReceiver {

	static final String TAG = "GenericBroadcastReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equalsIgnoreCase(
				Constants.CONNECTION_CLOSED_IN_ERROR)) {
			TChatApplication.connection = null;
			Toast.makeText(TChatApplication.getContext(),
					(String) TAG + " CONNECTION_CLOSED_IN_ERROR",
					Toast.LENGTH_LONG).show();

		} else if (intent.getAction().equalsIgnoreCase(
				Constants.CONNECTION_CLOSED_BY_USER)) {
			TChatApplication.connection = null;
			Toast.makeText(TChatApplication.getContext(),
					(String) TAG + " CONNECTION_CLOSED_BY_USER",
					Toast.LENGTH_LONG).show();

		} else if (intent.getAction().equalsIgnoreCase(Constants.RECONNECTING)) {
			Toast.makeText(TChatApplication.getContext(),
					(String) TAG + " RECONNECTING", Toast.LENGTH_LONG).show();
		} else if (intent.getAction().equalsIgnoreCase(
				Constants.RECONNECTING_FAILED)) {
			Toast.makeText(TChatApplication.getContext(),
					(String) TAG + " RECONNECTING_FAILED", Toast.LENGTH_LONG)
					.show();
		} else if (intent.getAction().equalsIgnoreCase(
				Constants.RECONNECTION_SUCCESSFULL)) {
			Toast.makeText(TChatApplication.getContext(),
					(String) TAG + " RECONNECTION_SUCCESSFULL",
					Toast.LENGTH_LONG).show();
		}

	}

}
