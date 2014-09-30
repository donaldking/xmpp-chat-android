package co.uk.tusksolutions.tchat.android.listeners;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import android.content.Context;
import android.util.Log;
import co.uk.tusksolutions.tchat.android.TChatApplication;

public class XMPPPresenceListener implements PacketListener {

	Context mContext = TChatApplication.getContext();
	static Presence presenceObject;

	public XMPPPresenceListener() {

		/**
		 * Tell the server we are online
		 */
		XMPPPresenceListener.setXMPPPresence(Presence.Type.available);
		
		Log.e("XMPPPresenceListener ","PRESENCE UPDATES ");
	}

	public static void setXMPPPresence(Presence.Type presence) {

		if (presence.equals(Presence.Type.available)) {
			presenceObject = new Presence(Presence.Type.available);
			/**
			 * Update our presence in db
			 */
			TChatApplication.getUserModel().updateCurrentPresence("online");
			TChatApplication.connection.sendPacket(presenceObject);

		} else if (presence.equals(Presence.Type.unavailable)) {
			presenceObject = new Presence(Presence.Type.unavailable);
			TChatApplication.connection.disconnect();
		}

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
			
			
			Log.e("XMPPPresenceListener ","PRESENCE FRIEND "+strTemp[0]+" presence "+presence);
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
