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
import co.uk.tusksolutions.tchat.android.tasks.CreateMUCAsyncTask;

public class GroupFriendsSelectionActivity extends ActionBarActivity implements
		TextWatcher, CreateMUCAsyncTask.OnCreateMUCListener{

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
			String roomName = TChatApplication.getUserModel().getUsername()
					+ "_" + System.currentTimeMillis();
			
	new CreateMUCAsyncTask(GroupFriendsSelectionActivity.this, roomName,mSelectedUserModel , listView,this).execute();
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

	
	@Override
	public void onCreateMUCFailed(boolean alreadyExists, String message) {
		// TODO Auto-generated method stub
		

		
	}

	@Override
	public void onCreateMUCSuccess(String room) {
		// TODO Auto-generated method stub
		Log.v("Create Room Succes","Successfully create room  "+room);
		
	}

}
