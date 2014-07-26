package co.uk.tusksolutions.tchat.android.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;

import co.uk.tusksolutions.tchat.android.adapters.GroupsContentAdapter;
import co.uk.tusksolutions.tchat.android.api.APIGetGroups;
import co.uk.tusksolutions.tchat.android.constants.Constants;

public class GroupsFragment extends Fragment {

	public static ListView listView;
	public String TAG = "GroupsFragments";
	private View rootView;
	private static GroupsContentAdapter mAdapter;

	private static int shortAnimTime;
	private static View mLodingStatusView;

	private APIGetGroups groupsApi;
	private Bundle instanceState;
	private static int lastViewedPosition;
	private static int topOffset;
  private NewGroupreceiver groupreceiver;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		/**
		 * Load Groups fromUser DB
		 */
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

		mLodingStatusView = getActivity().findViewById(
				R.id.recents_loading_view);
		listView = (ListView) rootView.findViewById(R.id.list_view);
		listView.setVerticalScrollBarEnabled(false);
		listView.setHorizontalScrollBarEnabled(false);

		showProgress(true);

		/*
		 * Load recents from API recentsApi = new APIRecents();
		 * recentsApi.getRecents();
		 */

		// Load groups from API
		groupsApi = new APIGetGroups();
		groupsApi.getGroups();

		if (instanceState != null && mAdapter != null) {

			lastViewedPosition = instanceState.getInt("lastViewedPosition");
			topOffset = instanceState.getInt("topOffset");
		} else {
			lastViewedPosition = 0;
			topOffset = 0;
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		
		groupreceiver = new NewGroupreceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.GROUPS_UPDATED); // For Update group
		
		getActivity().registerReceiver(groupreceiver, filter);

		
	}

	@Override
	public void onPause() {
		super.onPause();

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
		mAdapter = new GroupsContentAdapter(TChatApplication.getContext());

		if (mAdapter.getCount() == 0) {
			listView.setVisibility(View.GONE);
		} else {
			showProgress(false);
			listView.setAdapter(mAdapter);
			if (listView.getVisibility() != View.VISIBLE) {
				listView.setVisibility(View.VISIBLE);
			}

			listView.setSelectionFromTop(lastViewedPosition, topOffset);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		setHasOptionsMenu(true);
	}

	@SuppressLint("NewApi")
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getActivity().invalidateOptionsMenu();
		MenuItem filter = menu.findItem(R.id.action_settings);
		MenuItem filter1 = menu.findItem(R.id.action_chat_one);
		filter.setVisible(false);
		filter1.setVisible(false);
	}
	
	private class NewGroupreceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equalsIgnoreCase(Constants.GROUPS_UPDATED)) {
				groupsApi = new APIGetGroups();
				groupsApi.getGroups();
			prepareListView();
		}
		}
	}

}
