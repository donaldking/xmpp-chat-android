package co.uk.tusksolutions.tchat.android.fragments;

import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.adapters.RosterContentAdapter;
import co.uk.tusksolutions.tchat.android.fragments.RosterFragment.RosterReceiver;
import co.uk.tusksolutions.tchat.android.listeners.XMPPPresenceListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RadioButton;

public class ChatRoomsFragment extends Fragment {
	public static ListView listView;
	public String TAG = "ChatRoomFragment";
	private View rootView;

	private IntentFilter filter;

	private static View mLodingStatusView;
	private static int shortAnimTime;
	private static int ALL_CHATROOM_QUERY_ACTION = 1; // See adapter for notes
	private int ACTIVE_CHATROOM_QUERY_ACTION = 2; // See adapter for notes
	private RadioButton allchatroomButton, activechatroomButton,
			scheduledchatroomButton;
	private Bundle instanceState;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 rootView = (View) inflater.inflate(R.layout.fragment_chatrooms,
				container, false);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		instanceState = savedInstanceState;

		shortAnimTime = getResources().getInteger(
				android.R.integer.config_shortAnimTime);

		mLodingStatusView = getActivity()
				.findViewById(R.id.roster_loading_view);
		listView = (ListView) rootView.findViewById(R.id.list_view);
		listView.setVerticalScrollBarEnabled(false);
		listView.setHorizontalScrollBarEnabled(false);

		allchatroomButton = (RadioButton) rootView
				.findViewById(R.id.all_chatroom_button);
		allchatroomButton
				.setOnClickListener(new SegmentButtonOnClickListener());
		activechatroomButton = (RadioButton) rootView
				.findViewById(R.id.active_chatroom_button);
		activechatroomButton
				.setOnClickListener(new SegmentButtonOnClickListener());
		scheduledchatroomButton = (RadioButton) rootView
				.findViewById(R.id.scheduled_chatroom_button);

	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		setHasOptionsMenu(true);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub

		MenuItem filter = menu.findItem(R.id.action_settings);
		MenuItem filter1 = menu.findItem(R.id.action_chat_one);
		filter.setVisible(false);
		filter1.setVisible(false);
	}

	/*
	 * Segment button onClick listener
	 */
	private class SegmentButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.all_chatroom_button:

				break;
			case R.id.active_chatroom_button:

				break;
			case R.id.scheduled_chatroom_button:
				
				break;
			case R.id.create_chatroom_button:
				//To create a chat room
				break;

			default:
				break;
			}
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

}
