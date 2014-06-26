package co.uk.tusksolutions.tchat.android.xmpp;

import org.jivesoftware.smack.packet.Message;

import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.models.ChatMessagesModel;

public class XMPPChatMessageManager {

	private static ChatMessagesModel mChatMessageModel;

	public static void sendMessage(final String to, final String message) {
		if (mChatMessageModel == null) {
			mChatMessageModel = new ChatMessagesModel();
		}
		Message msg = new Message(to, Message.Type.chat);
		msg.setBody(message);
		if (TChatApplication.connection != null) {
			try {
				
				mChatMessageModel.saveMessageToDB(to,
						TChatApplication.getCurrentJid(), message,
						System.currentTimeMillis(), 1);
				TChatApplication.connection.sendPacket(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// online offline message
			mChatMessageModel.saveMessageToDB(to,
					TChatApplication.getCurrentJid(), message,
					System.currentTimeMillis(), 2);
		}
	}

}