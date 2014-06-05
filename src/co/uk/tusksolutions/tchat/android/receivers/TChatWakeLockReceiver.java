package co.uk.tusksolutions.tchat.android.receivers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import co.uk.tusksolutions.tchat.android.xmpp.notifications.XMPPNotificationManager;

public class TChatWakeLockReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		/*
		 * Wakes the device up if sleeping
		 */
		Intent notificationService = new Intent(context,
				XMPPNotificationManager.class);
		if (intent.getExtras() != null) {
			notificationService.putExtra("chatMessageBundle",
					intent.getBundleExtra("chatMessageBundle"));
		}
		startWakefulService(context, notificationService);
	}
}
