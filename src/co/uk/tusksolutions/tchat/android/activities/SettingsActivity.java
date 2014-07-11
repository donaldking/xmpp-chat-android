package co.uk.tusksolutions.tchat.android.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import co.uk.tusksolutions.extensions.RobotoBoldTextView;
import co.uk.tusksolutions.extensions.RobotoLightTextView;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.fragments.ChangePresenceFragment;
import co.uk.tusksolutions.tchat.android.models.UserModel;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

public class SettingsActivity extends ActionBarActivity {

	private PresenceChangeReceiver mPresenceChangeReceiver;
	private IntentFilter filter;
	Button mLogoutButton, mChangePresenceButton;
	LayoutInflater inflater;
	ViewGroup container;
	CheckBox mSoundNotificationCheckbox, mShowLastSeenOnlineCheckbox;

	ImageView mProfileAvatar;
	RobotoBoldTextView mFullNameTextView, mPresenceStatusText;
	RobotoLightTextView mUserNameTextView;
	UserModel mUserModel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		mProfileAvatar = (ImageView) findViewById(R.id.profile_image);

		mFullNameTextView = (RobotoBoldTextView) findViewById(R.id.profile_full_name);
		mUserNameTextView = (RobotoLightTextView) findViewById(R.id.profile_username);

		mSoundNotificationCheckbox = (CheckBox) findViewById(R.id.sound_notification_checkbox);
		mShowLastSeenOnlineCheckbox = (CheckBox) findViewById(R.id.show_last_seen_checkbox);

		mChangePresenceButton = (Button) findViewById(R.id.change_presence_status_button);
		mChangePresenceButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Show presence change dialog fragment

				showPresenceDialog();
			}
		});

		mPresenceStatusText = (RobotoBoldTextView) findViewById(R.id.presence_status_text);
		mPresenceStatusText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showPresenceDialog();
			}
		});

		mLogoutButton = (Button) findViewById(R.id.logout_button);
		mLogoutButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TChatApplication.tearDownAndLogout();
				finish();
			}
		});

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public void onResume() {
		super.onResume();

		TChatApplication.presenceDialogHandler = new PresenceDialogFragmentDismissHandler();

		mPresenceChangeReceiver = new PresenceChangeReceiver();
		filter = new IntentFilter();
		filter.addAction(Constants.USER_PRESENCE_CHANGED);
		registerReceiver(mPresenceChangeReceiver, filter);

		prepareProfile();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mPresenceChangeReceiver != null) {
			unregisterReceiver(mPresenceChangeReceiver);
		}
	}

	private void showPresenceDialog() {
		FragmentManager fm = getSupportFragmentManager();
		ChangePresenceFragment changePresenceFragment = new ChangePresenceFragment();
		changePresenceFragment.show(fm, "presence_dialog");
	}

	private void prepareProfile() {

		mUserModel = new UserModel();
		this.setUserPresence();

		try {
			UrlImageViewHelper.setUrlDrawable(mProfileAvatar,
					Constants.PROXY_SERVER
							+ TChatApplication.getUserModel().getUsername()
							+ "/avatar/1288&return=png",
					R.drawable.mondobar_jewel_friends_on);
		} catch (Exception e) {
			e.printStackTrace();
		}

		mFullNameTextView.setText(mUserModel.getProfileName());
		mUserNameTextView.setText(mUserModel.getUsername());

		mSoundNotificationCheckbox.setChecked(true);
		mShowLastSeenOnlineCheckbox.setChecked(true);
	}

	public void setUserPresence() {
		String presence = mUserModel.getCurrentPresence();

		if (presence.equalsIgnoreCase("online")) {
			mPresenceStatusText.setTextColor(getResources().getColor(
					R.color.light_green));
			mPresenceStatusText.setText("ONLINE");

		} else if (presence.equalsIgnoreCase("invisible")) {
			mPresenceStatusText.setTextColor(getResources().getColor(
					R.color.silver));
			mPresenceStatusText.setText("INVISIBLE");

		} else if (presence.equalsIgnoreCase("offline")) {
			mPresenceStatusText.setTextColor(getResources().getColor(
					R.color.silver));
			mPresenceStatusText.setText("OFFLINE");
		}
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

	// Dialog handler
	@SuppressLint("HandlerLeak")
	public class PresenceDialogFragmentDismissHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			prepareProfile();
		}
	}

	// Broadcast receiver
	private class PresenceChangeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			/*
			 * Load friends fromUser db and reload the list view
			 */
			if (intent.getAction().equalsIgnoreCase(
					Constants.USER_PRESENCE_CHANGED)) {

				prepareProfile();
			}
		}
	}
}
