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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.adapters.RosterContentAdapter;
import co.uk.tusksolutions.tchat.android.constants.Constants;

public class RosterFragment extends Fragment {

	public static ListView listView;
	public String TAG = "RosterFragment";
	private View rootView;
	private static RosterContentAdapter mAdapter;
	private IntentFilter filter;
	private RosterReceiver mRosterReceiver;
	private static View mLodingStatusView;
	private static int shortAnimTime;
	private int ALL_QUERY_ACTION = 1; // See adapter for notes
	private int ONLINE_QUERY_ACTION = 2; // See adapter for notes
	private static int CURRENT_QUERY_ACTION;
	private Button allButton, onlineButton;

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

		allButton = (Button) rootView.findViewById(R.id.all_button);
		allButton.setOnClickListener(new SegmentButtonOnClickListener());
		onlineButton = (Button) rootView.findViewById(R.id.online_button);
		onlineButton.setOnClickListener(new SegmentButtonOnClickListener());

		if (savedInstanceState != null) {
			CURRENT_QUERY_ACTION = savedInstanceState
					.getInt("currentQueryAction");
			prepareListView(CURRENT_QUERY_ACTION);
		} else {
			prepareListView(ALL_QUERY_ACTION);
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		filter = new IntentFilter();
		filter.addAction(Constants.ROSTER_EMPTY);
		filter.addAction(Constants.ROSTER_UPDATED);
		getActivity().registerReceiver(mRosterReceiver, filter);
	}

	@Override
	public void onStop() {
		super.onStop();
		getActivity().unregisterReceiver(mRosterReceiver);
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private static void showProgress(final boolean show) {

		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
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
		 * Load Friends from DB
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
				Log.d(TAG, "Switching to tab: " + ALL_QUERY_ACTION);
				RosterFragment.prepareListView(ALL_QUERY_ACTION);
				break;

			case R.id.online_button:
				Log.d(TAG, "Switching to tab: " + ONLINE_QUERY_ACTION);
				RosterFragment.prepareListView(ONLINE_QUERY_ACTION);
				break;

			default:
				break;
			}
		}
	}

	/*
	 * Broad cast receiver that gets called When we receive new data form cloud
	 * to db
	 */
	private class RosterReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			/*
			 * Load friends from db and reload the list view
			 */
			if (intent.getAction().equalsIgnoreCase(Constants.ROSTER_UPDATED)) {
				int inserts = intent.getExtras().getInt("inserts");
				if (inserts > mAdapter.getCount() && CURRENT_QUERY_ACTION == 1) {
					Toast.makeText(
							context,
							intent.getExtras().getInt("inserts")
									+ " Inserted!: All", Toast.LENGTH_SHORT)
							.show();
					prepareListView(ALL_QUERY_ACTION);
				} else if (inserts > mAdapter.getCount()
						&& CURRENT_QUERY_ACTION == 2) {
					Toast.makeText(
							context,
							intent.getExtras().getInt("inserts")
									+ " Inserted!: Online ", Toast.LENGTH_SHORT)
							.show();
					prepareListView(ONLINE_QUERY_ACTION);
				} else {
					Toast.makeText(context, "Do Nothing!", Toast.LENGTH_SHORT)
							.show();
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
