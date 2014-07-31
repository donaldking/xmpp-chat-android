package co.uk.tusksolutions.tchat.android.listeners;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.ChatState;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.TChatApplication.CHAT_STATUS_ENUM;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.models.ChatMessagesModel;
import co.uk.tusksolutions.tchat.android.xmpp.notifications.XMPPNotificationManager;

public class XMPPChatMessageListener implements PacketListener {

	private static final String TAG = "XMPPChatMessageListener";
	Context mContext = TChatApplication.getContext();
	public static final String EXTRA_CHAT_STATE = "chatState";
	public static final String ACTION_XMPP_CHAT_STATE_CHANGED = "XMPP_CHAT_STATE_CHANGED";
	public static final String EXTRA_CHAT_BUDDY_NAME = "roomJid";

	@Override
	public void processPacket(Packet packet) {

		Message message = (Message) packet;

		if (message.getType() == Message.Type.chat) {
			if (message.getBody() == null) {

				for (PacketExtension extension : message.getExtensions()) {
					Log.d(TAG, "XML: " + extension.getElementName());

					if (extension.getElementName().equals(
							ChatState.composing.toString())) {
						Log.i(TAG, "Composing...: ");
						this.sendComposeBroadcast(StringUtils
								.parseBareAddress(packet.getFrom()));

					} else if (extension.getElementName().equals(
							ChatState.paused.toString())) {
						Log.i(TAG, "Paused..");
						this.sendPausedBroadcast(StringUtils
								.parseBareAddress(packet.getFrom()));
					}
				}

			} else if (message.getBody().length() == 0) {
				Log.i(TAG, "Stopped composing...: ");
				this.sendPausedBroadcast(StringUtils.parseBareAddress(packet
						.getFrom()));

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

	private void sendComposeBroadcast(String from) {
		Intent i = new Intent();
		i.setAction(ACTION_XMPP_CHAT_STATE_CHANGED);
		i.putExtra(EXTRA_CHAT_STATE, "Composing");
		i.putExtra(EXTRA_CHAT_BUDDY_NAME, from);
		mContext.sendBroadcast(i);
	}

	private void sendPausedBroadcast(String from) {
		Intent i = new Intent();
		i.setAction(ACTION_XMPP_CHAT_STATE_CHANGED);
		i.putExtra(EXTRA_CHAT_STATE, "Paused");
		i.putExtra(EXTRA_CHAT_BUDDY_NAME, from);
		mContext.sendBroadcast(i);
	}

	private void sendNotification(Packet packet, Message message) {

		if (message.getBody().contains("|s|")) {
			
			String MessageParse[] = message.getBody().split("\\|s\\|");
			String buddyJID = MessageParse[0].replace("@"+Constants.CURRENT_SERVER,"");
			String last_message = MessageParse[1];
			Bundle b = new Bundle();

			b.putString("roomJid", packet.getFrom());
			b.putString("fromName",buddyJID);
			b.putString("message", last_message);

			Intent intent = new Intent();
			intent.putExtra("chatFromFriendBundle", b);

			// Send TO_USER notification manager
			new XMPPNotificationManager().sendNormalChatNotification(intent);

			// Save to DB
			saveMessageToDb(packet, message);

		} else {

			Bundle b = new Bundle();
			String buddyJid = StringUtils.parseBareAddress(message.getFrom());
			b.putString("roomJid", packet.getFrom());
			b.putString("fromName", TChatApplication.getRosterModel()
					.getBuddyName(buddyJid));
			b.putString("message", message.getBody());

			Intent intent = new Intent();
			intent.putExtra("chatFromFriendBundle", b);

			// Send TO_USER notification manager
			new XMPPNotificationManager().sendNormalChatNotification(intent);

			// Save to DB
			saveMessageToDb(packet, message);
		}
	}

	private void saveMessageToDb(Packet packet, Message message) {
		/*
		 * Insert received message to db
		 */
		String resource = StringUtils.parseResource(packet.getFrom());
		ChatMessagesModel mChatMessageModel = new ChatMessagesModel();
		String mid = packet.getPacketID();
		/*
		 * If Message from Other Resource and get the carbon message
		 */
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
	}
}
