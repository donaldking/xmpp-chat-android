package co.uk.tusksolutions.tchat.android.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jivesoftware.smack.util.StringUtils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
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
import co.uk.tusksolutions.tchat.android.adapters.GroupChatMessagesAdapter;
import co.uk.tusksolutions.tchat.android.api.APIChatDownloadAndShare;
import co.uk.tusksolutions.tchat.android.api.APICloudStorage;
import co.uk.tusksolutions.tchat.android.api.APIGetMessages;
import co.uk.tusksolutions.tchat.android.api.APIPostFile;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.models.ChatMessagesModel;
import co.uk.tusksolutions.tchat.android.models.GroupsModel;
import co.uk.tusksolutions.tchat.android.xmpp.XMPPChatMessageManager;

public class GroupChatActivity extends ActionBarActivity {
	private MediaPlayer mp;
	private TextView chatMessageEditText;
	private Button chatSendButton, emojiButton;
	private String roomName;
	static String roomJid;
	private static GroupChatMessagesAdapter mAdapter;
	private static View mLodingStatusView;
	private static int shortAnimTime;
	private static ListView listView;
	private GroupChatMessageReceiver mGroupChatMessageReceiver;
	private String currentJid;
	private static APIGetMessages mGetMessagesApi;
	private static final int SELECT_FILE = 1000;
	public String lastSeen;
	public static String CHATSTATE = "ACTION_CHAT_STATE";
	public static String mid;
	private static View mFileUploadStatusView;
	public String group_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		if (Build.VERSION.SDK_INT >= 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();

			StrictMode.setThreadPolicy(policy);
		}

		currentJid = TChatApplication.getCurrentJid();
		shortAnimTime = getResources().getInteger(
				android.R.integer.config_shortAnimTime);
		mLodingStatusView = this.findViewById(R.id.chat_loading_view);

		mFileUploadStatusView = this
				.findViewById(R.id.chat_file_upload_progress);

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

				group_id = getIntent().getExtras()
						.getBundle("groupChatFromRoomBundle")
						.getString("roomJid");

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

