package co.uk.tusksolutions.tchat.android.tasks;

import java.util.ArrayList;

import org.jivesoftware.smack.XMPPException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.models.RosterModel;
import co.uk.tusksolutions.tchat.android.xmpp.XMPPMUCManager;

public class CreateMUCAsyncTask extends AsyncTask<Void, Void, Boolean> {

	ProgressDialog progressDialog;

	private final Context context;
	private final String roomName;
	private final OnCreateMUCListener listener;
	private ArrayList<RosterModel> friendArrayList;
	private boolean alreadyExists;
	private String errorMessage = "Error creating room";
	String roomJID;

	public CreateMUCAsyncTask(final Context context, final String roomName,
			final ArrayList<RosterModel> friendArrayList,
			final OnCreateMUCListener listener) {
		this.context = context;
		this.roomName = roomName;
		this.friendArrayList = friendArrayList;
		this.listener = listener;

	}

	@Override
	protected void onPreExecute() {

		progressDialog = ProgressDialog.show(context, "", "Please wait...");
		super.onPreExecute();

		progressDialog.setMessage("Creating Group...");
		progressDialog.show();
		progressDialog.setCancelable(false);

		super.onPreExecute();
	}

	@Override
	protected Boolean doInBackground(Void... voids) {

		try {

			/**
			 * Build group name from buddy names. This is used for sending
			 * invitation reason.
			 */
			int index = 0;
			StringBuilder group_name = new StringBuilder();
			for (RosterModel rosterModel : friendArrayList) {
				group_name.append(rosterModel.name);
				index++;
				if (index < friendArrayList.size()) {
					group_name.append(", ");
				}
			}

			XMPPMUCManager xmppMucManager = XMPPMUCManager.getInstance(context);
			roomJID = roomName + "@conference." + Constants.CURRENT_SERVER;

			for (int i = 0; i < friendArrayList.size(); i++) {
				if (friendArrayList.get(i).isSelected()) {
					String friendJID = friendArrayList.get(i).user;
					xmppMucManager.inviteToRoom(roomName, TChatApplication
							.getUserModel().getUsername(), friendJID, "",
							roomJID, group_name.toString());
				}
			}

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

			listener.onCreateMUCSuccess(roomName, roomJID, friendArrayList);

		} else {
			listener.onCreateMUCFailed(alreadyExists, errorMessage);
		}

		super.onPostExecute(result);
	}

	public interface OnCreateMUCListener {
		void onCreateMUCSuccess(String room, String roomjid,
				ArrayList<RosterModel> friendArrayList);

		void onCreateMUCFailed(boolean alreadyExists, String message);
	}
}
