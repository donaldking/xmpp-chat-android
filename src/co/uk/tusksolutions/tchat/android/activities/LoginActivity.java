package co.uk.tusksolutions.tchat.android.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.constants.Constants;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {

	private Context context;
	// Values for username and password at the time of the login attempt.
	private String mUsername;
	private String mPassword;

	// UI references.
	private EditText mUsernameView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	// Input manager reference.
	private InputMethodManager imm;
	private LoginReceiver mLoginReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = TChatApplication.getContext();

		setContentView(R.layout.activity_login);

		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		// Set up the login form.
		mUsernameView = (EditText) findViewById(R.id.username);
		mUsernameView.setText(mUsername);
		mUsernameView.setText("donaldking");

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.setText("default");

		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
	}

	@Override
	public void onResume() {
		super.onResume();

		mLoginReceiver = new LoginReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.LOGIN_SUCCESSFUL);
		filter.addAction(Constants.LOGIN_UNSUCCESSFUL);
		registerReceiver(mLoginReceiver, filter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * Attempt to login
	 */
	public void attemptLogin() {

		// Reset errors.
		mUsernameView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mUsername = mUsernameView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mUsername)) {
			mUsernameView.setError(getString(R.string.error_field_required));
			focusView = mUsernameView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.

			// Get view with current focus
			// and dismiss the keyboard
			View v = this.getCurrentFocus();
			if (v == null) {
				return;
			} else {
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				mLoginStatusMessageView
						.setText(R.string.login_progress_please_wait);

				showProgress(true);

				/*
				 * Start Login process
				 */
				TChatApplication.getUserModel().doFirstTimeLogin(mUsername,
						mPassword);

			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mLoginReceiver != null) {
			unregisterReceiver(mLoginReceiver);
		}
	}

	/*
	 * Login receiver
	 */
	private class LoginReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			/*
			 * Login result receiver
			 */
			if (intent.getAction().equalsIgnoreCase(Constants.LOGIN_SUCCESSFUL)) {
				showProgress(false);
				finish();
				loginSuccessful();
			} else if (intent.getAction().equalsIgnoreCase(
					Constants.LOGIN_UNSUCCESSFUL)) {
				showProgress(false);
				loginUnSuccessful();
			}
		}
	}

	private void loginSuccessful() {
		Log.d("LoginActivity", "Login successfull");
		Intent intent = new Intent(context, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
	}

	private void loginUnSuccessful() {
		TChatApplication.getUserModel().setUsername(null);
		TChatApplication.getUserModel().setPassword(null);

		mPasswordView.requestFocus();
		mPasswordView
				.setError(getString(R.string.error_invalid_username_or_password));
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
}
