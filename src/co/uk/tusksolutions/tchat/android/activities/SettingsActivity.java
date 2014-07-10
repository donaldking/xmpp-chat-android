package co.uk.tusksolutions.tchat.android.activities;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.os.Bundle;
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
import co.uk.tusksolutions.tchat.android.models.UserModel;

public class SettingsActivity extends ActionBarActivity {

	Button mLogoutButton;
	LayoutInflater inflater;
	ViewGroup container;
	CheckBox mSoundNotificationCheckbox, mShowLastSeenOnlineCheckbox;

	ImageView mProfileAvatar;
	RobotoBoldTextView mFullNameTextView;
	RobotoLightTextView mUserNameTextView;
	UserModel mUserModel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		mUserModel = new UserModel();

		mProfileAvatar = (ImageView) findViewById(R.id.profile_image);

		mFullNameTextView = (RobotoBoldTextView) findViewById(R.id.profile_full_name);
		mUserNameTextView = (RobotoLightTextView) findViewById(R.id.profile_username);

		mSoundNotificationCheckbox = (CheckBox) findViewById(R.id.sound_notification_checkbox);
		mShowLastSeenOnlineCheckbox = (CheckBox) findViewById(R.id.show_last_seen_checkbox);

		mLogoutButton = (Button) findViewById(R.id.logout_button);
		mLogoutButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TChatApplication.tearDownAndLogout();
				finish();
			}
		});

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		this.setCurrentPreferences();
	}

	private void setCurrentPreferences() {

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
}
