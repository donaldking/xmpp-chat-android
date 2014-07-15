package co.uk.tusksolutions.tchat.android.activities;

import java.util.ArrayList;

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
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import co.uk.tusksolutions.extensions.RobotoBoldTextView;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.adapters.GroupFriendsAdapter;
import co.uk.tusksolutions.tchat.android.models.GroupItemsModel;

public class GroupChatActivity extends ActionBarActivity implements TextWatcher {

	public EditText searchView;
	public static ListView listView;
	public String TAG = "GroupChatActivity";
	private static GroupFriendsAdapter mAdapter;
	private static View mLodingStatusView;
	private static int shortAnimTime;
	private GroupItemsModel mModel;
	static RobotoBoldTextView selected_user;
	public static ArrayList<GroupItemsModel> rosterModelCollection;
	StringBuilder builder;
	public static ArrayList<String> users_selected_array = new ArrayList<String>();
	ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_chat_activity);
		actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);

		actionBar.setDisplayHomeAsUpEnabled(true);

		mModel = new GroupItemsModel();

		rosterModelCollection = new ArrayList<GroupItemsModel>();
		listView = (ListView) findViewById(R.id.group_list_view);
		listView.setItemsCanFocus(false);
		listView.setFastScrollEnabled(true);

		selected_user = (RobotoBoldTextView) findViewById(R.id.selected_user_group);
		actionBar.setTitle("New Message");
		searchView = (EditText) findViewById(R.id.friend_add_edittext);
		searchView.clearFocus();
		searchView.setFocusableInTouchMode(true);
		searchView.addTextChangedListener(this);
		builder = new StringBuilder();
		shortAnimTime = getResources().getInteger(
				android.R.integer.config_shortAnimTime);

		mLodingStatusView = (View) findViewById(R.id.roster_loading_view);

		listView.setVerticalScrollBarEnabled(false);
		listView.setHorizontalScrollBarEnabled(false);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		rosterModelCollection = mModel.queryAllFriends();
		mAdapter = new GroupFriendsAdapter(getApplicationContext(),
				rosterModelCollection);
		listView.setAdapter(mAdapter);

		scrollToTop();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.group_activity_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onStart() {
		super.onStart();

		users_selected_array = new ArrayList<String>();
	}

	protected void setNametoTextView() {
		builder = new StringBuilder();
		for (String s : users_selected_array) {

			builder.append(s).append(", ");

		}

		selected_user.setText(builder.toString());
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
		if (s.length() > 0) {

			performSearch(s);
		} else {
			rosterModelCollection = mModel.queryAllFriends();
			mAdapter = new GroupFriendsAdapter(this, rosterModelCollection);
			listView.setAdapter(mAdapter);
			scrollToTop();
		}
	}

	public void performSearch(CharSequence s) {
		rosterModelCollection = mModel.querySearch(s.toString());
		mAdapter = new GroupFriendsAdapter(this, rosterModelCollection);
		listView.setAdapter(mAdapter);
		Log.d("TCHAT", "result Size " + mAdapter.getCount());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.submit_next:
			Toast.makeText(GroupChatActivity.this,
					"implementing group chat...", Toast.LENGTH_SHORT).show();
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

	public static void showSelectedItems() {
		final StringBuffer sb = new StringBuffer("To: ");

		// Get an array that tells us for each position whether the item is
		// checked or not
		// --
		final SparseBooleanArray checkedItems = listView
				.getCheckedItemPositions();
		if (checkedItems == null) {

			selected_user.setText("");
			Toast.makeText(TChatApplication.getContext(),
					"Not Selected Any friend", Toast.LENGTH_LONG).show();
			return;
		}

		// For each element in the status array
		// --
		boolean isFirstSelected = true;
		final int checkedItemsCount = checkedItems.size();
		for (int i = 0; i < checkedItemsCount; ++i) {
			// This tells us the item position we are looking at
			// --
			final int position = checkedItems.keyAt(i);

			// This tells us the item status at the above position
			// --
			final boolean isChecked = checkedItems.valueAt(i);

			if (isChecked) {
				if (!isFirstSelected) {
					sb.append(", ");
				}
				sb.append(rosterModelCollection.get(position).name);
				users_selected_array.add(sb.toString());

				isFirstSelected = false;
			} else {
				selected_user.setText(sb);
			}

			for (String s : users_selected_array) {
				StringBuilder name = new StringBuilder();
				name.append(s).append(", ");
				selected_user.setText(name.toString());

			}
		}

	}

}
