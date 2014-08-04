package co.uk.tusksolutions.tchat.android.tasks;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import co.uk.tusksolutions.tchat.android.models.RosterModel;
import co.uk.tusksolutions.tchat.android.xmpp.XMPPMUCManager;

public class AddPeopleToGroup extends AsyncTask<Void, Void, Boolean> {

	private ProgressDialog progressDialog;
	public Context context;
	private String groupId;
	private String groupName;
	private OnAddPeopleMUCListener listener;
	private ArrayList<RosterModel> friendArrayList;

	public AddPeopleToGroup(Context context, String groupName,
			String groupId, ArrayList<RosterModel> friendArrayList,
			OnAddPeopleMUCListener listener) {
		this.context = context;
		this.groupName = groupName;
		this.groupId = groupId;
		this.friendArrayList = friendArrayList;
		this.listener = listener;

	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		progressDialog = new ProgressDialog(context);
		progressDialog.setMessage("Adding, please wait...");
		progressDialog.show();
		progressDialog.setCancelable(false);
	}

	@Override
	protected Boolean doInBackground(Void... voids) {

		XMPPMUCManager xmppMucManager = XMPPMUCManager.getInstance(context);
		
		for (int i = 0; i < friendArrayList.size(); i++) {
			if (friendArrayList.get(i).isSelected()) {
				String friendJID = friendArrayList.get(i).user;
				xmppMucManager
						.inviteBuddyToGroup(friendJID, groupName, groupId);
			}
		}

		return true;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {

		if (progressDialog != null)
			progressDialog.dismiss();
		if (result) {

			listener.onAddPeopleMUCSuccess(friendArrayList);

		} else {
			listener.onAddPeopleMUCFailed();
		}

		super.onPostExecute(result);
	}

	public interface OnAddPeopleMUCListener {
		void onAddPeopleMUCSuccess(ArrayList<RosterModel> addedFriendsList);
		void onAddPeopleMUCFailed();
	}

}
