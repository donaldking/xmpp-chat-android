package co.uk.tusksolutions.tchat.android.activities;

import java.util.ArrayList;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.muc.MultiUserChat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.adapters.GroupFriendsSelectionAdapter;
import co.uk.tusksolutions.tchat.android.models.RosterModel;

public class GroupFriendsSelectionActivity extends ActionBarActivity implements
		TextWatcher {

	public EditText searchInput;
	public static ListView listView;
	public String TAG = "RosterFragment";
	private static GroupFriendsSelectionAdapter mAdapter;

	private static View mLodingStatusView;
	private static int shortAnimTime;
	public static ArrayList<RosterModel> mSelectedUserModel;
	private ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_group_chat);
		actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		listView = (ListView) findViewById(R.id.list_view);
		listView.setItemsCanFocus(false);
		listView.setFastScrollEnabled(true);
		actionBar.setTitle("New Message");

		searchInput = (EditText) findViewById(R.id.friend_add_edittext);
		searchInput.clearFocus();
		searchInput.setFocusableInTouchMode(true);
		searchInput.addTextChangedListener(this);
		shortAnimTime = getResources().getInteger(
				android.R.integer.config_shortAnimTime);

		mLodingStatusView = (View) findViewById(R.id.roster_loading_view);

		listView.setVerticalScrollBarEnabled(false);
		listView.setHorizontalScrollBarEnabled(false);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		mAdapter = new GroupFriendsSelectionAdapter();
		mSelectedUserModel = new ArrayList<RosterModel>();
		listView.setAdapter(mAdapter);

		scrollToTop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.group_friends_selection_menu, menu);
		return true;
	}

	public static void scrollToTop() {
		Log.d("ChatActivity", "ScrollToBottom");
		listView.setSelection(0);
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private static void showProgress(final boolean show) {

		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs TO_USER
		// fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {

			mLodingStatusView.setVisibility(View.VISIBLE);
			mLodingStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLodingStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.*/
			mLodingStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

		mAdapter.getFilter().filter(s);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.submit_next:
			Toast.makeText(GroupFriendsSelectionActivity.this,
					"implementing group chat...", Toast.LENGTH_SHORT).show();

			TChatApplication.getUserModel().getUsername();

			String roomName = TChatApplication.getUserModel().getUsername()
					+ "_" + System.currentTimeMillis();
			String roomJID = roomName
					+ "@conference."
					+ co.uk.tusksolutions.tchat.android.constants.Constants.STAGING_SERVER;
			String nickname = TChatApplication.getUserModel().getUsername();

			try {
				createRoom(roomName, roomJID, nickname, "");
			} catch (XMPPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

	}

	private MultiUserChat createRoom(String roomName, String roomJID,
			String nickname, String password) throws XMPPException {

		MultiUserChat multiUserChat = null;

		// final String subjectInviteStr = getRoomString(number, name);

		Log.i("Creating room [%s]", roomJID);

		// See issue 136
		try {
			if (TChatApplication.connection != null)

			{

				multiUserChat = new MultiUserChat(TChatApplication.connection,
						roomJID);
			} else {
				TChatApplication.connection = TChatApplication
						.createNewConnection();
				Log.e("connection closed ", "connection "
						+ TChatApplication.connection);
				multiUserChat = new MultiUserChat(TChatApplication.connection,
						roomJID);

			}

			boolean service = MultiUserChat.isServiceEnabled(
					TChatApplication.connection,
					TChatApplication.getCurrentJid());
			Log.e("TAG", "service " + service);

		} catch (Exception e) {

		}

		try {
			multiUserChat.create(nickname);

			// multiUserChat.join(nickname);

		} catch (Exception e) {
			Log.e("MUC create", "MUC creation failed: ");
			throw new XMPPException("MUC creation failed for " + nickname
					+ ": " + e.getLocalizedMessage(), e);
		}

		try {
			// We send an empty configuration to the server. For some reason the
			// server doesn't accept or process our
			// completed form, so we just send an empty one. The server defaults
			// will be used which are fine.
			multiUserChat.sendConfigurationForm(new Form(Form.TYPE_SUBMIT));

			multiUserChat.changeSubject(roomName);

		} catch (XMPPException e1) {
			Log.d(e1.toString(),
					"Unable to send conference room configuration form.");
			// then we also should not send an invite as the room will be locked
			throw e1;
		}

		// Sleep few seconds between creation and invite new user
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {

		}

		/*
		 * if (info.isPasswordProtected()) { multiUserChat.join(nickname,
		 * password, discussionHistory, JOIN_TIMEOUT); } else {
		 * multiUserChat.join(nickname, null, discussionHistory, JOIN_TIMEOUT);
		 * }
		 */

		multiUserChat.join(nickname, null, null, 1000);
		// IMPORTANT you should join before registerRoom
		// registerRoom(multiUserChat, roomJID, roomName, password);

		return multiUserChat;
	}

}
