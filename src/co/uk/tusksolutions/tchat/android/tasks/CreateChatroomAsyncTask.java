package co.uk.tusksolutions.tchat.android.tasks;

import org.jivesoftware.smack.XMPPException;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.xmpp.XMPPMUCManager;

public class CreateChatroomAsyncTask extends AsyncTask<Void, Void, Boolean> {

	ProgressDialog progressDialog;

	private final Context context;
	private final String chatroomName;
	private final OnCreateChatroomListener listener;
	private boolean alreadyExists;
	private String errorMessage = "Error creating chatroom";
	String chatroomJID;

	public CreateChatroomAsyncTask(final Context context, final String roomName,
			final OnCreateChatroomListener listener) {
		this.context = context;
		this.chatroomName = roomName;
		
		this.listener = listener;

	}

	@Override
	protected void onPreExecute() {

		progressDialog = new ProgressDialog(context);
		super.onPreExecute();
		
		progressDialog.setMessage("Creating Chatroom...");
		progressDialog.show();
		progressDialog.setCancelable(false);

		super.onPreExecute();
	}

	@Override
	protected Boolean doInBackground(Void... voids) {

		try {

			/**
			 * Build chat room name from buddy names. This is used for sending
			 * invitation reason.
			 */
			String password="";
			XMPPMUCManager xmppMucManager = XMPPMUCManager.getInstance(context);
			chatroomJID = TChatApplication.getUserModel().getUsername()+"_"+System.currentTimeMillis()+"@conference." + Constants.CURRENT_SERVER;
			
			xmppMucManager.CreateRoom(chatroomName, chatroomJID, TChatApplication
							.getUserModel().getUsername(), password);
		
			

		} catch (XMPPException e) {
			alreadyExists = false;

			return false;
		}

		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {

		if (progressDialog != null)
			progressDialog.dismiss();
		if (result) {

			listener.onCreatechatroomSuccess(chatroomName, chatroomJID);

		} else {
			listener.onCreateChatroomFailed(alreadyExists, errorMessage);
		}

		super.onPostExecute(result);
	}

	public interface OnCreateChatroomListener {
		void onCreatechatroomSuccess(String room, String roomjid);
		void onCreateChatroomFailed(boolean alreadyExists, String message);
	}
}
