package co.uk.tusksolutions.tchat.android;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import co.uk.tusksolutions.tchat.android.TChatApplication.CHAT_STATUS_ENUM;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.xmpp.notifications.XMPPNotificationManager;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";
	public static final String ACTION_XMPP_CHAT_STATE_CHANGED = "XMPP_CHAT_STATE_CHANGED";
	public static final String EXTRA_CHAT_STATE = "chatState";

	public GCMIntentService() {
		super(Constants.SENDER_ID);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {

		TChatApplication.registerForPush(registrationId);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {

		TChatApplication.unRegisterForPush();
	}

	@Override
	protected void onMessage(final Context context, Intent intent) {

		String jsonString = intent.getStringExtra("message");
		
		try {
			JSONObject object = new JSONObject(jsonString);
			if (jsonString.length() > 0) {

				Intent i = new Intent();
				i.setAction(ACTION_XMPP_CHAT_STATE_CHANGED);
				i.putExtra(EXTRA_CHAT_STATE, "sent");
				context.sendBroadcast(i);

				if (TChatApplication.getChatActivityStatus() == CHAT_STATUS_ENUM.VISIBLE
						&& TChatApplication.chatSessionBuddy
								.equalsIgnoreCase(object.getString("senderJid"))) {

					// 1. Visible and chatting with buddy

				} else if (TChatApplication.getChatActivityStatus() == CHAT_STATUS_ENUM.VISIBLE
						&& !TChatApplication.chatSessionBuddy
								.equalsIgnoreCase(object.getString("senderJid"))) {

					// 2. Visible and not chatting with buddy
					processNotification(object);

				} else if (TChatApplication.getChatActivityStatus() == CHAT_STATUS_ENUM.NOT_VISIBLE) {

					// 3. Not Visible and not chatting
					processNotification(object);

				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void processNotification(JSONObject object){
		try {
			
			Bundle b = new Bundle();

			if (object.getString("messageType").equalsIgnoreCase("CHAT")) {
				b.putString("roomJid", object.getString("sender") + "@"
						+ Constants.CURRENT_SERVER);

				b.putString("fromName", object.getString("sender_display_name"));

				/*
				 * Image & File notifications added
				 */
				String last_message = object.getString("message");
				if (last_message.contains("<img src")) {
					last_message = "Image";
				} else if (last_message.contains("<a target")) {
					last_message = "File";
				}

				b.putString("message", last_message);

				Intent i = new Intent();
				i.putExtra("chatFromFriendBundle", b);
				
				//this.saveNormalChatMessageToDb(b);

				// Send TO_USER notification manager
				new XMPPNotificationManager().sendNormalChatNotification(i);

			} else if (object.getString("messageType").equalsIgnoreCase(
					"GROUP_CHAT")) {

				Bundle groupChatBundle = new Bundle();
				groupChatBundle.putString("roomJid",
						object.getString("roomJid"));
				groupChatBundle.putString("resource",
						object.getString("sender"));
				groupChatBundle.putString("roomName",
						object.getString("sender_display_name"));
				groupChatBundle.putString("senderJid",
						object.getString("sender") + "@"
								+ Constants.CURRENT_SERVER);
				groupChatBundle.putString(
						"senderName",
						TChatApplication.getRosterModel().getBuddyName(
								object.getString("sender") + "@"
										+ Constants.CURRENT_SERVER));

				/*
				 * Image & File notifications added
				 */
				String last_message = object.getString("message");
				if (last_message.contains("<img src")) {
					last_message = "Image";
				} else if (last_message.contains("<a target")) {
					last_message = "File";
				}

				groupChatBundle.putString("message", last_message);

				Intent i = new Intent();
				i.putExtra("groupChatFromRoomBundle", groupChatBundle);

				Log.d(TAG,
						"message received: "
								+ groupChatBundle.getString("message"));

				// Send TO_USER notification manager
				new XMPPNotificationManager().sendGroupChatNotification(i);

			} else if (object.getString("messageType").equalsIgnoreCase(
					"CHAT_ROOM")) {
				b.putString("roomJid", object.getString("sender")
						+ "@conference." + Constants.CURRENT_SERVER);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method called on receiving a deleted message
	 * */
	@Override
	protected void onDeletedMessages(Context context, int total) {

	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
		return super.onRecoverableError(context, errorId);
	}

	@Override
	protected void onError(Context context, String errorId) {

	}
	
	/*private void saveNormalChatMessageToDb(Bundle bundle) {
		/*
		 * Insert received message to db
		 *
		String resource = StringUtils.parseResource(packet.getFrom());
		ChatMessagesModel mChatMessageModel = new ChatMessagesModel();
		String mid = packet.getPacketID();
		/*
		 * If Message from Other Resource and get the carbon message
		 *
		if (message.getBody().contains("|s|")) {

			String MessageParse[] = message.getBody().split("\\|s\\|");
			String buddyJID = MessageParse[0];
			String last_message = MessageParse[1];
			mChatMessageModel.saveMessageToDB(TChatApplication.getCurrentJid(),
					buddyJID, resource,
					StringUtils.parseName(packet.getFrom()), last_message, 0,
					"CHAT", System.currentTimeMillis(), 1, mid);

		} else {

			mChatMessageModel.saveMessageToDB(TChatApplication.getCurrentJid(),
					StringUtils.parseBareAddress(packet.getFrom()), resource,
					StringUtils.parseName(packet.getFrom()), message.getBody(),
					0, "CHAT", System.currentTimeMillis(), 1, mid);
		}
	}*/

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 *
	private static void generateNotification(Context context, String message) {
		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);

		String title = context.getString(R.string.app_name);

		Intent notificationIntent = new Intent(context, MainActivity.class);
		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, title, message, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		// Play default notification sound
		notification.defaults |= Notification.DEFAULT_SOUND;

		// Vibrate if vibrate is enabled
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notificationManager.notify(0, notification);

	}*/
}
