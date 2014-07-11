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
	Presence presenceObject;

	public XMPPPresenceListener() {

		/**
		 * Tell the server we are online
		 */
		this.setXMPPPresence(Presence.Type.available);
	}

	public void setXMPPPresence(Presence.Type presence) {

		if (presence.equals(Presence.Type.available)) {
			presenceObject = new Presence(Presence.Type.available);
			/**
			 * Update our presence in db
			 */
			TChatApplication.getUserModel().updateCurrentPresence("online");

		} else if (presence.equals(Presence.Type.unavailable)) {
			presenceObject = new Presence(Presence.Type.unavailable);
		}
		TChatApplication.connection.sendPacket(presenceObject);
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
		try {
			Roster roster = TChatApplication.connection.getRoster();
			TChatApplication.getRosterModel().saveRosterToDB(roster);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
