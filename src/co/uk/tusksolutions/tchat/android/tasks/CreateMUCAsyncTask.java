package co.uk.tusksolutions.tchat.android.tasks;

import java.util.ArrayList;

import org.jivesoftware.smack.XMPPException;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.SparseBooleanArray;
import android.widget.ListView;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.adapters.GroupFriendsSelectionAdapter;
import co.uk.tusksolutions.tchat.android.models.RosterModel;
import co.uk.tusksolutions.tchat.android.xmpp.XmppMuc;

/**
 * @author Deepak Jangir deepakjangir07@gmail.com
 * 
 * 
 */
public class CreateMUCAsyncTask extends AsyncTask<Void, Void, Boolean> {

	ProgressDialog progressDialog;

	private final Context context;
	private final String roomName;
	private final OnCreateMUCListener listener;
	private ArrayList<RosterModel> friendArrayList;
	private final ListView friendsList;
	private boolean alreadyExists;
	private String errorMessage;

	public CreateMUCAsyncTask(final Context context, final String roomName,
			final ArrayList<RosterModel> friendArrayList,
			final ListView friendsList, final OnCreateMUCListener listener) {
		this.context = context;
		this.roomName = roomName;
		this.friendArrayList = friendArrayList;
		this.friendsList = friendsList;

		this.listener = listener;

	}

	@Override
	protected void onPreExecute() {

		progressDialog = ProgressDialog.show(context, "", "Please wait...");
		super.onPreExecute();

		progressDialog.setMessage("Creating Room...");
		progressDialog.show();
		progressDialog.setCancelable(true);

		super.onPreExecute();
	}

	@Override
	protected Boolean doInBackground(Void... voids) {

		try {

			XmppMuc xmppMuc = XmppMuc.getInstance(context);

			String roomJID = roomName
					+ "@conference."
					+ co.uk.tusksolutions.tchat.android.constants.Constants.STAGING_SERVER;

			friendArrayList = GroupFriendsSelectionAdapter.rosterModelCollection;

			for (int i = 0; i < friendArrayList.size(); i++) {
				if (friendArrayList.get(i).isSelected()) {

					String friendJID = friendArrayList.get(i).user;

					String password = "";
					xmppMuc.inviteToRoom(roomName, TChatApplication
							.getUserModel().getUsername(), friendJID, password,
							roomJID);
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

		// baseActivity.dismissPleaseWaitDialog();
		if (progressDialog != null)
			progressDialog.dismiss();
		if (result) {
			// listener.onCreateMUCSuccess(room);
			// MUCFriendsAdapter.checkedItems.clear();
		} else {
			listener.onCreateMUCFailed(alreadyExists, errorMessage);
		}

		super.onPostExecute(result);
	}

	public interface OnCreateMUCListener {
		void onCreateMUCSuccess(String room);

		void onCreateMUCFailed(boolean alreadyExists, String message);
	}
}
