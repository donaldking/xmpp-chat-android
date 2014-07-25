package co.uk.tusksolutions.tchat.android.listeners;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.InvitationListener;

import android.content.Context;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.api.APIGetGroups;
import co.uk.tusksolutions.tchat.android.xmpp.XMPPMUCManager;

public class XMPPMucInvitationListener implements InvitationListener {

	private final Context context;

	public XMPPMucInvitationListener(Context context) {
		this.context = context;
	}

	@Override
	public void invitationReceived(Connection conn, String room,
			String inviter, String reason, String password, Message message) {

		// Load groups from API
		APIGetGroups groupsApi = new APIGetGroups();
		groupsApi.getGroups();
		
		//if (TChatApplication.getGroupsModel().saveCreatedRoomInDB(room, reason,
			//	StringUtils.parseBareAddress(inviter), "")) {
			XMPPMUCManager.getInstance(context).joinRoom(conn, room, password,
					TChatApplication.getUserModel().getUsername());
		//}
	}
}
