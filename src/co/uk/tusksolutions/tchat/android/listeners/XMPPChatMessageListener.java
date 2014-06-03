package co.uk.tusksolutions.tchat.android.listeners;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import co.tusksolutions.tchat.android.activities.MainActivity;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;

public class XMPPChatMessageListener implements PacketListener {

	private static final String TAG = "XMPPChatMessageListener";
	private static final int MID = 2222;
	NotificationCompat.Builder mBuilder;
	Context mContext = TChatApplication.getContext();

	@Override
	public void processPacket(Packet packet) {
		Message message = (Message) packet;

		if (message.getBody() != null) {
			postStatusBarNotification(message);
		}
	}

	public void postStatusBarNotification(Message message) {
		
		/**
		 * Post Jelly Bean use inbox style notification
		 * Pre Jelly Bean, use normal ticker notification
		 */
		String FromName = StringUtils.parseName(StringUtils
				.parseBareAddress(message.getFrom()));
		mBuilder = new NotificationCompat.Builder(mContext)
				.setSmallIcon(R.drawable.ic_action_chat)
				.setContentTitle(FromName)
				.setTicker(FromName + ": "+ message.getBody())
				.setContentText("New message received");
		Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		mBuilder.setSound(defaultSound);
		mBuilder.setAutoCancel(true);
		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
		inboxStyle.setBigContentTitle(FromName +" Messages:");
		inboxStyle.addLine(message.getBody());
		inboxStyle.setSummaryText("New message received");
		mBuilder.setStyle(inboxStyle);

		Intent i = new Intent(mContext, MainActivity.class);

		TaskStackBuilder stackBuilder = TaskStackBuilder
				.create(TChatApplication.getContext());
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(i);

		PendingIntent pi = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(pi);

		NotificationManager mNotificationManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(MID, mBuilder.build());

		Log.i(TAG,
				"New message from "
						+ StringUtils.parseBareAddress(message.getFrom())
						+ ", To: "
						+ StringUtils.parseBareAddress(message.getTo()
								.split("@", 0).toString()) + " : "
						+ message.getBody());
	}
}
