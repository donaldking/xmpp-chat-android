package co.uk.tusksolutions.tchat.android.listeners;

import org.jivesoftware.smackx.muc.UserStatusListener;

import android.util.Log;
import co.uk.tusksolutions.tchat.android.models.GroupsModel;

public class XMPPMucUserStatusListener implements UserStatusListener {

	public String roomId;
	
	public XMPPMucUserStatusListener(String roomId){
		this.roomId = roomId;
	}
	
	@Override
	public void adminGranted() {
		// TODO Auto-generated method stub

	}

	@Override
	public void adminRevoked() {
		// TODO Auto-generated method stub

	}

	@Override
	public void banned(String owner, String reason) {

		Log.d("Banned", "Banned from room " + owner + " - " + reason);
		GroupsModel.joinAllGroups();

	}

	@Override
	public void kicked(String owner, String reason) {
		/*
		 * Called when we are booted from a room
		 */
		Log.d("kicked", "Kicked from room " + owner + " - " + reason + " Room ID: " + roomId);
		GroupsModel.joinAllGroups();
	}

	@Override
	public void membershipGranted() {
		// TODO Auto-generated method stub

	}

	@Override
	public void membershipRevoked() {
		Log.d("membershipRevoked", "Membership revoked from room ");
	}

	@Override
	public void moderatorGranted() {
		// TODO Auto-generated method stub

	}

	@Override
	public void moderatorRevoked() {
		// TODO Auto-generated method stub

	}

	@Override
	public void ownershipGranted() {
		// TODO Auto-generated method stub

	}

	@Override
	public void ownershipRevoked() {
		// TODO Auto-generated method stub

	}

	@Override
	public void voiceGranted() {
		// TODO Auto-generated method stub

	}

	@Override
	public void voiceRevoked() {
		// TODO Auto-generated method stub

	}

}
