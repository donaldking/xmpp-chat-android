package co.uk.tusksolutions.tchat.android.activities;

import java.util.ArrayList;

import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.adapters.GroupContentAdapter;
import co.uk.tusksolutions.tchat.android.adapters.RosterContentAdapter;
import co.uk.tusksolutions.tchat.android.models.RosterModel;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
	private int action;
	public static ArrayList<RosterModel> rosterModelCollection;

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
		
		getSupportActionBar().setTitle("New Message");
		searchView = (EditText) findViewById(R.id.friend_add_edittext);
		searchView.setFocusableInTouchMode(true);
		searchView.addTextChangedListener(this);
		instanceState = savedInstanceState;

		shortAnimTime = getResources().getInteger(
				android.R.integer.config_shortAnimTime);

		mLodingStatusView = (View) findViewById(R.id.roster_loading_view);

		listView.setVerticalScrollBarEnabled(false);
		listView.setHorizontalScrollBarEnabled(false);
		mAdapter = new GroupContentAdapter(TChatApplication.getContext(),
				ALL_QUERY_ACTION);
		listView.setAdapter(mAdapter);

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

			listView.setVisibility(View.GONE);
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

}
