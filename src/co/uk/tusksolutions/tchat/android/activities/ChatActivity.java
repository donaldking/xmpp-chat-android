package co.uk.tusksolutions.tchat.android.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.constants.Constants;

public class ChatActivity extends ActionBarActivity {

	TextView chatMessageEditText;
	Button chatSendButton, emojiButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		emojiButton = (Button) findViewById(R.id.emoji_button);
		emojiButton.setOnClickListener(new EmojiButtonOnClickListener());

		chatSendButton = (Button) findViewById(R.id.chat_send_button);
		chatSendButton.setOnClickListener(new ChatSendOnClickListener());

		chatMessageEditText = (TextView) findViewById(R.id.chat_message_edit_text);
		chatMessageEditText.addTextChangedListener(new ChatTextListener());

		if (getIntent().getExtras() != null) {
			String fromName = getIntent().getExtras()
					.getBundle("chatFromFriendBundle").getString("fromName");
			// String message = getIntent().getExtras()
			// .getBundle("chatMessageBundle").getString("message");

			getSupportActionBar().setTitle(fromName);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		/**
		 * Set chat visible enum to visible so when we get a chat packet, no
		 * status bar notification will be posted.
		 */
		TChatApplication
				.setChatActivityStatus(TChatApplication.CHAT_STATUS_ENUM.VISIBLE);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		/**
		 * Set chat visible enum to not visible so when we get a chat packet,
		 * status bar notification will be posted.
		 */
		TChatApplication
				.setChatActivityStatus(TChatApplication.CHAT_STATUS_ENUM.NOT_VISIBLE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		/**
		 * Set chat visible enum to not visible so when we get a chat packet,
		 * status bar notification will be posted.
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
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			if (s.toString().length() >= 1) {
				if (chatSendButton.isEnabled() == false) {
					// TODO Send composing stanza to friend.
					chatSendButton.setEnabled(true);
				}
			} else {
				if (chatSendButton.isEnabled() == true) {
					// TODO Send stopped composing stanza to friend.
					chatSendButton.setEnabled(false);
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
				chatMessageEditText.setText("");
				chatSendButton.setEnabled(false);
				// TODO Insert chat to db
				// TODO Reload chat db.
				// TODO Wait for packet call back to set it as delivered.
				Toast.makeText(TChatApplication.getContext(), "Message sent!",
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	/*
	 * Emoji button click listener
	 */
	class EmojiButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO change inputview for chatMessageEditText to emoji fragment
			Toast.makeText(TChatApplication.getContext(),
					"Emoji coming soon...", Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * Programatically register for broadcast to be notified when chat message
	 * has been received, processed and inserted to the db so we can reload this
	 * view with the new message.
	 * 
	 */
	class ChatMessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equalsIgnoreCase(
					Constants.CHAT_MESSAGE_READY)) {
				Toast.makeText(TChatApplication.getContext(),
						intent.getStringExtra("message"), Toast.LENGTH_LONG)
						.show();
			}

		}

	}

}
