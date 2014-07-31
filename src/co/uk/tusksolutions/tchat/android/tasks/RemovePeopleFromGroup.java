package co.uk.tusksolutions.tchat.android.tasks;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.models.RosterModel;
import co.uk.tusksolutions.tchat.android.xmpp.XMPPMUCManager;

public class RemovePeopleFromGroup extends AsyncTask<Void, Void, Boolean> {

	private ProgressDialog progressDialog;
	public Context context;
	private final String groupId;
	private final OnRemoveMUCListener listener;
	private ArrayList<RosterModel> friendArrayList;

	public RemovePeopleFromGroup(final Context context, final String groupId,
			final ArrayList<RosterModel> friendArrayList,
			final OnRemoveMUCListener listener) {
		this.context = context;
		this.groupId = groupId;
		this.friendArrayList = friendArrayList;
		this.listener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		progressDialog = new ProgressDialog(context);
		progressDialog.setMessage("Removing, please wait...");
		progressDialog.show();
		progressDialog.setCancelable(false);
	}

	@Override
	protected Boolean doInBackground(Void... params) {

		XMPPMUCManager xmppMucManager = XMPPMUCManager.getInstance(context);
		xmppMucManager.mucServiceDiscovery();
		
		if (xmppMucManager.kickFromRoom(TChatApplication.connection,
				friendArrayList, groupId)) {
			Log.d("TAG", "Successfully removed all from group");
		}
		return true;
	}

	public interface OnRemoveMUCListener {
		void onRemoveMUCSuccess(ArrayList<RosterModel> removedFriendsList);
		void onRemoveMUCFailed();
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		
		if (progressDialog != null){
			progressDialog.dismiss();
		}
		if (result == true) {
			listener.onRemoveMUCSuccess(friendArrayList);
		}else{
			listener.onRemoveMUCFailed();
		}
	}

}
