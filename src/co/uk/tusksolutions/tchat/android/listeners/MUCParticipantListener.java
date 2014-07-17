package co.uk.tusksolutions.tchat.android.listeners;

import android.content.Context;
import android.util.Log;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

/**
 * @author Sebastian Gansca sebigansca@gmail.com
 *         <p/>
 *         Copyright 2012 Gemoro Mobile Media All rights reserved
 */
public class MUCParticipantListener implements PacketListener {

    private Context context;

    public MUCParticipantListener(final Context context) {
        this.context = context;
    }
    @Override
    public void processPacket(Packet packet) {
        Log.d("MUCParticipantListener","Received a participant in MUC");

        if (packet instanceof Presence) {
        	
            Presence presence = (Presence) packet;

            String fromBareResource = StringUtils.parseResource(presence.getFrom());
            String roomJID = StringUtils.parseBareAddress(presence.getFrom());

            if (presence.isAvailable()) {
	           // MUCDbUtility.addParticipant(context, roomJID, fromBareResource);
            } else {
            	//MUCDbUtility.removeParticipant(context, roomJID, fromBareResource);
            }
        }
    }
}
