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
import co.uk.tusksolutions.tchat.android.activities.ChatRoomActivity;
import co.uk.tusksolutions.tchat.android.models.UserModel;

public class XMPPNotificationManager {

	private static final int MID = 2222;
	NotificationCompat.Builder mBuilder;
	Context mContext = TChatApplication.getContext();
	private static final String TAG = "XMPPNotificationManager";
UserModel mUserModel;
	public void sendNormalChatNotification(Intent intent) {
		mUserModel=new UserModel();
		String presence = mUserModel.getCurrentPresence();

		  if (presence.equalsIgnoreCase("offline")) {
				return;
			} 
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
		if(TChatApplication.isChatNotificationSound())
		{
		mBuilder.setSound(defaultSound);
		}
		else
		{
			mBuilder.setSound(null);
		}
		
		mBuilder.setAutoCancel(true);

		/**
		 * Package this TO_USER in a bundle which we will add TO_USER the
		 * pending intent TO_USER post so that we can navigate TO_USER the exact
		 * window when we click on the notification.
		 */
		Intent chatActivityIntent = new Intent(mContext, ChatActivity.class);
		chatActivityIntent.putExtra("chatFromFriendBundle",
				intent.getBundleExtra("chatFromFriendBundle"));
		chatActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

		PendingIntent pi = PendingIntent.getActivity(mContext, 1,
				chatActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(pi);

		NotificationManager mNotificationManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(MID, mBuilder.build());

		Log.i(TAG, "sent message" + " [" + message + "] " + "as notification!");
	}

	public void sendGroupChatNotification(Intent intent) {
		// TODO Auto-generated method stub

		/**
		 * Post Jelly Bean use inbox style notification Pre Jelly Bean, use
		 * normal ticker notification
		 */

		String roomName = intent.getBundleExtra("groupChatFromRoomBundle")
				.getString("roomName");
		String message = intent.getBundleExtra("groupChatFromRoomBundle")
				.getString("message");
		String messageType=intent.getBundleExtra("groupChatFromRoomBundle").getString("messageType");
		mBuilder = new NotificationCompat.Builder(mContext)
				.setSmallIcon(R.drawable.ic_action_chat)
				.setContentTitle(roomName).setTicker(roomName + ": " + message)
				.setContentText(message);
		Uri defaultSound = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		if(TChatApplication.isChatNotificationSound())
		{
		mBuilder.setSound(defaultSound);
		}
		else
		{
			mBuilder.setSound(null);
		}
		mBuilder.setAutoCancel(true);

		/**
		 * Package this TO_USER in a bundle which we will add TO_USER the
		 * pending intent TO_USER post so that we can navigate TO_USER the exact
		 * window when we click on the notification.
		 */
		Intent groupChatActivityIntent;
	
		
			 groupChatActivityIntent = new Intent(mContext,
					ChatRoomActivity.class);
				
		
		
		groupChatActivityIntent.putExtra("groupChatFromRoomBundle",
				intent.getBundleExtra("groupChatFromRoomBundle"));
		groupChatActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		
		PendingIntent pi = PendingIntent.getActivity(mContext, 1,
				groupChatActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		mBuilder.setContentIntent(pi);
		NotificationManager mNotificationManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(MID, mBuilder.build());

		Log.i(TAG, "sent message" + " [" + message + "] " + "as notification!");
	}
}
