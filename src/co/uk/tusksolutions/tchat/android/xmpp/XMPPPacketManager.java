package co.uk.tusksolutions.tchat.android.xmpp;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;

import co.uk.tusksolutions.tchat.android.listeners.XMPPChatMessageListener;
import co.uk.tusksolutions.tchat.android.listeners.XMPPConnectionListener;
import co.uk.tusksolutions.tchat.android.listeners.XMPPGroupChatMessageListener;

public class XMPPPacketManager {

	Connection connection;

	public XMPPPacketManager(Connection connection) {
		this.connection = connection;
		/**
		 * Set up our packet listeners
		 */
		setUpPacketListeners();
	}

	private void setUpPacketListeners() {
		PacketFilter chatFilter = new MessageTypeFilter(Message.Type.chat);
		PacketFilter groupChatFilter = new MessageTypeFilter(
				Message.Type.groupchat);

		connection.addPacketListener(new XMPPChatMessageListener(), chatFilter);
		connection.addPacketListener(new XMPPGroupChatMessageListener(),
				groupChatFilter);
		connection.addConnectionListener(new XMPPConnectionListener());
	}
}
