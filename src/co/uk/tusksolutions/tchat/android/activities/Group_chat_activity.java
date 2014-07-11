package co.uk.tusksolutions.tchat.android.activities;

import java.util.ArrayList;

import co.uk.tusksolutions.extensions.RobotoBoldTextView;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.adapters.GroupContentAdapter;
import co.uk.tusksolutions.tchat.android.models.RosterModel;
import co.uk.tusksolutions.tchat.android.viewHolders.GroupViewHolder;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

public class Group_chat_activity extends ActionBarActivity implements
		TextWatcher {

	public EditText searchView;
	public static ListView listView;
	public String TAG = "RosterFragment";
	private View rootView;
	private static GroupContentAdapter mAdapter;
	private IntentFilter filter;
	// private RosterReceiver mRosterReceiver;
	private static View mLodingStatusView;
	private static int shortAnimTime;
	private static int ALL_QUERY_ACTION = 1; // See adapter for notes
	private int ONLINE_QUERY_ACTION = 2; // See adapter for notes

	private int SEARCH_ACTION = 3; // for search result

	private Bundle instanceState;

	private RosterModel mModel;
	

	RobotoBoldTextView selected_user;
	private int action;
	public static ArrayList<RosterModel> rosterModelCollection;
	
	
	StringBuilder builder;
	public static ArrayList<String> users_selected_array = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_chat_activity);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mModel = new RosterModel();
		
		
		rosterModelCollection = new ArrayList<RosterModel>();
	
		listView = (ListView) findViewById(R.id.list_view_group_friends);
		listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
		listView.setStackFromBottom(true);
		selected_user = (RobotoBoldTextView) findViewById(R.id.selected_user_group);
		getSupportActionBar().setTitle("New Message");
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
		mAdapter = new GroupContentAdapter(TChatApplication.getContext(),
				ALL_QUERY_ACTION);
		listView.setAdapter(mAdapter);
		
	

		scrollToTop();
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int postion, long id) {
				// TODO Auto-generated method stub
				GroupViewHolder groupViewHolder = new GroupViewHolder(view);
				Log.e("Debug",
						"User name click "
								+ GroupContentAdapter.rosterModelCollection
										.get(postion).name);
				
				

				if (!(users_selected_array
						.contains(GroupContentAdapter.rosterModelCollection
								.get(postion).name))) {
					selected_user.setText("");
					String friend_username = GroupContentAdapter.rosterModelCollection
							.get(postion).name;
					users_selected_array.add(friend_username);
					Log.e("debug", " add user " + friend_username);
					setNametoTextView();

					groupViewHolder.rosterPresenceFrame
							.setVisibility(view.VISIBLE);
					
					
					

				} else {
					String friend_username = GroupContentAdapter.rosterModelCollection
							.get(postion).name;

					Log.e("debug", " removed user " + friend_username);
					users_selected_array.remove(friend_username);
					setNametoTextView();
				
					groupViewHolder.rosterPresenceFrame
							.setVisibility(view.GONE);
				
				}

			}
		});

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

			builder.append(s).append(" ");

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

	private static void prepareListView(final int queryInt) {

		/**
		 * Load Friends fromUser DB
		 */

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
			scrollToTop();
		}
	}

	public void performSearch(CharSequence s) {
		rosterModelCollection = mModel.querySearch(s.toString());
		mAdapter = new GroupContentAdapter(TChatApplication.getContext(),
				SEARCH_ACTION);
		Log.d("TCHAT", "result Size " + mAdapter.getCount());
		if (mAdapter.getCount() == 0) {
			if (TChatApplication.CHAT_SECTION_QUERY_ACTION == 2) {
				showProgress(false);
			} else {
				showProgress(true);
			}

			listView.setVisibility(View.GONE);
		} else {
			showProgress(false);
			listView.setAdapter(mAdapter);
			if (listView.getVisibility() != View.VISIBLE) {
				listView.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}
		return true;
	}

}
