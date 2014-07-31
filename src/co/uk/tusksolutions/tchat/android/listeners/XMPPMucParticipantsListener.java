package co.uk.tusksolutions.tchat.android.listeners;

import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.ParticipantStatusListener;

import android.util.Log;

public class XMPPMucParticipantsListener implements ParticipantStatusListener{

	public XMPPMucParticipantsListener(){
		Log.d("XMPPMucParticipantsListener", "Registrered for XMPPMucParticipantsListener");
	}
	
	@Override
	public void adminGranted(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void adminRevoked(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void banned(String participant, String owner, String reason) {
		// TODO Auto-generated method stub
		Log.d("Banned","Banned from room info: " +  StringUtils.parseResource(participant) + " - "+ owner + " - " +reason);
		
	}

	@Override
	public void joined(String arg0) {
		// TODO Auto-generated method stub
		
		Log.d("JOINED", StringUtils.parseResource(arg0) + " has joined the room");
	}

	@Override
	public void kicked(String participant, String owner, String reason) {
		// TODO Auto-generated method stub
		Log.d("kicked","Kicked from room info: " +  StringUtils.parseResource(participant) + " - "+ owner + " - " +reason);
	}

	@Override
	public void left(String arg0) {
		// TODO Auto-generated method stub
		Log.d("LEFT",  StringUtils.parseResource(arg0) + " has left the room");
	}

	@Override
	public void membershipGranted(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void membershipRevoked(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void moderatorGranted(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void moderatorRevoked(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void nicknameChanged(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ownershipGranted(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ownershipRevoked(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void voiceGranted(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void voiceRevoked(String arg0) {
		// TODO Auto-generated method stub
		
	}

}
