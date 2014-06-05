package co.uk.tusksolutions.tchat.android.xmpp.notifications;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import co.tusksolutions.tchat.android.activities.MainActivity;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.receivers.TChatWakeLockReceiver;

public class XMPPNotificationManager extends IntentService {

	public XMPPNotificationManager() {
		super("TCHAT");
	}

	private static final int MID = 2222;
	NotificationCompat.Builder mBuilder;
	Context mContext = TChatApplication.getContext();
	private static final String TAG = "XMPPNotificationManager";

	@Override
	protected void onHandleIntent(Intent intent) {

		/**
		 * Post Jelly Bean use inbox style notification Pre Jelly Bean, use
		 * normal ticker notification
		 */

		String fromName = intent.getBundleExtra("chatMessageBundle").getString(
				"fromName");
		String message = intent.getBundleExtra("chatMessageBundle").getString(
				"message");
		mBuilder = new NotificationCompat.Builder(mContext)
				.setSmallIcon(R.drawable.ic_action_chat)
				.setContentTitle(fromName).setTicker(fromName + ": " + message)
				.setContentText("New message received");
		Uri defaultSound = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		mBuilder.setSound(defaultSound);
		mBuilder.setAutoCancel(true);

		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
		inboxStyle.setBigContentTitle(fromName + " Messages:");
		inboxStyle.addLine(message);
		inboxStyle.setSummaryText("New message received");
		mBuilder.setStyle(inboxStyle);

		/**
		 * Package this sender in a bundle which we will add to the pending
		 * intent to post so that we can navigate to the exact window when we
		 * click on the notification.
		 */
		Intent mainActivityIntent = new Intent(mContext, MainActivity.class);
		mainActivityIntent.putExtra("chatMessageBundle",
				intent.getBundleExtra("chatMessageBundle"));
		mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		PendingIntent pi = PendingIntent.getActivity(mContext, 0,
				mainActivityIntent, PendingIntent.FLAG_ONE_SHOT);
		mBuilder.setContentIntent(pi);

		NotificationManager mNotificationManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(MID, mBuilder.build());

		Log.i(TAG, "sent message" + " [" + message + "] " + "as notification!");
		
		TChatWakeLockReceiver.completeWakefulIntent(intent);
	}

}
