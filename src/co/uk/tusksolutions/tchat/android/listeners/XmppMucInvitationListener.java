package co.uk.tusksolutions.tchat.android.listeners;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.InvitationListener;

import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.xmpp.XmppMuc;
import android.content.Context;
import android.util.Log;



/**
 * @author Sebastian Gansca sebigansca@gmail.com
 *         <p/>
 *         Copyright 2012 Gemoro Mobile Media All rights reserved
 */
public class XmppMucInvitationListener implements InvitationListener {

	private final Context context;

	public XmppMucInvitationListener(Context context) {
		this.context = context;
	}

	@Override
	public void invitationReceived(Connection conn, String room, String inviter, String reason,
			String password, Message message) {
		

		XmppMuc.getInstance(context).joinRoom(conn, room, password,
				TChatApplication.getUserModel().getUsername());

	
		//NotificationHelper.getInstance(context).showRoomInvitationNotification(room, reason);
	}
}
