package co.tusksolutions.tchat.android.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.constants.Constants;

public class ChatActivity extends ActionBarActivity {

	TextView senderMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		senderMessage = (TextView) findViewById(R.id.sender_message);
		/*
		 * Surely, Intent extras must be passed to this activity for us to pull
		 * the chat participants from db.
		 */
		if (getIntent().getExtras() != null) {
			String fromName = getIntent().getExtras()
					.getBundle("chatMessageBundle").getString("fromName");
			String message = getIntent().getExtras()
					.getBundle("chatMessageBundle").getString("message");

			getSupportActionBar().setTitle(fromName);
			senderMessage.setText(message);
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
			finish();
			break;

		default:
			break;
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

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
