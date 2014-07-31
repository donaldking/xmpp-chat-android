package co.uk.tusksolutions.tchat.android.listeners;

import org.jivesoftware.smackx.muc.SubjectUpdatedListener;

import android.util.Log;

public class XMPPMucSubjectUpdatedListener implements SubjectUpdatedListener {

	@Override
	public void subjectUpdated(String subject, String from) {
		// TODO Auto-generated method stub
		Log.d("XMPPMucSubjectUpdatedListener", "New subject: " + subject
				+ ", Set by: " + from);
	}

}
