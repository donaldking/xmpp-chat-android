package co.uk.tusksolutions.tchat.android.fragments;

import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.R.id;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.activities.CreateChatRoomActivity;
import co.uk.tusksolutions.tchat.android.adapters.ChatroomsContentAdapter;
import co.uk.tusksolutions.tchat.android.adapters.GroupsContentAdapter;
import co.uk.tusksolutions.tchat.android.adapters.RosterContentAdapter;
import co.uk.tusksolutions.tchat.android.api.APIGetChatRooms;
import co.uk.tusksolutions.tchat.android.api.APIGetChatRooms.OnGetChatroomsCompleted;
import co.uk.tusksolutions.tchat.android.fragments.RosterFragment.RosterReceiver;
import co.uk.tusksolutions.tchat.android.listeners.XMPPPresenceListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
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

public class ChatRoomsFragment extends Fragment implements OnGetChatroomsCompleted {
	public static ListView listView;
	public String TAG = "ChatRoomFragment";
	private View rootView;
	private static ChatroomsContentAdapter mAdapter;
	private IntentFilter filter;

	private static int shortAnimTime;
	private static int lastViewedPosition;
	private static int topOffset;
	private static View mLodingStatusView;
	private RadioButton allchatroomButton, activechatroomButton,scheduledchatroomButton,createchatroomButton;
	private Bundle instanceState;
	
	private static int ALL_CHATROOMS_QUERY_ACTION = 1; // See adapter for notes
	private int ACTIVE_CHATROOMS_QUERY_ACTION = 2; // See adapter for notes
	private int SCHEDULED_CHATROOMS_QUERY_ACTION = 3;
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

		
		listView = (ListView) rootView.findViewById(R.id.list_view);
		listView.setVerticalScrollBarEnabled(false);
		listView.setHorizontalScrollBarEnabled(false);
		mLodingStatusView = getActivity()
				.findViewById(R.id.chatroom_loading_view);

		if (instanceState != null && mAdapter != null) {

			lastViewedPosition = instanceState.getInt("lastViewedPosition");
			topOffset = instanceState.getInt("topOffset");
		} else {
			lastViewedPosition = 0;
			topOffset = 0;
		}
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
		scheduledchatroomButton.setOnClickListener(new SegmentButtonOnClickListener());
		createchatroomButton=(RadioButton)rootView.findViewById(R.id.create_chatroom_button);
		createchatroomButton.setOnClickListener(new SegmentButtonOnClickListener());

	}
	
	@Override
	public void onResume() {
		super.onResume();
   
		APIGetChatRooms apiGetChatRooms=new APIGetChatRooms();
		apiGetChatRooms.getChatrooms(this);
		if (TChatApplication.CHATROOM_SECTION_QUERY_ACTION == ALL_CHATROOMS_QUERY_ACTION) {
			prepareListView(TChatApplication.CHATROOM_SECTION_QUERY_ACTION);
		}
		
	}

	@Override
	public void onPause() {
		super.onPause();

	}

	private static void prepareListView(final int queryInt) {

		/**
		 * Load Recents from DB
		 */
		TChatApplication.CHATROOM_SECTION_QUERY_ACTION=queryInt;
		mAdapter = new ChatroomsContentAdapter(TChatApplication.getContext(),queryInt);

		if (mAdapter.getCount() == 0) {
			if (TChatApplication.CHATROOM_SECTION_QUERY_ACTION == 2) {
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
				TChatApplication.CHATROOM_SECTION_QUERY_ACTION=ALL_CHATROOMS_QUERY_ACTION;
				prepareListView(TChatApplication.CHATROOM_SECTION_QUERY_ACTION);

				break;
			case R.id.active_chatroom_button:
				TChatApplication.CHATROOM_SECTION_QUERY_ACTION=ACTIVE_CHATROOMS_QUERY_ACTION;
				prepareListView(TChatApplication.CHATROOM_SECTION_QUERY_ACTION);
				break;
			case R.id.scheduled_chatroom_button:
				TChatApplication.CHATROOM_SECTION_QUERY_ACTION=SCHEDULED_CHATROOMS_QUERY_ACTION;
				prepareListView(TChatApplication.CHATROOM_SECTION_QUERY_ACTION);
				break;
			case R.id.create_chatroom_button:
				//To create a chat room
				
				Intent intent=new Intent(TChatApplication.getContext(),CreateChatRoomActivity.class);
				startActivity(intent);
			
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

	@Override
	public void OnGetChatRoomSuccess() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnGetChatRoomFailed() {
		// TODO Auto-generated method stub
		
	}
	
	
}
