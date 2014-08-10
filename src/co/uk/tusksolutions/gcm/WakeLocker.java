package co.uk.tusksolutions.gcm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;

@SuppressLint("Wakelock")
public class WakeLocker {

	private static PowerManager.WakeLock wakeLock;

	@SuppressLint("Wakelock")
	public static void acquire(Context context) {
		if (wakeLock != null)
			wakeLock.release();

		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.ON_AFTER_RELEASE, "WakeLock");
		wakeLock.acquire();
	}

	public static void release() {
		if (wakeLock != null)
			wakeLock.release();
		wakeLock = null;
	}
}
