package co.uk.tusksolutions.tchat.android.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.dbHelper.TChatDBHelper;
import co.uk.tusksolutions.tchat.android.xmpp.XMPPConnectionManager;

public class UserModel {

	final static String TAG = "UserModel";
	private String username;
	private String password;
	private String profileName;
	private String lastLogin;
	SQLiteDatabase db;

	public UserModel() {
		prepareProfile();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String name) {
		this.profileName = name;
	}

	public String getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
	}

	private boolean saveUserProfile(String username, String password) {
		db = TChatApplication.getTChatDBWritable();
		/*
		 * Save user profile to db
		 */

		boolean saveResult = false;
		ContentValues contentValues = new ContentValues();

		contentValues.put(TChatDBHelper.USERNAME, username);
		contentValues.put(TChatDBHelper.PASSWORD, password);

		if (db.insert(TChatDBHelper.PROFILE_TABLE, null, contentValues) >= 1) {

			prepareProfile();

			saveResult = true;
		}

		db.close();
		return saveResult;
	}

	public void doFirstTimeLogin(String u, String p) {

		if (TChatApplication.getUserModel().deleteProfile()) {

			if (TChatApplication.getUserModel().saveUserProfile(u, p)) {

				/**
				 * Do Login
				 */
				XMPPConnectionManager.connect(TChatApplication.getUserModel()
						.getUsername(), TChatApplication.getUserModel()
						.getPassword());
			}
		}
	}

	public boolean deleteProfile() {
		db = TChatApplication.getTChatDBWritable();
		/*
		 * Delete all user profile
		 */

		db.delete(TChatDBHelper.PROFILE_TABLE, null, null);

		// Clears any token in memory
		TChatApplication.getUserModel().setUsername(null);
		TChatApplication.getUserModel().setPassword(null);
		db.close();

		return true;
	}

	private void prepareProfile() {
		db = TChatApplication.getTChatDBWritable();
		/*
		 * Pulls and sets the user's profile from db to user object
		 */
		String[] columns = { TChatDBHelper.USERNAME, TChatDBHelper.PASSWORD,
				TChatDBHelper.PROFILE_NAME, TChatDBHelper.LAST_LOGIN };
		Cursor cursor = db.query(TChatDBHelper.PROFILE_TABLE, columns, null,
				null, null, null, null);
		while (cursor.moveToNext()) {

			setUsername(cursor.getString(cursor
					.getColumnIndex(TChatDBHelper.USERNAME)));
			setPassword(cursor.getString(cursor
					.getColumnIndex(TChatDBHelper.PASSWORD)));
			setProfileName(cursor.getString(cursor
					.getColumnIndex(TChatDBHelper.PROFILE_NAME)));
			setLastLogin(cursor.getString(cursor
					.getColumnIndex(TChatDBHelper.LAST_LOGIN)));
		}

		db.close();
	}

}
