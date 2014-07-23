package co.uk.tusksolutions.tchat.android.listeners;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.InvitationListener;

import android.content.Context;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.xmpp.XMPPMUCManager;

public class XmppMucInvitationListener implements InvitationListener {

	private final Context context;

	public XmppMucInvitationListener(Context context) {
		this.context = context;
	}

	@Override
	public void invitationReceived(Connection conn, String room,
			String inviter, String reason, String password, Message message) {

		XMPPMUCManager.getInstance(context).joinRoom(conn, room, password,
				TChatApplication.getUserModel().getUsername());

		// NotificationHelper.getInstance(context).showRoomInvitationNotification(room,
		// reason);
	}
}
