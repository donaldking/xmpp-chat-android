package co.uk.tusksolutions.tchat.android.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.xmpp.XMPPConnectionManager;

public class ScheduleXMPPService extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equalsIgnoreCase(
				Constants.START_LISTENING_FOR_PACKETS)) {
			/**
			 * Sets up Alarm Manager to run every 30 seconds to monitor our
			 * connection to the XMPP server. If it gets disconnected, it will
			 * be restarted automatically.
			 */
			Intent i = new Intent(context, XMPPConnectionManager.class);
			PendingIntent operation = PendingIntent.getService(context, -1, i,
					PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager alarmManager = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
					System.currentTimeMillis(), 30 * 1000, operation);
		}
	}
}
