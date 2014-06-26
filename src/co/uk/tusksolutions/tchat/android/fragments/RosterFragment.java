package co.uk.tusksolutions.tchat.android.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioButton;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.adapters.RosterContentAdapter;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.listeners.XMPPPresenceListener;

public class RosterFragment extends Fragment {

	public static ListView listView;
	public String TAG = "RosterFragment";
	private View rootView;
	private static RosterContentAdapter mAdapter;
	private IntentFilter filter;
	private RosterReceiver mRosterReceiver;
	private static View mLodingStatusView;
	private static int shortAnimTime;
	private static int ALL_QUERY_ACTION = 1; // See adapter for notes
	private int ONLINE_QUERY_ACTION = 2; // See adapter for notes
	private static int CURRENT_QUERY_ACTION;
	private RadioButton allButton, onlineButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = (View) inflater.inflate(R.layout.fragment_roster, container,
				false);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mRosterReceiver = new RosterReceiver();
		shortAnimTime = getResources().getInteger(
				android.R.integer.config_shortAnimTime);

		mLodingStatusView = getActivity().findViewById(R.id.loading_view);
		listView = (ListView) rootView.findViewById(R.id.list_view);
		listView.setVerticalScrollBarEnabled(false);
		listView.setHorizontalScrollBarEnabled(false);

		allButton = (RadioButton) rootView.findViewById(R.id.all_button);
		allButton.setOnClickListener(new SegmentButtonOnClickListener());
		onlineButton = (RadioButton) rootView.findViewById(R.id.online_button);
		onlineButton.setOnClickListener(new SegmentButtonOnClickListener());

		if (savedInstanceState != null && mAdapter != null) {
			CURRENT_QUERY_ACTION = savedInstanceState
					.getInt("currentQueryAction");
			prepareListView(CURRENT_QUERY_ACTION);
		} else {
			CURRENT_QUERY_ACTION = 1;
			prepareListView(CURRENT_QUERY_ACTION);
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		filter = new IntentFilter();
		filter.addAction(Constants.ROSTER_EMPTY);
		filter.addAction(Constants.ROSTER_UPDATED);
		getActivity().registerReceiver(mRosterReceiver, filter);

		if (TChatApplication.getRosterModel().queryAll().size() == 0) {
			showProgress(true);
			new Thread() {
				public void run() {
					XMPPPresenceListener.loadRoster();
				}
			}.start();
		} else {
			prepareListView(CURRENT_QUERY_ACTION);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private static void showProgress(final boolean show) {

		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs TO_USER fade-in
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

		CURRENT_QUERY_ACTION = queryInt;
		mAdapter = new RosterContentAdapter(TChatApplication.getContext(),
				queryInt);

		if (mAdapter.getCount() == 0) {
			showProgress(true);
			listView.setVisibility(View.GONE);
		} else {
			showProgress(false);
			listView.setAdapter(mAdapter);
			if (listView.getVisibility() != View.VISIBLE) {
				listView.setVisibility(View.VISIBLE);
			}
		}
	}

	/*
	 * Segment button onClick listener
	 */
	private class SegmentButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.all_button:
				Log.d(TAG, "Switching TO_USER tab: " + ALL_QUERY_ACTION);
				RosterFragment.prepareListView(ALL_QUERY_ACTION);
				break;

			case R.id.online_button:
				Log.d(TAG, "Switching TO_USER tab: " + ONLINE_QUERY_ACTION);
				RosterFragment.prepareListView(ONLINE_QUERY_ACTION);
				break;

			default:
				break;
			}
		}
	}

	/*
	 * Broad cast fromUser that gets called When we receive new data form cloud
	 * TO_USER db
	 */
	private class RosterReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			/*
			 * Load friends fromUser db and reload the list view
			 */
			if (intent.getAction().equalsIgnoreCase(Constants.ROSTER_UPDATED)) {

				if (intent.getExtras() != null) {
					int inserts = intent.getExtras().getInt("inserts");
					if (inserts > mAdapter.getCount()
							&& CURRENT_QUERY_ACTION == 1) {
						prepareListView(ALL_QUERY_ACTION);
					}
				} else if (CURRENT_QUERY_ACTION == ONLINE_QUERY_ACTION) {
					prepareListView(ONLINE_QUERY_ACTION);
				}

			} else if (intent.getAction().equalsIgnoreCase(
					Constants.ROSTER_EMPTY)) {
				showProgress(false);
				Log.i(TAG, "Online Roster Empty!");
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt("currentQueryAction", CURRENT_QUERY_ACTION);
	}

}
