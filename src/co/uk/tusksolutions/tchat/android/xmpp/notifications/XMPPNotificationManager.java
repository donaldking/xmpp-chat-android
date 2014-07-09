package co.uk.tusksolutions.tchat.android.xmpp.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.activities.ChatActivity;

public class XMPPNotificationManager {

	private static final int MID = 2222;
	NotificationCompat.Builder mBuilder;
	Context mContext = TChatApplication.getContext();
	private static final String TAG = "XMPPNotificationManager";

	public void sendNormalChatNotification(Intent intent) {

		/**
		 * Post Jelly Bean use inbox style notification Pre Jelly Bean, use
		 * normal ticker notification
		 */

		String fromName = intent.getBundleExtra("chatFromFriendBundle")
				.getString("fromName");
		String message = intent.getBundleExtra("chatFromFriendBundle")
				.getString("message");
		mBuilder = new NotificationCompat.Builder(mContext)
				.setSmallIcon(R.drawable.ic_action_chat)
				.setContentTitle(fromName).setTicker(fromName + ": " + message)
				.setContentText(message);
		Uri defaultSound = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		mBuilder.setSound(defaultSound);
		mBuilder.setAutoCancel(true);

		/**
		 * Package this TO_USER in a bundle which we will add TO_USER the
		 * pending intent TO_USER post so that we can navigate TO_USER the exact
		 * window when we click on the notification.
		 */
		Intent chatActivityIntent = new Intent(mContext, ChatActivity.class);
		chatActivityIntent.putExtra("chatFromFriendBundle",
				intent.getBundleExtra("chatFromFriendBundle"));
		chatActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		PendingIntent pi = PendingIntent.getActivity(mContext, 0,
				chatActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT
						| PendingIntent.FLAG_ONE_SHOT);
		mBuilder.setContentIntent(pi);

		NotificationManager mNotificationManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(MID, mBuilder.build());

		Log.i(TAG, "sent message" + " [" + message + "] " + "as notification!");
	}
}
