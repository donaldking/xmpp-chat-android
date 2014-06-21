package co.uk.tusksolutions.tchat.android.listeners;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import android.content.Context;
import co.uk.tusksolutions.tchat.android.TChatApplication;

public class XMPPPresenceListener implements PacketListener {

	private static final String TAG = "XMPPPresenceListener";
	Context mContext = TChatApplication.getContext();

	public XMPPPresenceListener() {
		Presence presence;
		/**
		 * Tell the server we are online
		 */
		presence = new Presence(Presence.Type.available);
		TChatApplication.connection.sendPacket(presence);
	}

	@Override
	public void processPacket(Packet packet) {
		Presence presence = (Presence) packet;
		/**
		 * Update presence for friend
		 */
		if (presence.getType() != null) {

			String[] strTemp = presence.getFrom().split("/");
			TChatApplication.getRosterModel().updatePresenceForFriend(
					strTemp[0], presence, strTemp[1]);
		}
	}

	public static void loadRoster() {
		/**
		 * Request and process our roster
		 */
		Roster roster = TChatApplication.connection.getRoster();
		TChatApplication.getRosterModel().saveRosterToDB(roster);

	}
}
