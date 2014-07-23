package co.uk.tusksolutions.tchat.android.activities;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.adapters.GroupFriendsSelectionAdapter;
import co.uk.tusksolutions.tchat.android.models.RosterModel;
import co.uk.tusksolutions.tchat.android.tasks.CreateMUCAsyncTask;

public class GroupFriendsSelectionActivity extends ActionBarActivity implements
		TextWatcher, CreateMUCAsyncTask.OnCreateMUCListener {

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

		setContentView(R.layout.activity_group_friends_selection);
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

			if (searchInputCleared()) {

				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						handleSelection();

					}
				}, 100);
			}

			break;
		default:
			break;
		}
		return true;
	}

	private boolean searchInputCleared() {
		searchInput.setText("");
		mAdapter.getFilter().filter(searchInput.getText().toString());

		if (searchInput.length() == 0) {
			return true;
		}
		return false;
	}

	private void handleSelection() {
		ArrayList<RosterModel> totalSelectedModel = new ArrayList<RosterModel>();

		for (RosterModel selectedModel : GroupFriendsSelectionAdapter.rosterModelCollection) {
			if (selectedModel.isSelected()) {
				totalSelectedModel.add(selectedModel);
			}
		}

		/** Check if we need to create a group or a normal conversation **/
		if (totalSelectedModel.size() > 1) {
			hideKeyboard();
			// Create group as we have selected more than one person

			String roomName = TChatApplication.getUserModel().getUsername()
					+ "_" + System.currentTimeMillis();

			new CreateMUCAsyncTask(GroupFriendsSelectionActivity.this,
					roomName, totalSelectedModel, this).execute();

		} else if (totalSelectedModel.size() == 1) {
			// Start a normal conversation as we only selected one person
			hideKeyboard();

			Bundle b = new Bundle();
			b.putString("roomJid", totalSelectedModel.get(0).user);
			b.putString("friendName", totalSelectedModel.get(0).name);

			Intent intent = new Intent(TChatApplication.getContext(),
					ChatActivity.class);
			intent.putExtra("chatWithFriendBundle", b);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			TChatApplication.getContext().startActivity(intent);
		} else {
			Log.d(TAG, "No selection made");
		}
	}

	public void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

	}

	@Override
	public void onCreateMUCFailed(boolean alreadyExists, String message) {
		Log.v("Create Room Error", message);
	}

	@Override
	public void onCreateMUCSuccess(String room, String roomJid,
			ArrayList<RosterModel> participantsList) {

		StringBuilder group_name = new StringBuilder();
		String group_admin = TChatApplication.getCurrentJid();
		JSONArray jsonArray = new JSONArray();
		JSONObject participant;

		try {
			int index = 0;
			for (RosterModel rosterModel : participantsList) {
				participant = new JSONObject();
				participant.put("user_id", rosterModel.user);
				jsonArray.put(participant);
				group_name.append(rosterModel.name);
				index++;
				if (index < participantsList.size()) {
					group_name.append(", ");
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		JSONObject Obj = new JSONObject();
		try {
			Obj.put("participants", jsonArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String jsonStr = Obj.toString();
		if (TChatApplication.getGroupsModel().saveCreatedRoomInDB(room,
				group_name.toString(), group_admin, jsonStr)) {

			Bundle b = new Bundle();
			b.putString("roomJid", roomJid);
			b.putString("roomName", group_name.toString());

			Intent intent = new Intent(TChatApplication.getContext(),
					GroupChatActivity.class);
			intent.putExtra("groupChatToRoomBundle", b);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			TChatApplication.getContext().startActivity(intent);
		}
	}

}
