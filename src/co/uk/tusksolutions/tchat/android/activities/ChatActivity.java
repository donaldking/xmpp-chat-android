package co.uk.tusksolutions.tchat.android.activities;

import org.jivesoftware.smack.util.StringUtils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import co.uk.tusksolutions.extensions.TimeAgo;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.adapters.ChatMessagesAdapter;
import co.uk.tusksolutions.tchat.android.api.APICloudStorage;
import co.uk.tusksolutions.tchat.android.api.APIGetLastOnlineTime;
import co.uk.tusksolutions.tchat.android.api.APIGetMessages;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.listeners.XMPPChatMessageListener;
import co.uk.tusksolutions.tchat.android.models.RosterModel;
import co.uk.tusksolutions.tchat.android.xmpp.XMPPChatMessageManager;

public class ChatActivity extends ActionBarActivity {
	private MediaPlayer mp;
	private TextView chatMessageEditText;
	private Button chatSendButton, emojiButton;
	private String buddyName;
	static String buddyJid;
	private static ChatMessagesAdapter mAdapter;
	private static View mLodingStatusView;
	private static int shortAnimTime;
	private static ListView listView;
	private ChatMessageReceiver mChatMessageReceiver;
	private String currentJid;
	private static APIGetMessages mGetMessagesApi;
	private RosterModel mRosterModel;
	public String lastSeen;
	public static String CHATSTATE = "ACTION_CHAT_STATE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		currentJid = TChatApplication.getCurrentJid();
		shortAnimTime = getResources().getInteger(
				android.R.integer.config_shortAnimTime);
		mLodingStatusView = this.findViewById(R.id.chat_loading_view);

		listView = (ListView) findViewById(R.id.chat_messages_list_view);
		listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
		listView.setStackFromBottom(true);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		emojiButton = (Button) findViewById(R.id.emoji_button);
		emojiButton.setOnClickListener(new EmojiButtonOnClickListener());

		chatSendButton = (Button) findViewById(R.id.chat_send_button);
		chatSendButton.setOnClickListener(new ChatSendOnClickListener());

		chatMessageEditText = (TextView) findViewById(R.id.chat_message_edit_text);
		chatMessageEditText.setFocusableInTouchMode(true);
		chatMessageEditText.addTextChangedListener(new ChatTextListener());

		if (getIntent().getExtras() != null) {
			if (getIntent().getExtras().containsKey("chatFromFriendBundle")) {
				/*
				 * Launched fromUser Notification...
				 */
				buddyName = getIntent().getExtras()
						.getBundle("chatFromFriendBundle")
						.getString("fromName");
				buddyJid = StringUtils.parseBareAddress(getIntent().getExtras()
						.getBundle("chatFromFriendBundle")
						.getString("buddyJid"));

			} else if (getIntent().getExtras().containsKey(
					"chatWithFriendBundle")) {
				/*
				 * Launched fromUser Roster...
				 */
				buddyName = getIntent().getExtras()
						.getBundle("chatWithFriendBundle")
						.getString("friendName");
				buddyJid = getIntent().getExtras()
						.getBundle("chatWithFriendBundle")
						.getString("buddyJid");
			}

			getSupportActionBar().setTitle(buddyName);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.chat_activity_menu, menu);
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();

