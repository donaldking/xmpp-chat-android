package co.uk.tusksolutions.tchat.android.xmpp;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.packet.Presence;

import co.uk.tusksolutions.tchat.android.TChatApplication;

public class XMPPPresenceManager {

	static final String TAG = "XMPPPresenceManager";

	Presence presence;
	Roster roster;

	public XMPPPresenceManager() {
		try {
			/**
			 * Tell the server we are online
			 */
			presence = new Presence(Presence.Type.available);
			TChatApplication.connection.sendPacket(presence);

			/**
			 * Request and process our roster
			 */
			roster = TChatApplication.connection.getRoster();
			
			TChatApplication.getRosterModel().saveRosterToDB(roster);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
