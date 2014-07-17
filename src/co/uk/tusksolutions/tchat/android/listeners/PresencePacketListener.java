package co.uk.tusksolutions.tchat.android.listeners;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;


import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class PresencePacketListener implements PacketListener {
	private static final String TAG_CONTACT_SYNC = "CONTACT_SYNC";
	private final XMPPConnection connection;
	
	private final Context context;

	public PresencePacketListener(final Context context, XMPPConnection connection) {
		this.connection = connection;
		
		this.context = context;
	}

	@Override
	public void processPacket(Packet packet) {

		Presence presence = (Presence) packet;
		String fromJID = StringUtils.parseBareAddress(presence.getFrom());
		String resource = StringUtils.parseResource(presence.getFrom());

		if (presence.getType().equals(Presence.Type.subscribe)) {
			
		} else if (presence.getType().equals(Presence.Type.available)) {
			
		} else if (presence.getType().equals(Presence.Type.unavailable)) {
			
		}
	}
}
