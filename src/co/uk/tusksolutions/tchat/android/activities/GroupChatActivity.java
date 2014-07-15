package co.uk.tusksolutions.tchat.android.activities;

import java.util.ArrayList;

import co.uk.tusksolutions.extensions.RobotoBoldTextView;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.adapters.GroupContentAdapter;
import co.uk.tusksolutions.tchat.android.adapters.GroupFriendsAdapter;
import co.uk.tusksolutions.tchat.android.models.GroupItemsModel;
import co.uk.tusksolutions.tchat.android.models.RosterModel;
import co.uk.tusksolutions.tchat.android.viewHolders.GroupViewHolder;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment.SavedState;
import android.app.ListActivity;
import android.content.IntentFilter;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar.OnMenuVisibilityListener;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class GroupChatActivity extends ListActivity implements TextWatcher {

	public EditText searchView;
	public static ListView listView;
	public String TAG = "RosterFragment";
	private View rootView;
	private static GroupFriendsAdapter mAdapter;
	private IntentFilter filter;
	// private RosterReceiver mRosterReceiver;
	private static View mLodingStatusView;
	private static int shortAnimTime;
	private static int ALL_QUERY_ACTION = 1; // See adapter for notes
	private int ONLINE_QUERY_ACTION = 2; // See adapter for notes

	private int SEARCH_ACTION = 3; // for search result

	private Bundle instanceState;

	private GroupItemsModel mModel;

	 static RobotoBoldTextView selected_user;
	private int action;
	public static ArrayList<GroupItemsModel> rosterModelCollection;
	StringBuilder builder;
	public static ArrayList<String> users_selected_array = new ArrayList<String>();
	android.app.ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_chat_activity);
		actionBar = getActionBar();
		// getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		
actionBar.setDisplayHomeAsUpEnabled(true);

		mModel = new GroupItemsModel();

		rosterModelCollection = new ArrayList<GroupItemsModel>();
		listView = getListView();
		listView.setItemsCanFocus(false);
		listView.setFastScrollEnabled(true);
	
		selected_user = (RobotoBoldTextView) findViewById(R.id.selected_user_group);
		actionBar.setTitle("New Message");
		searchView = (EditText) findViewById(R.id.friend_add_edittext);
		searchView.clearFocus();
		searchView.setFocusableInTouchMode(true);
		searchView.addTextChangedListener(this);
		instanceState = savedInstanceState;
		builder = new StringBuilder();
		shortAnimTime = getResources().getInteger(
				android.R.integer.config_shortAnimTime);

		mLodingStatusView = (View) findViewById(R.id.roster_loading_view);

		listView.setVerticalScrollBarEnabled(false);
		listView.setHorizontalScrollBarEnabled(false);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		rosterModelCollection = mModel.queryAllFriends();
		mAdapter= new GroupFriendsAdapter(getApplicationContext(),
				rosterModelCollection);
		setListAdapter(mAdapter);
          
		scrollToTop();
		

	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.activity_main_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		users_selected_array = new ArrayList<String>();
	}

	protected void setNametoTextView() {
		// TODO Auto-generated method stub
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

	/*private static void prepareListView(final int queryInt) {

		*//**
		 * Load Friends fromUser DB
		 *//*

		TChatApplication.CHAT_SECTION_QUERY_ACTION = queryInt;
		mAdapter = new GroupContentAdapter(TChatApplication.getContext(),
				queryInt);

		if (mAdapter.getCount() == 0) {
			if (TChatApplication.CHAT_SECTION_QUERY_ACTION == 2) {
				showProgress(false);
			} else {
				showProgress(true);
			}
			listView.setVisibility(View.GONE);
		} else {
			// showProgress(false);
			listView.setAdapter(mAdapter);
			if (listView.getVisibility() != View.VISIBLE) {
				listView.setVisibility(View.VISIBLE);
			}
		}
	}
*/
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
			rosterModelCollection=mModel.queryAllFriends();
			mAdapter = new GroupFriendsAdapter(this,rosterModelCollection);
			setListAdapter(mAdapter);
			scrollToTop();
		}
	}

	public void performSearch(CharSequence s) {
		rosterModelCollection = mModel.querySearch(s.toString());
		mAdapter = new GroupFriendsAdapter(this,rosterModelCollection);
		setListAdapter(mAdapter);
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
		final SparseBooleanArray checkedItems = listView.getCheckedItemPositions();
		users_selected_array=new ArrayList<String>();
		if (checkedItems == null) {
			
		selected_user.setText("");
			Toast.makeText(TChatApplication.getContext(), "Not Selected Any friend", Toast.LENGTH_LONG).show();
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
			}
			else
			{
				selected_user.setText(sb);
			}
			Log.e("DEbug","size "+users_selected_array.size());
			for(String s:users_selected_array)
			{
			
				StringBuilder name=new StringBuilder();
			     name.append(s).append(", ");
				selected_user.setText(name.toString());
				
			}
		}

	}
	
	


}
