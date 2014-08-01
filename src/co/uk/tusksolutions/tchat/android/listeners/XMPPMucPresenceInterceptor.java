package co.uk.tusksolutions.tchat.android.listeners;

import org.jivesoftware.smack.PacketInterceptor;
import org.jivesoftware.smack.packet.Packet;

import android.util.Log;

public class XMPPMucPresenceInterceptor implements PacketInterceptor {

	@Override
	public void interceptPacket(Packet packet) {
		// TODO Auto-generated method stub
		
		Log.d("XMPPMucPresenceInterceptor", packet.getXmlns());
	}

}
