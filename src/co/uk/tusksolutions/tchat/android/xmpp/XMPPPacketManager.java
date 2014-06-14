package co.uk.tusksolutions.tchat.android.xmpp;

import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;

import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.listeners.XMPPChatMessageListener;
import co.uk.tusksolutions.tchat.android.listeners.XMPPConnectionListener;
import co.uk.tusksolutions.tchat.android.listeners.XMPPGroupChatMessageListener;

public class XMPPPacketManager {

	public XMPPPacketManager() {
		/**
		 * Set up our packet listeners
		 */
		setUpPacketListeners();
	}

	private void setUpPacketListeners() {
		
		try {
			PacketFilter chatFilter = new MessageTypeFilter(Message.Type.chat);
			PacketFilter groupChatFilter = new MessageTypeFilter(
					Message.Type.groupchat);

			TChatApplication.connection.addPacketListener(new XMPPChatMessageListener(), chatFilter);
			TChatApplication.connection.addPacketListener(new XMPPGroupChatMessageListener(),
					groupChatFilter);
			TChatApplication.connection.addConnectionListener(new XMPPConnectionListener());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