				group_id = getIntent().getExtras()
						.getBundle("groupChatToRoomBundle")
						.getString("roomJid");
			}
			getSupportActionBar().setTitle(roomName);
		}

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
		filter.addAction(Constants.BANNED_FROM_ROOM); // In case we are banned
														// from this room

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

		mGetMessagesApi = new APIGetMessages();
		mGetMessagesApi.getMessages(StringUtils.parseName(roomJid),
				mAdapter.getCount(), 25, "group");
	}

	private static void prepareListView(String buddyJid, String currentJid,
			Integer action, long id) {

		/**
		 * Load Chat from DB
		 */
		mAdapter = new GroupChatMessagesAdapter(buddyJid, currentJid, action,
				id);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();

		if (GroupsModel.isAdmin(group_id, TChatApplication.getCurrentJid())) {
			inflater.inflate(R.menu.group_chat_activity_admin_menu, menu);
		} else {
			inflater.inflate(R.menu.group_chat_activity_menu, menu);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		Intent intent = new Intent(Intent.ACTION_PICK);
		switch (menuItem.getItemId()) {
		case android.R.id.home:
			doGoBack();
			break;
		case R.id.group_photo_menu:

			intent.setType("image/*");
			startActivityForResult(
					Intent.createChooser(intent, "Pick a picture"), SELECT_FILE);
			break;
		/*
		 * case R.id.group_video_menu: intent.setType("video/*");
		 * startActivityForResult( Intent.createChooser(intent, "Pick a Video"),
		 * SELECT_FILE); break;
		 */
		case R.id.group_add_people:
			Intent addPeopleIntent = new Intent(TChatApplication.getContext(),
					GroupParticipantsAddActivity.class); // Change to people
															// selction
			Bundle addPeopleBundle = new Bundle();
			addPeopleBundle.putString("group_id", group_id);
			addPeopleIntent.putExtras(addPeopleBundle);
			startActivity(addPeopleIntent);

			break;
		case R.id.group_remove_people:
			Intent removePeopleIntent = new Intent(
					TChatApplication.getContext(),
					GroupParticipantsRemoveActivity.class);
			Bundle removePeopleBundle = new Bundle();
			removePeopleBundle.putString("group_id", group_id);
			removePeopleIntent.putExtras(removePeopleBundle);
			startActivity(removePeopleIntent);

			break;
		case R.id.group_download_chat_history:

			APIChatDownloadAndShare chatDownload = new APIChatDownloadAndShare();
			chatDownload.doChatDownloadAndShare(
					GroupChatActivity.this,
					TChatApplication.getCurrentJid().replace(
							"@" + Constants.CURRENT_SERVER, ""), roomJid
							.replace("@conference." + Constants.CURRENT_SERVER,
									""), false);

			break;
		case R.id.group_share_chat_history:
			APIChatDownloadAndShare chatShare = new APIChatDownloadAndShare();
			chatShare.doChatDownloadAndShare(
					GroupChatActivity.this,
					TChatApplication.getCurrentJid().replace(
							"@" + Constants.CURRENT_SERVER, ""), roomJid
							.replace("@conference." + Constants.CURRENT_SERVER,
									""), true);

			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		mid = TChatApplication.getMid();

		if (requestCode == SELECT_FILE) {
			if (resultCode == Activity.RESULT_OK) {
				Toast.makeText(GroupChatActivity.this,
						"Sending Image please wait", Toast.LENGTH_SHORT).show();
				if (data != null) {

					Uri imagepath = data.getData();

					Log.e("TAG", "scheme " + imagepath.getScheme());
					if (imagepath.getScheme().contains("content")) {

						try {
							showProgressUpload(true);
							String name = "Temp_" + System.currentTimeMillis()
									+ ".jpg";
							File f = new File(TChatApplication.getContext()
									.getFilesDir() + "/" + name);
							saveImageToAppDir(imagepath, f.getAbsolutePath());
							if (f.exists()) {

								String selectedFile = f.getAbsolutePath();

								// showProgressUpload(true);
								saveToDB(roomJid, selectedFile, 0,
										"FileTransfer");
								APIPostFile apiPostFile = new APIPostFile();
								apiPostFile.doPostFile(currentJid, roomJid,
										selectedFile, roomName,
										GroupChatActivity.this, mid);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}

					} else {
						showProgressUpload(true);
						String selectedFile = getRealPathFromURI(data.getData());
						// showProgressUpload(true);
						saveToDB(roomJid, selectedFile, 0, "FileTransfer");
						APIPostFile apiPostFile = new APIPostFile();
						apiPostFile.doPostFile(currentJid, roomJid,
								selectedFile, roomName, GroupChatActivity.this,
								mid);
					}
				}

			}

			super.onActivityResult(requestCode, resultCode, data);
		}
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
				mid = TChatApplication.getMid();
				String message = chatMessageEditText.getText().toString();

				try {
					PlaySound(true);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				XMPPChatMessageManager.sendMessage(roomJid, roomName, message,
						1, "GROUP_CHAT", mid);
				Log.e("Groupchat activity ","RoomName "+roomName);
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
						message, mid, 1, "GROUP_CHAT");
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
					"Use emoji on your keyboard", Toast.LENGTH_SHORT).show();
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
				try {
					PlaySound(false);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				prepareListView(roomJid, currentJid, 1,
						intent.getLongExtra("id", -1));
			} else if (intent.getAction().equalsIgnoreCase(
					Constants.BANNED_FROM_ROOM)) {
				if (roomJid.equalsIgnoreCase(intent
						.getStringExtra("bannedRoomJid"))) {
					Log.d("BANNED", "You are banned from this room");
					Toast.makeText(context,
							"You have been kicked from this room",
							Toast.LENGTH_LONG).show();
					finish();
				}
			}
		}

	}

	private String getRealPathFromURI(Uri contentUri) {
		// can post image
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = null;

		try {
			cursor = TChatApplication.getContext().getContentResolver()
					.query(contentUri, proj, null, null, null);

			cursor.moveToFirst();
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			return cursor.getString(column_index);
		} catch (Exception ex) {
			return "";
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public void saveImageToAppDir(Uri imageUri, String imagepath)
			throws IOException {
		OutputStream output;

		InputStream input = getContentResolver().openInputStream(imageUri);
		try {

			output = new FileOutputStream(imagepath);
			try {
				byte[] buffer = new byte[2048];
				int bytesRead = 0;
				while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
					output.write(buffer, 0, bytesRead);
				}
			} finally {
				output.close();
			}

		} finally {
			input.close();
		}

	}

	public void saveToDB(String to, String message, int isGroupMessage,
			String messageType) {
		try {

			ChatMessagesModel mChatMessageModel = new ChatMessagesModel();
			mChatMessageModel.saveMessageToDB(to,
					TChatApplication.getCurrentJid(), roomJid, message,
					Constants.XMPP_RESOURCE, isGroupMessage, messageType,
					System.currentTimeMillis(), 1, mid);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public static void showProgressUpload(final boolean show) {

		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs TO_USER
		// fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {

			// mLodingStatusView.setVisibility(View.VISIBLE);
			mFileUploadStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mFileUploadStatusView
									.setVisibility(show ? View.VISIBLE
											: View.GONE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.*/
			mFileUploadStatusView
					.setVisibility(show ? View.VISIBLE : View.GONE);
		}
	}

	public void PlaySound(Boolean sent) throws Exception {
		if (sent) {
			try {
				mp = MediaPlayer.create(TChatApplication.getContext(),
						R.raw.send_message);
				mp.setVolume(1, 1);
				mp.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				mp = MediaPlayer.create(TChatApplication.getContext(),
						R.raw.received_message);
				mp.setVolume(1, 1);
				mp.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
