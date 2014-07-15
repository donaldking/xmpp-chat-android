package co.uk.tusksolutions.tchat.android.listeners;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.TChatApplication.CHAT_STATUS_ENUM;
import co.uk.tusksolutions.tchat.android.models.ChatMessagesModel;
import co.uk.tusksolutions.tchat.android.xmpp.notifications.XMPPNotificationManager;

public class XMPPChatMessageListener implements PacketListener {

	private static final String TAG = "XMPPChatMessageListener";
	Context mContext = TChatApplication.getContext();
	public static final String EXTRA_CHAT_STATE = "chatState";
	public static final String ACTION_XMPP_CHAT_STATE_CHANGED = "XMPP_CHAT_STATE_CHANGED";
	public static final String EXTRA_CHAT_BUDDY_NAME="buddyJid";
	@Override
	public void processPacket(Packet packet) {

		Message message = (Message) packet;
		if (message.getType() == Message.Type.chat) {
			if (message.getBody() == null) {
				Log.i(TAG, "Composing...: ");
				Intent i = new Intent();
		        i.setAction(ACTION_XMPP_CHAT_STATE_CHANGED);
		        i.putExtra(EXTRA_CHAT_STATE, "Composing..");
		        i.putExtra(EXTRA_CHAT_BUDDY_NAME, StringUtils
						.parseBareAddress(packet.getFrom()));
		        mContext.sendBroadcast(i);
				
			} else if (message.getBody().length() == 0) {
				Log.i(TAG, "Stopped composing...: ");
				
			} else if (message.getBody().length() > 0) {
			
				Intent i = new Intent();
		        i.setAction(ACTION_XMPP_CHAT_STATE_CHANGED);
		        i.putExtra(EXTRA_CHAT_STATE, "sent");
		        mContext.sendBroadcast(i);
				Log.d(TAG,
						"Current buddy: "
								+ TChatApplication.chatSessionBuddy
								+ " Packet received: "
								+ StringUtils.parseBareAddress(packet.getFrom()));

				if (TChatApplication.getChatActivityStatus() == CHAT_STATUS_ENUM.VISIBLE
						&& TChatApplication.chatSessionBuddy
								.equalsIgnoreCase(StringUtils
										.parseBareAddress(packet.getFrom()))) {

					// 1. Visible and chatting with buddy

					// Save to DB
					saveMessageToDb(packet, message);

				} else if (TChatApplication.getChatActivityStatus() == CHAT_STATUS_ENUM.VISIBLE
						&& !TChatApplication.chatSessionBuddy
								.equalsIgnoreCase(StringUtils
										.parseBareAddress(packet.getFrom()))) {

					// 2. Visible and not chatting with buddy
					/*
					 * Prepare message bundle.
					 */
					sendNotification(packet, message);

				} else if (TChatApplication.getChatActivityStatus() == CHAT_STATUS_ENUM.NOT_VISIBLE) {

					// 3. Not Visible and not chatting
					/*
					 * Prepare message bundle.
					 */
					sendNotification(packet, message);

				}
			}
		}
	}

	private void sendNotification(Packet packet, Message message) {
		Bundle b = new Bundle();
		b.putString("buddyJid", packet.getFrom());
		b.putString("fromName", StringUtils.parseName(StringUtils
				.parseBareAddress(message.getFrom())));
		b.putString("message", message.getBody());

		Intent intent = new Intent();
		intent.putExtra("chatFromFriendBundle", b);

		// Send TO_USER notification manager
		new XMPPNotificationManager().sendNormalChatNotification(intent);

		// Save to DB
		saveMessageToDb(packet, message);

	}

	private void saveMessageToDb(Packet packet, Message message) {
		/*
		 * Insert received message to db
		 */
		ChatMessagesModel mChatMessageModel = new ChatMessagesModel();
		mChatMessageModel.saveMessageToDB(TChatApplication.getCurrentJid(),
				StringUtils.parseBareAddress(packet.getFrom()),
				StringUtils.parseName(packet.getFrom()), message.getBody(),
				System.currentTimeMillis(), 1);
	}
}
