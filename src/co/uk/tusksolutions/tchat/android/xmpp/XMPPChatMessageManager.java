package co.uk.tusksolutions.tchat.android.xmpp;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.ChatState;
import org.jivesoftware.smackx.packet.ChatStateExtension;

import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.models.ChatMessagesModel;

public class XMPPChatMessageManager {

	private static ChatMessagesModel mChatMessageModel;
	private static ChatStateExtension cm;

	public static void sendMessage(final String to, String buddyName,
			final String message, int isGroupMessage, String messageType) {
		if (mChatMessageModel == null) {
			mChatMessageModel = new ChatMessagesModel();
		}
		Message msg;
		if (isGroupMessage == 1) {
			msg = new Message(to, Message.Type.groupchat);
		} else {
			msg = new Message(to, Message.Type.chat);
		}

		msg.setBody(message);
		if (TChatApplication.connection != null) {
			try {
				mChatMessageModel.saveMessageToDB(to,
						TChatApplication.getCurrentJid(), buddyName, message,
						isGroupMessage, messageType,
						System.currentTimeMillis(), 1);
				TChatApplication.connection.sendPacket(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// online offline message
			mChatMessageModel.saveMessageToDB(to,
					TChatApplication.getCurrentJid(), buddyName, message,
					isGroupMessage, messageType, System.currentTimeMillis(), 2);
		}
	}

	public static void sendComposing(String to, String packetId) {

		cm = new ChatStateExtension(ChatState.composing);
		Message msg = new Message(to, Message.Type.chat);
		msg.addExtension(cm);

		if (TChatApplication.connection != null) {
			TChatApplication.connection.sendPacket(msg);
		}
	}

	public static void sendPaused(String to, String packetId) {
		cm = new ChatStateExtension(ChatState.paused);
		Message msg = new Message(to, Message.Type.chat);
		msg.addExtension(cm);

		if (TChatApplication.connection != null) {
			TChatApplication.connection.sendPacket(msg);
		}
	}

}