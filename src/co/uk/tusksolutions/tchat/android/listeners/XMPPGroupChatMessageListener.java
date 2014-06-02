package co.uk.tusksolutions.tchat.android.listeners;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

import android.util.Log;

public class XMPPGroupChatMessageListener implements PacketListener {

	private static final String TAG = "XMPPGroupChatMessageListener";

	@Override
	public void processPacket(Packet packet) {
		Message message = (Message) packet;

		if (message.getBody() != null) {
			String fromName = StringUtils.parseBareAddress(message.getFrom());
			String toName = StringUtils.parseBareAddress(message.getTo());
			Log.i(TAG, "New group message from " + fromName + ", To: " + toName
					+ " : " + message.getBody());
		}

	}

}
