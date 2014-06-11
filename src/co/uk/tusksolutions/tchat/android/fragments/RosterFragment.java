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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.adapters.RosterContentAdapter;

public class RosterFragment extends Fragment {

	public ListView listView;
	public String TAG;
	private View rootView;
	private RosterContentAdapter mAdapter;
	private IntentFilter filter;
	private RosterReceiver mRosterReceiver;
	private static View mLodingStatusView;
	private int shortAnimTime;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/**
		 * Load Friends from DB
		 */
		mAdapter = new RosterContentAdapter(TChatApplication.getContext(), "");
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

		this.prepareListView();
	}

	private void prepareListView() {
		listView.setAdapter(mAdapter);
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {

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
			showProgress(false);
			mAdapter = new RosterContentAdapter(context, getArguments()
					.getString("mediaTable"));
			prepareListView();
		}

	}
}
