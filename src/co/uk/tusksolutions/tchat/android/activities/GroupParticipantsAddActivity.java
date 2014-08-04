package co.uk.tusksolutions.tchat.android.activities;

import java.util.ArrayList;

import org.jivesoftware.smack.util.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.adapters.GroupFriendsSelectionAdapter;
import co.uk.tusksolutions.tchat.android.api.APIAddUserToGroup;
import co.uk.tusksolutions.tchat.android.enums.AddOrRemoveEnum;
import co.uk.tusksolutions.tchat.android.models.GroupsModel;
import co.uk.tusksolutions.tchat.android.models.RosterModel;
import co.uk.tusksolutions.tchat.android.tasks.AddPeopleToGroup;
import co.uk.tusksolutions.tchat.android.tasks.AddPeopleToGroup.OnAddPeopleMUCListener;

public class GroupParticipantsAddActivity extends ActionBarActivity implements
		TextWatcher, OnAddPeopleMUCListener {

	public EditText searchInput;
	public static ListView listView;
	public String TAG = "GroupParticipantsAddActivity";
	private static GroupFriendsSelectionAdapter mAdapter;
	ArrayList<RosterModel> totalSelectedModel, totalUnSelectedModel,
			existingUsersModel;
	public Bundle bundle;
	public String groupId;
	public JSONArray participants;
	private ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_friends_selection);
		actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("Add People");

		searchInput = (EditText) findViewById(R.id.friend_add_edittext);
		searchInput.clearFocus();
		searchInput.setFocusableInTouchMode(true);
		searchInput.addTextChangedListener(this);
		listView = (ListView) findViewById(R.id.list_view);
		listView.setVerticalScrollBarEnabled(false);
		listView.setHorizontalScrollBarEnabled(false);

		bundle = getIntent().getExtras();

		try {
			prepareListView();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private void prepareListView() throws JSONException {

		if (bundle != null) {
			groupId = bundle.getString("group_id");
			participants = TChatApplication.getGroupsModel().getParticipants(
					groupId);
			if (participants != null) {
				mAdapter = new GroupFriendsSelectionAdapter(
						TChatApplication.getContext(), participants,
						AddOrRemoveEnum.ADD_PEOPLE);
				listView.setAdapter(mAdapter);
			}
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		mAdapter.getFilter().filter(s);
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.group_participants_selection_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.submit_next:

			if (searchInputCleared()) {

				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						handleSelection();

					}
				}, 100);
			}

			break;
		default:
			break;
		}
		return true;
	}

	private boolean searchInputCleared() {
		searchInput.setText("");
		mAdapter.getFilter().filter(searchInput.getText().toString());

		if (searchInput.length() == 0) {
			return true;
		}
		return false;
	}

	public void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
	}

	protected void handleSelection() {
		totalSelectedModel = new ArrayList<RosterModel>();
		totalUnSelectedModel = new ArrayList<RosterModel>();

		for (RosterModel model : GroupFriendsSelectionAdapter.rosterModelCollection) {
			if (model.isSelected()) {
				totalSelectedModel.add(model);
			} else {
				totalUnSelectedModel.add(model);
			}
		}

		if (totalSelectedModel.size() >= 1) {
			hideKeyboard();
			
			new AddPeopleToGroup(GroupParticipantsAddActivity.this,
					StringUtils.parseName(groupId), groupId,
					totalSelectedModel, this).execute();
		} else {
			Log.d(TAG, "No selection made");
		}
	}

	@Override
	public void onAddPeopleMUCSuccess(ArrayList<RosterModel> addedFriendsList) {
		JSONObject participant;
		try {

			for (RosterModel rosterModel : totalSelectedModel) {
				participant = new JSONObject();
				participant.put("user_id", rosterModel.user);
				participants.put(participant);

				// Add this user to API
				APIAddUserToGroup addUserToGroupObject = new APIAddUserToGroup();
				addUserToGroupObject.doAddUserToGroup(
						StringUtils.parseName(groupId),
						StringUtils.parseName(rosterModel.user));
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		// Add participants to local db
		if (GroupsModel.updateGroupParticipants(groupId, participants)) {
			finish();
		}
	}

	@Override
	public void onAddPeopleMUCFailed() {
		// TODO Auto-generated method stub

	}

}
