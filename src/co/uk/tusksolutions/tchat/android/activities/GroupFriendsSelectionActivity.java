package co.uk.tusksolutions.tchat.android.activities;

import java.util.ArrayList;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.muc.MultiUserChat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import co.uk.tusksolutions.extensions.RobotoBoldTextView;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.adapters.GroupFriendsAdapter;
import co.uk.tusksolutions.tchat.android.models.GroupItemsModel;
import co.uk.tusksolutions.tchat.android.xmpp.XMPPConnectionManager;

public class GroupFriendsSelectionActivity extends ActionBarActivity implements TextWatcher {

	
	private XMPPConnection xmppConnection;
	
	
	public EditText searchView;
	public static ListView listView;
	public String TAG = "RosterFragment";
	private static GroupFriendsAdapter mAdapter;
	
	ArrayAdapter<String> adapter;

	private static View mLodingStatusView;
	private static int shortAnimTime;

	private GroupItemsModel mModel;

	static RobotoBoldTextView selected_user;
	public static ArrayList<GroupItemsModel> rosterModelCollection;
	static StringBuilder builder;
	public static ArrayList<String> users_selected_array = new ArrayList<String>();
	ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_chat_activity);
		actionBar = getSupportActionBar();

		actionBar.setHomeButtonEnabled(true);

		actionBar.setDisplayHomeAsUpEnabled(true);

		mModel = new GroupItemsModel();

		rosterModelCollection = new ArrayList<GroupItemsModel>();
		
		
		
		listView = (ListView) findViewById(R.id.group_list_view);
		listView.setItemsCanFocus(false);
		listView.setFastScrollEnabled(true);

		selected_user = (RobotoBoldTextView) findViewById(R.id.selected_user_group);
		actionBar.setTitle("New Message");
	
		searchView = (EditText) findViewById(R.id.friend_add_edittext);
		searchView.clearFocus();
		searchView.setFocusableInTouchMode(true);
		searchView.addTextChangedListener(this);
		builder = new StringBuilder();
		shortAnimTime = getResources().getInteger(
				android.R.integer.config_shortAnimTime);

		mLodingStatusView = (View) findViewById(R.id.roster_loading_view);

		listView.setVerticalScrollBarEnabled(false);
		listView.setHorizontalScrollBarEnabled(false);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		rosterModelCollection = mModel.queryAllFriends();
		
		 

		mAdapter = new GroupFriendsAdapter(getApplicationContext(),
				rosterModelCollection);
		listView.setAdapter(mAdapter);
		
		
		/*adapter=new ArrayAdapter<String>(TChatApplication.getContext(), R.layout.group_chat_friends_row,R.id.roster_name,products);
		listView.setAdapter(adapter);*/
		
		

		scrollToTop();
		users_selected_array = new ArrayList<String>();

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				if (!selected_user.getText().toString()
						.contains(rosterModelCollection.get(position).name)) {
					builder.append(rosterModelCollection.get(position).name)
							.append(", ");
					users_selected_array.add(rosterModelCollection
							.get(position).name);
					setNametoTextView();
				} else {
					users_selected_array.remove(rosterModelCollection
							.get(position).name);
					setNametoTextView();
				}

			}
		});
		
	
	}

	protected void setNametoTextView() {
		// TODO Auto-generated method stub
		builder = new StringBuilder();
		for (String s : users_selected_array) {

			builder.append(s).append(", ");

		}

		selected_user.setText(builder.toString());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.group_friends_selection_menu, menu);
		return true;
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		// users_selected_array = new ArrayList<String>();
	}

	public static void scrollToTop() {
		Log.d("ChatActivity", "ScrollToBottom");
		listView.setSelection(0);
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
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
	/*	if (s.length() > 0) {

			performSearch(s);
		} else {
			rosterModelCollection = mModel.queryAllFriends();
			mAdapter = new GroupFriendsAdapter(this, rosterModelCollection);
			listView.setAdapter(mAdapter);
			scrollToTop();
		}*/
		//GroupFriendsSelectionActivity.this.adapter.getFilter().filter(s);
		GroupFriendsSelectionActivity.this.mAdapter.getFilter().filter(s);
	}

	public void performSearch(CharSequence s) {
		rosterModelCollection = mModel.querySearch(s.toString());
		mAdapter = new GroupFriendsAdapter(this, rosterModelCollection);
		listView.setAdapter(mAdapter);
		Log.d("TCHAT", "result Size " + mAdapter.getCount());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.submit_next:
			Toast.makeText(GroupFriendsSelectionActivity.this,
					"implementing group chat...", Toast.LENGTH_SHORT).show();
			
			
			TChatApplication.getUserModel().getUsername();
			
			String roomName=TChatApplication.getUserModel().getUsername()+"_"+System.currentTimeMillis();
			String roomJID=roomName+"@conference."+co.uk.tusksolutions.tchat.android.constants.Constants.STAGING_SERVER;
			String  nickname=TChatApplication.getUserModel().getUsername();
			
		
			try {
				createRoom(roomName, roomJID, nickname,"");
			} catch (XMPPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

	}

	public static void showSelectedItems() {
		final StringBuffer sb = new StringBuffer("To: ");

		// Get an array that tells us for each position whether the item is
		// checked or not
		// --
		final SparseBooleanArray checkedItems = listView
				.getCheckedItemPositions();

		if (checkedItems == null) {

			selected_user.setText("");
			Toast.makeText(TChatApplication.getContext(),
					"Not Selected Any friend", Toast.LENGTH_LONG).show();
			return;
		}

		// For each element in the status array
		// --
		boolean isFirstSelected = true;
		final int checkedItemsCount = checkedItems.size();
		for (int i = 0; i < checkedItemsCount; ++i) {
			// This tells us the item position we are looking at
			// --
			final int position = checkedItems.keyAt(i);

			// This tells us the item status at the above position
			// --
			final boolean isChecked = checkedItems.valueAt(i);

			if (isChecked) {
				if (!isFirstSelected) {
					sb.append(", ");

				}
				if(selected_user.getText().toString().contains(rosterModelCollection.get(position).name))
				{
					//CheckableRelativeLayout layout=new CheckableRelativeLayout(context, attrs)
				  Log.e("Checked Item", "Check item "+checkedItems.keyAt(i));
				}
				sb.append(rosterModelCollection.get(position).name);
				// if(!users_selected_array.contains(sb))
				// users_selected_array.add(sb.toString());

				isFirstSelected = false;
			}

		}

	}
	
	private MultiUserChat createRoom(String roomName, String roomJID, String nickname, String password) throws XMPPException {

		MultiUserChat multiUserChat = null;

		// final String subjectInviteStr = getRoomString(number, name);

		Log.i("Creating room [%s]", roomJID);
    
		// See issue 136
		try {
			if(TChatApplication.connection!=null)
				
			{
				
				multiUserChat = new MultiUserChat(TChatApplication.connection, roomJID);
			}
			else
			{
				TChatApplication.connection=TChatApplication.createNewConnection();
				Log.e("connection closed ", "connection "+TChatApplication.connection);
				multiUserChat = new MultiUserChat(TChatApplication.connection, roomJID);
		
				
			}
				
			
			boolean service=multiUserChat.isServiceEnabled(TChatApplication.connection, "zoepraise@uat.yookoschat.com");
			Log.e("TAG","service "+service);
			
			
			
		} catch (Exception e) {
			
		}

		try {
			multiUserChat.create(nickname);
			
			//multiUserChat.join(nickname);
			
		} catch (Exception e) {
			Log.e("MUC create", "MUC creation failed: ");
			throw new XMPPException("MUC creation failed for " + nickname + ": " + e.getLocalizedMessage(), e);
		}

		try {
			// We send an empty configuration to the server. For some reason the
			// server doesn't accept or process our
			// completed form, so we just send an empty one. The server defaults
			// will be used which are fine.
			multiUserChat.sendConfigurationForm(new Form(Form.TYPE_SUBMIT));

			multiUserChat.changeSubject(roomName);

		} catch (XMPPException e1) {
			Log.d(e1.toString(), "Unable to send conference room configuration form.");
			// then we also should not send an invite as the room will be locked
			throw e1;
		}

		// Sleep few seconds between creation and invite new user
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			
		}

		/*
		 * if (info.isPasswordProtected()) { multiUserChat.join(nickname,
		 * password, discussionHistory, JOIN_TIMEOUT); } else {
		 * multiUserChat.join(nickname, null, discussionHistory, JOIN_TIMEOUT);
		 * }
		 */

		multiUserChat.join(nickname, null, null, 1000);
		// IMPORTANT you should join before registerRoom
		//registerRoom(multiUserChat, roomJID, roomName, password);

		return multiUserChat;
	}


}
