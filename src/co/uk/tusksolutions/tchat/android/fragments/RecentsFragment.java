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
import android.view.ViewGroup;
import android.widget.ListView;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.adapters.RecentsContentAdapter;
import co.uk.tusksolutions.tchat.android.constants.Constants;

public class RecentsFragment extends Fragment {

	public static ListView listView;
	public String TAG = "RecentsFragment";
	private View rootView;
	private static RecentsContentAdapter mAdapter;
	private IntentFilter filter;
	private Bundle instanceState;
	private static int shortAnimTime;
	private static View mLodingStatusView;
	private RecentsReceiver mRecentsReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = (View) inflater.inflate(R.layout.fragment_recents,
				container, false);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		instanceState = savedInstanceState;
		
		shortAnimTime = getResources().getInteger(
				android.R.integer.config_shortAnimTime);

		mLodingStatusView = getActivity().findViewById(R.id.loading_view);
		listView = (ListView) rootView.findViewById(R.id.list_view);
		listView.setVerticalScrollBarEnabled(false);
		listView.setHorizontalScrollBarEnabled(false);

		if (instanceState != null && mAdapter != null) {
			TChatApplication.CHAT_SECTION_QUERY_ACTION = instanceState
					.getInt("currentQueryAction");

			prepareListView();

		} else {

			prepareListView();
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		
		mRecentsReceiver = new RecentsReceiver();
		filter = new IntentFilter();
		filter.addAction(Constants.RECENTS_EMPTY);
		filter.addAction(Constants.RECENTS_UPDATED);
		filter.addAction(Constants.CHAT_MESSAGE_READY);
		getActivity().registerReceiver(mRecentsReceiver, filter);
		
		prepareListView();
	}

	@Override
	public void onPause(){
		super.onPause();
		if (mRecentsReceiver !=null) {
			getActivity().unregisterReceiver(mRecentsReceiver);
		}
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

	private static void prepareListView() {

		/**
		 * Load Recents from DB
		 */
		mAdapter = new RecentsContentAdapter(TChatApplication.getContext());

		if (mAdapter.getCount() == 0) {
			//showProgress(true);
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
	 * Broad cast fromUser that gets called When we receive new data form cloud
	 * TO_USER db
	 */
	private class RecentsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			/*
			 * Load friends fromUser db and reload the list view
			 */
			if (intent.getAction().equalsIgnoreCase(Constants.RECENTS_UPDATED)) {

				if (intent.getExtras() != null) {
					int inserts = intent.getExtras().getInt("inserts");
					if (inserts > mAdapter.getCount()) {
						prepareListView();
					}
				}

			} else if (intent.getAction().equalsIgnoreCase(
					Constants.RECENTS_EMPTY)) {
				showProgress(false);
				Log.i(TAG, "Recents Empty!");
			}else if (intent.getAction().equalsIgnoreCase(Constants.CHAT_MESSAGE_READY)){
				prepareListView();
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
}
