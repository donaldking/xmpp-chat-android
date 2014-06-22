package co.uk.tusksolutions.tchat.android.xmpp;

import org.jivesoftware.smack.packet.Message;

import co.uk.tusksolutions.tchat.android.TChatApplication;

public class XMPPChatMessageManager {

	public static void sendMessage(String to, String message) {
		Message msg = new Message(to, Message.Type.chat);
		msg.setBody(message);
		if (TChatApplication.connection != null) {
			try {
				// TODO Save to db and send relay message to self
				// then send packet!
				TChatApplication.connection.sendPacket(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// TODO Save message locally so it can be tried again when we caome
			// online offline message
		}
	}

}