		mRosterModel = new RosterModel();
		mChatMessageReceiver = new ChatMessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.CHAT_MESSAGE_READY); // From sender (me)
		filter.addAction(Constants.CHAT_MESSAGE_RECEIVED); // From Receiver
															// (buddy)

		filter.addAction(Constants.CHAT_MESSAGE_EMPTY); // No recent
														// conversation
		filter.addAction(XMPPChatMessageListener.ACTION_XMPP_CHAT_STATE_CHANGED); // For
																					// composing
																					// notification
		filter.addAction(Constants.LAST_ONLINE_TIME_STATE_CHANGED); // For
																	// Lastseen
																	// time
		filter.addAction(Constants.ROSTER_UPDATED); // To monitor buddy presence

		registerReceiver(mChatMessageReceiver, filter);

		/**
		 * Set chat visible enum TO_USER visible so when we get a chat packet,
		 * no status bar notification will be posted.
		 */
		if (TChatApplication.connection == null) {
			TChatApplication.reconnect();
		}
		TChatApplication
				.setChatActivityStatus(TChatApplication.CHAT_STATUS_ENUM.VISIBLE);

		TChatApplication.chatSessionBuddy = buddyJid;

		prepareListView(buddyJid, currentJid, 1, -1);

		// Get last online time for this buddy
		getLastOnlineTime();
	}

	private static void prepareListView(String buddyJid, String currentJid,
			Integer action, long id) {

		/**
		 * Load Chat from DB
		 */
		mAdapter = new ChatMessagesAdapter(buddyJid, currentJid, action, id);
		/*if (mAdapter.getCount() == 0) {
			// Sync with API
			// showProgress(true);
			listView.setVisibility(View.GONE);
			mGetMessagesApi = new APIGetMessages();
			mGetMessagesApi.getMessages(StringUtils.parseName(buddyJid),
					mAdapter.getCount(), 25);

		} else {
			showProgress(false);
			listView.setAdapter(mAdapter);
			if (listView.getVisibility() != View.VISIBLE) {
				listView.setVisibility(View.VISIBLE);
			}

			scrollToBottom();
		}*/
		mGetMessagesApi = new APIGetMessages();
		mGetMessagesApi.getMessages(StringUtils.parseName(buddyJid),
				mAdapter.getCount(), 25);
		listView.setAdapter(mAdapter);
		scrollToBottom();
	}

	public static void scrollToBottom() {
		Log.d("ChatActivity", "ScrollToBottom");
		listView.setSelection(listView.getCount() - 1);
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

			// mLodingStatusView.setVisibility(View.VISIBLE);
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
	public void onPause() {
		super.onPause();
		if (mChatMessageReceiver != null) {
			unregisterReceiver(mChatMessageReceiver);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		/**
		 * Set chat visible enum TO_USER not visible so when we get a chat
		 * packet, status bar notification will be posted.
		 */
		TChatApplication
				.setChatActivityStatus(TChatApplication.CHAT_STATUS_ENUM.NOT_VISIBLE);
		TChatApplication.chatSessionBuddy = null;
	}

	@Override
	protected void onStop() {
		super.onStop();
		/**
		 * Set chat visible enum TO_USER not visible so when we get a chat
		 * packet, status bar notification will be posted.
		 */
		TChatApplication
				.setChatActivityStatus(TChatApplication.CHAT_STATUS_ENUM.NOT_VISIBLE);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
		case android.R.id.home:
			doGoBack();
			break;

		default:
			break;
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		doGoBack();
	}

	private void doGoBack() {

		if (this.isTaskRoot()) {
			/*
			 * Start MainActivity as ChatActiviy is the last in the stack.
			 * Otherwise, simply finish ChatActivity
			 */
			Intent i = new Intent(this, MainActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			finish();
		} else {
			finish();
		}
	}

	public void showLastOnlineTime() {

		String lastOnlineTime = mRosterModel.getLastSeen(buddyJid);

		if (lastOnlineTime != null) {
			lastSeen = TimeAgo.getTimeAgo(Long.parseLong(lastOnlineTime), this);
			getSupportActionBar().setSubtitle(
					Html.fromHtml("<font color='#FFFFFF'>last seen " + lastSeen
							+ "</font>"));
		}
	}

	// Get last online time for this buddy

	private void getLastOnlineTime() {

		if (mRosterModel.isBuddyOnline(buddyJid) == true) {
			lastSeen = "online";
			getSupportActionBar().setSubtitle(
					Html.fromHtml("<font color='#FFFFFF'> " + lastSeen
							+ "</font>"));
			return;
		}
		showLastOnlineTime();
		APIGetLastOnlineTime getLastOnlineTimeObject = new APIGetLastOnlineTime();
		getLastOnlineTimeObject.doGetLastOnlineTime(buddyJid);
	}

	/*
	 * TextChange watcher
	 */

	class ChatTextListener implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			if (s.toString().length() >= 1) {
				if (chatSendButton.isEnabled() == false) {
					chatSendButton.setEnabled(true);
				}
				if (chatSendButton.isEnabled()) {
					XMPPChatMessageManager.sendComposing(buddyJid, buddyJid);
				}
			} else {
				if (chatSendButton.isEnabled() == true) {
					chatSendButton.setEnabled(false);
				}
				if (chatSendButton.isSelected() == false) {
					XMPPChatMessageManager.sendPaused(buddyJid, buddyJid);
				}
			}
		}
	}

	/*
	 * Chat message send listener
	 */
	class ChatSendOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (chatMessageEditText.getText().toString().length() >= 1) {
				String message = chatMessageEditText.getText().toString();

				mp = MediaPlayer.create(v.getContext(), R.raw.received_message);
				mp.setVolume(1, 1);
				mp.start();

				XMPPChatMessageManager
						.sendMessage(buddyJid, buddyName, message);
				chatMessageEditText.setText("");
				chatSendButton.setEnabled(false);

				// Save to cloud
				APICloudStorage cloudStorage = new APICloudStorage();
				cloudStorage.saveToCloud(TChatApplication.getUserModel()
						.getUsername(), StringUtils.parseName(buddyJid),
						message, "none");
			}
		}

	}

	/*
	 * Emoji button click listener
	 */
	class EmojiButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO change inputview for chatMessageEditText TO_USER emoji
			// fragment
			Toast.makeText(TChatApplication.getContext(),
					"Emoji coming soon...", Toast.LENGTH_SHORT).show();
		}

	}

	public void displayComposing(String chatStateStr, String ComposingBuddyJid) {
		Log.e("Chatstate ", "chat state " + chatStateStr + " composing friend "
				+ ComposingBuddyJid);

		if (chatStateStr.equalsIgnoreCase("composing")
				&& buddyJid.equalsIgnoreCase(ComposingBuddyJid)) {

			getSupportActionBar()
					.setSubtitle(
							Html.fromHtml("<font color='#FFFFFF'> is typing...</font>"));

		} else if (chatStateStr.equalsIgnoreCase("paused")
				&& buddyJid.equalsIgnoreCase(ComposingBuddyJid)) {

			if (lastSeen != null) {
				getSupportActionBar().setSubtitle(
						Html.fromHtml("<font color='#FFFFFF'>" + lastSeen
								+ "</font>"));
			}

		} else {
			if (lastSeen != null) {
				getSupportActionBar().setSubtitle(
						Html.fromHtml("<font color='#FFFFFF'>" + lastSeen
								+ "</font>"));
			}
		}
	}

	/**
	 * Programatically register for broadcast TO_USER be notified when chat
	 * message has been received, processed and inserted TO_USER the db so we
	 * can reload this view with the new message.
	 * 
	 */
	private class ChatMessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equalsIgnoreCase(
					Constants.CHAT_MESSAGE_READY)) {
				prepareListView(buddyJid, currentJid, 1,
						intent.getLongExtra("id", -1));
			} else if (intent.getAction().equalsIgnoreCase(
					Constants.CHAT_MESSAGE_EMPTY)) {
				showProgress(false);
			} else if (intent.getAction().equalsIgnoreCase(
					XMPPChatMessageListener.ACTION_XMPP_CHAT_STATE_CHANGED)) {

				String chatStateStr = intent
						.getStringExtra(XMPPChatMessageListener.EXTRA_CHAT_STATE);

				String chatStateUserJid = intent
						.getStringExtra(XMPPChatMessageListener.EXTRA_CHAT_BUDDY_NAME);

				if (chatStateStr != null && chatStateStr.length() > 0) {

					displayComposing(chatStateStr, chatStateUserJid);
				}
			} else if (intent.getAction().equalsIgnoreCase(
					Constants.LAST_ONLINE_TIME_STATE_CHANGED)) {
				showLastOnlineTime();
			} else if (intent.getAction().equalsIgnoreCase(
					Constants.ROSTER_UPDATED)) {
				getLastOnlineTime();
			}
		}

	}
}
