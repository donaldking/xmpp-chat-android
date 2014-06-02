package co.uk.tusksolutions.tchat.android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import co.uk.tusksolutions.tchat.android.services.MainService;

public class BootReceiver extends BroadcastReceiver {

	static final String TAG = "BootReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

			Intent serviceLauncher = new Intent(context, MainService.class);
			context.startService(serviceLauncher);
			Toast.makeText(context, (String) TAG + " onReceive",
					Toast.LENGTH_LONG).show();

		}

	}
}
