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
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.adapters.ChatMessagesAdapter;
import co.uk.tusksolutions.tchat.android.api.APICloudStorage;
import co.uk.tusksolutions.tchat.android.api.APIGetMessages;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.xmpp.XMPPChatMessageManager;

public class GroupChatActivity extends ActionBarActivity {
	private MediaPlayer mp;
	private TextView chatMessageEditText;
	private Button chatSendButton, emojiButton;
	private String roomName;
	static String roomJid;
	private static ChatMessagesAdapter mAdapter;
	private static View mLodingStatusView;
	private static int shortAnimTime;
	private static ListView listView;
	private GroupChatMessageReceiver mGroupChatMessageReceiver;
	private String currentJid;
	private static APIGetMessages mGetMessagesApi;

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
			if (getIntent().getExtras().containsKey("groupChatFromRoomBundle")) {
				/*
				 * Launched fromUser Notification...
				 */
				roomName = getIntent().getExtras()
						.getBundle("groupChatFromRoomBundle")
						.getString("roomName");
				roomJid = StringUtils.parseBareAddress(getIntent().getExtras()
						.getBundle("groupChatFromRoomBundle")
						.getString("roomJid"));

			} else if (getIntent().getExtras().containsKey(
					"groupChatToRoomBundle")) {
				/*
				 * Launched normally
				 */
				roomName = getIntent().getExtras()
						.getBundle("groupChatToRoomBundle")
						.getString("roomName");
				roomJid = getIntent().getExtras()
						.getBundle("groupChatToRoomBundle")
						.getString("roomJid");
			}
			getSupportActionBar().setTitle(roomName);
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

		mGroupChatMessageReceiver = new GroupChatMessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.MESSAGE_READY); // From sender
													// (me)
		filter.addAction(Constants.MESSAGE_RECEIVED); // From
														// Receiver
		// (buddy)
		registerReceiver(mGroupChatMessageReceiver, filter);

		/**
		 * Set chat visible enum TO_USER visible so when we get a chat packet,
		 * no status bar notification will be posted.
		 */
		if (TChatApplication.connection == null) {
			TChatApplication.reconnect();
		}
		TChatApplication
				.setChatActivityStatus(TChatApplication.CHAT_STATUS_ENUM.VISIBLE);

		TChatApplication.chatSessionBuddy = roomJid;

		prepareListView(roomJid, currentJid, 1, -1);
	}

	private static void prepareListView(String buddyJid, String currentJid,
			Integer action, long id) {

		/**
		 * Load Chat from DB
		 */
		mAdapter = new ChatMessagesAdapter(buddyJid, currentJid, action, id);
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
		if (mGroupChatMessageReceiver != null) {
			unregisterReceiver(mGroupChatMessageReceiver);
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
					XMPPChatMessageManager.sendComposing(roomJid, roomJid);
				}
			} else {
				if (chatSendButton.isEnabled() == true) {
					chatSendButton.setEnabled(false);
				}
				if (chatSendButton.isSelected() == false) {
					XMPPChatMessageManager.sendPaused(roomJid, roomJid);
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

				XMPPChatMessageManager.sendMessage(roomJid, roomName, message,
						1, "GROUP_CHAT");
				chatMessageEditText.setText("");
				chatSendButton.setEnabled(false);

				// Save to cloud
				Log.d("APICloudStorage", "Sender: "
						+ TChatApplication.getUserModel().getUsername()
						+ ", Receiver: " + StringUtils.parseName(roomJid)
						+ " Message: " + message);

				APICloudStorage cloudStorage = new APICloudStorage();
				cloudStorage.saveToCloud(TChatApplication.getUserModel()
						.getUsername(), StringUtils.parseName(roomJid),
						message, "none", 1, "GROUP_CHAT");
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

	/**
	 * Programatically register for broadcast & be notified when group chat
	 * message has been received, processed and inserted to the db so we can
	 * reload this view with the new message.
	 * 
	 */
	private class GroupChatMessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equalsIgnoreCase(Constants.MESSAGE_READY)) {
				prepareListView(roomJid, currentJid, 1,
						intent.getLongExtra("id", -1));
			} else if (intent.getAction().equalsIgnoreCase(
					Constants.MESSAGE_RECEIVED)) {
				prepareListView(roomJid, currentJid, 1,
						intent.getLongExtra("id", -1));
			}
		}

	}
}
