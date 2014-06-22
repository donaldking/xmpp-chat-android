package co.uk.tusksolutions.tchat.android.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.dbHelper.TChatDBHelper;

public class RosterModel implements Parcelable {

	public String user;
	public String name;
	public String status;
	public String presenceStatus;
	public String presenceType;
	public String resourceName;
	private String TABLE = TChatDBHelper.ROSTER_TABLE;
	private SQLiteDatabase db;

	static final String TAG = "RosterModel";

	public RosterModel() {
	}

	public boolean saveRosterToDB(Roster roster) {
		int counter = 0;
		db = TChatApplication.getTChatDBWritable();
		Collection<RosterEntry> entries = roster.getEntries();
		/*
		 * This method inserts the roster received from the server to the local
		 * db.
		 */

		for (RosterEntry entry : entries) {
			try {
				ContentValues contentValues = new ContentValues();

				roster.getPresence(entry.getUser());

				contentValues.put(TChatDBHelper.USER, entry.getUser());
				contentValues.put(TChatDBHelper.NAME, entry.getName());
				contentValues.put(TChatDBHelper.STATUS,
						entry.getStatus() != null ? entry.getStatus()
								.toString() : "");
				contentValues.put(TChatDBHelper.TYPE, entry.getType()
						.toString());

				// Get presence object
				Presence presence = roster.getPresence(entry.getUser());

				contentValues.put(TChatDBHelper.PRESENCE_STATUS, presence
						.getStatus() != null ? presence.getStatus() : "");
				contentValues.put(TChatDBHelper.PRESENCE_TYPE, presence
						.getType().toString());

				// Get Resource Name
				if (presence.getType() != null
						&& presence.getType() == Presence.Type.available) {
					String[] strTemp = presence.getFrom().split("/");
					contentValues.put(TChatDBHelper.RESOURCE,
							getResourceType(strTemp[1].toLowerCase(Locale
									.getDefault())));
				}

				// Insert
				db.insertWithOnConflict(TABLE, null, contentValues,
						SQLiteDatabase.CONFLICT_IGNORE);

				counter++;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Log.d(TAG, "Total Roster contacts " + counter);

		/**
		 * Broadcast that we have data in now.
		 */
		Intent i = new Intent();
		i.putExtra("inserts", counter);
		i.setAction(Constants.ROSTER_UPDATED);
		TChatApplication.getContext().sendBroadcast(i);

		return true;
	}

	private String getResourceType(String string) {
		String resource = null;
		if (string.matches("[0-9]+") && string.length() >= 1) {
			resource = "Web";
		} else {
			resource = "Mobile";
		}

		return resource;
	}

	public boolean deleteRosterRecords() {
		db = TChatApplication.getTChatDBWritable();
		/*
		 * Delete all roster records
		 */

		db.delete(TChatDBHelper.ROSTER_TABLE, null, null);

		// Clears any token in memory
		TChatApplication.getUserModel().setUsername(null);
		TChatApplication.getUserModel().setPassword(null);
		db.close();

		return true;
	}

	public void updatePresenceForFriend(String friendJid, Presence presence,
			String resource) {

		String whereClause = TChatDBHelper.USER + " = ? ";
		String[] whereArgs = { friendJid };

		ContentValues contentValues = new ContentValues();
		contentValues.put(TChatDBHelper.PRESENCE_TYPE, presence.getType()
				.name());
		contentValues.put(TChatDBHelper.RESOURCE,
				getResourceType(resource.toLowerCase(Locale.getDefault())));

		TChatApplication.getTChatDBWritable().updateWithOnConflict(TABLE,
				contentValues, whereClause, whereArgs,
				SQLiteDatabase.CONFLICT_IGNORE);

		/**
		 * Broadcast presence updated.
		 */
		TChatApplication.getContext().sendBroadcast(
				new Intent(Constants.ROSTER_UPDATED));

	}

	public void setAllOffline() {

		String whereClause = TChatDBHelper.PRESENCE_TYPE + " = ? ";
		String[] whereArgs = { "available" };

		ContentValues contentValues = new ContentValues();
		contentValues.put(TChatDBHelper.PRESENCE_TYPE, "unavailable");
		contentValues.put(TChatDBHelper.RESOURCE, "null");

		TChatApplication.getTChatDBWritable().updateWithOnConflict(TABLE,
				contentValues, whereClause, whereArgs,
				SQLiteDatabase.CONFLICT_IGNORE);

		/**
		 * Broadcast presence updated.
		 */
		TChatApplication.getContext().sendBroadcast(
				new Intent(Constants.ROSTER_UPDATED));

	}

	public ArrayList<RosterModel> queryOnline() {

		ArrayList<RosterModel> rosterModelCollection = new ArrayList<RosterModel>();

		String whereClause = TChatDBHelper.PRESENCE_TYPE + " = ? OR "
				+ TChatDBHelper.PRESENCE_TYPE + " = ? OR "
				+ TChatDBHelper.PRESENCE_TYPE + " = ? ";

		String[] whereArgs = new String[] { "available", "busy", "away" };
		String orderBy = TChatDBHelper.NAME + " ASC";

		Cursor cursor = TChatApplication.getTChatDBReadable().query(TABLE,
				null, whereClause, whereArgs, null, null, orderBy);

		while (cursor.moveToNext()) {
			/*
			 * Request for the values to be pulled from this cursor and returned
			 * back to us.
			 */
			rosterModelCollection.add(fromCursor(cursor));
		}

		if (rosterModelCollection.size() == 0) {
			/*
			 * No one online
			 */
			TChatApplication.getContext().sendBroadcast(
					new Intent(Constants.ROSTER_EMPTY));
		}
		return rosterModelCollection;
	}

	public ArrayList<RosterModel> queryAll() {

		ArrayList<RosterModel> rosterModelCollection = new ArrayList<RosterModel>();

		String whereClause = TChatDBHelper.PRESENCE_TYPE + " = ? OR "
				+ TChatDBHelper.PRESENCE_TYPE + " = ? ";

		String[] whereArgs = new String[] { "available", "unavailable"};
		String orderBy = TChatDBHelper.NAME + " ASC";

		Cursor cursor = TChatApplication.getTChatDBReadable().query(TABLE,
				null, whereClause, whereArgs, null, null, orderBy);

		while (cursor.moveToNext()) {
			/*
			 * Request for the values to be pulled from this cursor and returned
			 * back to us.
			 */
			rosterModelCollection.add(fromCursor(cursor));
		}
		return rosterModelCollection;
	}

	private RosterModel fromCursor(Cursor cursor) {
		/*
		 * Pulls the values from the cursor object and returns it to the caller
		 */
		RosterModel rosterModel = new RosterModel();

		rosterModel.user = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.USER));
		rosterModel.name = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.NAME));
		rosterModel.status = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.STATUS));
		rosterModel.presenceStatus = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.PRESENCE_STATUS));
		rosterModel.presenceType = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.PRESENCE_TYPE));
		rosterModel.resourceName = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.RESOURCE));

		return rosterModel;

	}

	/*
	 * Parcelable stuff non-Javadoc)
	 * 
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(user);
		dest.writeString(name);
		dest.writeString(status);
		dest.writeString(presenceStatus);
		dest.writeString(presenceType);
		dest.writeString(resourceName);
	}

	public static final Parcelable.Creator<RosterModel> CREATOR = new Parcelable.Creator<RosterModel>() {
		public RosterModel createFromParcel(Parcel in) {
			return new RosterModel(in);
		}

		public RosterModel[] newArray(int size) {
			return new RosterModel[size];
		}
	};

	/** recreate object from parcel */
	private RosterModel(Parcel in) {
		this.user = in.readString();
		this.name = in.readString();
		this.status = in.readString();
		this.presenceStatus = in.readString();
		this.presenceType = in.readString();
		this.resourceName = in.readString();
	}

}
