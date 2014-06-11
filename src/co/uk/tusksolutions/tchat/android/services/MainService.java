/**
 * 
 */
package co.uk.tusksolutions.tchat.android.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.xmpp.XMPPConnectionManager;

public class MainService extends Service {
	static final String TAG = "TChatMainService";
	Context context = TChatApplication.getContext();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		/**
		 * Sets up Alarm Manager to run every 30 seconds to monitor our
		 * connection to the XMPP server. If it gets disconnected, it will be
		 * restarted automatically.
		 */
		Intent i = new Intent(context, XMPPConnectionManager.class);

		i.putExtra("username", TChatApplication.getUserModel().getUsername());
		i.putExtra("password", TChatApplication.getUserModel().getPassword());

		TChatApplication.connectionMonitoringOperation = PendingIntent
				.getService(context, -1, i, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis(), 30 * 1000,
				TChatApplication.connectionMonitoringOperation);

		TChatApplication.isMainServiceRunning = true;
		TChatApplication.acquireWakeLock();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Toast.makeText(getApplicationContext(),
				(String) TAG + " Release connection", Toast.LENGTH_LONG).show();

		if (TChatApplication.connection !=null) {
			TChatApplication.connection.disconnect();
		}
		TChatApplication.connection = null;
		TChatApplication.isMainServiceRunning = false;
		TChatApplication.releaseWakeLock();
	}

}
