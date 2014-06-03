/**
 * 
 */
package co.uk.tusksolutions.tchat.android.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import co.uk.tusksolutions.tchat.android.xmpp.XMPPConnectionManager;

public class MainService extends Service {
	static final String TAG = "TChatMainService";

	@Override
	public void onCreate() {
		super.onCreate();
		/**
		 * Connects the user to XMPP server immediately after the service is
		 * started.
		 */
		Intent i = new Intent(this, XMPPConnectionManager.class);
		this.startService(i);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
