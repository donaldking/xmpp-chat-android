package co.uk.tusksolutions.tchat.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;

public class BootStrapActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_boot_strap);

		if (TChatApplication.getUserModel().getUsername() == null) {
			/*
			 * Show LoginActivity
			 */
			startActivity(new Intent(this, LoginActivity.class));

		} else {
			/*
			 * Show MainActivity
			 */
			startActivity(new Intent(this, MainActivity.class));
		}
		finish();
	}
}
