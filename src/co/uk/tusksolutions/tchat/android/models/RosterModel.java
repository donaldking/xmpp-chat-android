package co.uk.tusksolutions.tchat.android.models;

import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
	public String lastSeenTimestamp;
	public String lastMessageTimestamp;
	public String lastMessage;
	public String resourceName;
	public String TABLE = TChatDBHelper.ROSTER_TABLE;
	private SQLiteDatabase db;
	private boolean selected;

	static final String TAG = "RosterModel";

	public RosterModel() {
		selected = false;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getBuddyName(String buddyJid) {
		db = TChatApplication.getTChatDBReadable();

		String buddyName = null;

		String[] columns = { TChatDBHelper.NAME };
		String whereClause = TChatDBHelper.USER + " = ? ";

		String[] whereArgs = new String[] { buddyJid };

		Cursor cursor = TChatApplication.getTChatDBReadable().query(TABLE,
				columns, whereClause, whereArgs, null, null, null);

		while (cursor.moveToNext()) {
			buddyName = cursor.getString(cursor
					.getColumnIndex(TChatDBHelper.NAME));
		}

		return buddyName;
	}

	public boolean saveRosterToDB(Roster roster) {
		int counter = 0;
		db = TChatApplication.getTChatDBWritable();
		Collection<RosterEntry> entries = roster.getEntries();
		/*
		 * This method inserts the roster received fromUser the server TO_USER
		 * the local db.
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
		
		Log.e("Presence ","Friend id "+friendJid+ " presence type "+presence.toString());

		/*
		 * Mode mode = presence.getMode(); String status = null; if
		 * (mode.toString().equalsIgnoreCase("chat")) { status = "available"; }
		 * else if (mode.toString().equalsIgnoreCase("dnd")) { status =
		 * "available"; } else if (mode.toString().equalsIgnoreCase("away")) {
		 * status = "available"; } else { status = "unavailable"; }
		 */

		String whereClause = TChatDBHelper.USER + " = ? ";
		String[] whereArgs = { friendJid };

		ContentValues contentValues = new ContentValues();
		contentValues.put(TChatDBHelper.PRESENCE_TYPE, presence.getType()
				.toString());
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
		TChatApplication.getContext().sendBroadcast(
				new Intent(Constants.LAST_ONLINE_TIME_STATE_CHANGED));

	}

	public String getLastSeen(String buddyJid) {

		String lastSeen = null;

		String[] columns = { TChatDBHelper.LAST_SEEN_TIMESTAMP };
		String whereClause = TChatDBHelper.USER + " = ? ";

		String[] whereArgs = new String[] { buddyJid };

		Cursor cursor = TChatApplication.getTChatDBReadable().query(TABLE,
				columns, whereClause, whereArgs, null, null, null);

		while (cursor.moveToNext()) {
			/*
			 * Request for the values TO_USER be pulled fromUser this cursor and
			 * returned back TO_USER us.
			 */
			lastSeen = cursor.getString(cursor
					.getColumnIndex(TChatDBHelper.LAST_SEEN_TIMESTAMP));
		}

		return lastSeen;
	}

	public boolean isBuddyOnline(String buddyJid) {

		String whereClause = TChatDBHelper.PRESENCE_TYPE + " = ? AND "
				+ TChatDBHelper.USER + " = ? ";

		String[] whereArgs = new String[] { "available", buddyJid };
		String[] columns = { TChatDBHelper.USER };
		String orderBy = TChatDBHelper.NAME + " ASC";

		Cursor cursor = TChatApplication.getTChatDBReadable().query(TABLE,
				columns, whereClause, whereArgs, null, null, orderBy);
		if (cursor.getCount() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean updateLastOnline(JSONObject jsonObject, String buddyJid) {

		String lastseen;
		try {
			lastseen = jsonObject.getString("seconds");
			if (lastseen.matches("[0-9]+") && lastseen.length() >= 1) {
				String whereClause = TChatDBHelper.USER + " = ? ";
				String[] whereArgs = { buddyJid };

				ContentValues contentValues = new ContentValues();
				contentValues.put(TChatDBHelper.LAST_SEEN_TIMESTAMP, lastseen);

				TChatApplication.getTChatDBWritable().updateWithOnConflict(
						TABLE, contentValues, whereClause, whereArgs,
						SQLiteDatabase.CONFLICT_REPLACE);

				return true;
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
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

	public CopyOnWriteArrayList<RosterModel> queryOnline() {

		CopyOnWriteArrayList<RosterModel> rosterModelCollection = new CopyOnWriteArrayList<RosterModel>();

		String whereClause = TChatDBHelper.PRESENCE_TYPE + " = ? OR "
				+ TChatDBHelper.PRESENCE_TYPE + " = ? OR "
				+ TChatDBHelper.PRESENCE_TYPE + " = ? ";

		String[] whereArgs = new String[] { "available", "busy", "away" };
		String orderBy = TChatDBHelper.NAME + " ASC";

		Cursor cursor = TChatApplication.getTChatDBWritable().query(TABLE,
				null, whereClause, whereArgs, null, null, orderBy);

		while (cursor.moveToNext()) {
			/*
			 * Request for the values TO_USER be pulled fromUser this cursor and
			 * returned back TO_USER us.
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

	public CopyOnWriteArrayList<RosterModel> queryAll() {

		CopyOnWriteArrayList<RosterModel> rosterModelCollection = new CopyOnWriteArrayList<RosterModel>();

		String whereClause = TChatDBHelper.PRESENCE_TYPE + " = ? OR "
				+ TChatDBHelper.PRESENCE_TYPE + " = ? ";

		String[] whereArgs = new String[] { "available", "unavailable" };
		String orderBy = TChatDBHelper.NAME + " ASC";

		Cursor cursor = TChatApplication.getTChatDBReadable().query(TABLE,
				null, whereClause, whereArgs, null, null, orderBy);

		while (cursor.moveToNext()) {
			/*
			 * Request for the values TO_USER be pulled fromUser this cursor and
			 * returned back TO_USER us.
			 */
			rosterModelCollection.add(fromCursor(cursor));
		}
		return rosterModelCollection;
	}

	public CopyOnWriteArrayList<RosterModel> querySearch(String text) {

		CopyOnWriteArrayList<RosterModel> rosterModelCollection = new CopyOnWriteArrayList<RosterModel>();

		String whereClause = TChatDBHelper.NAME + " LIKE ? ";

		String[] whereArgs = new String[] { "%" + text + "%" };
		String orderBy = TChatDBHelper.NAME + " ASC";

		Cursor cursor = TChatApplication.getTChatDBReadable().query(TABLE,
				null, whereClause, whereArgs, null, null, orderBy);

		while (cursor.moveToNext()) {
			/*
			 * Request for the values TO_USER be pulled fromUser this cursor and
			 * returned back TO_USER us.
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

	public CopyOnWriteArrayList<RosterModel> getUsers(JSONArray users)
			throws JSONException {

		CopyOnWriteArrayList<RosterModel> rosterModelCollection = new CopyOnWriteArrayList<RosterModel>();

		for (int i = 0; i < users.length(); i++) {
			JSONObject user = (JSONObject) users.get(i);
			String userJid = user.getString("user_id");

			String whereClause = TChatDBHelper.USER + " = ? ";

			String[] whereArgs = new String[] { userJid };
			String orderBy = TChatDBHelper.NAME + " ASC";

			Cursor cursor = TChatApplication.getTChatDBReadable().query(TABLE,
					null, whereClause, whereArgs, null, null, orderBy);

			while (cursor.moveToNext()) {
				rosterModelCollection.add(fromCursor(cursor));
			}
		}

		if (rosterModelCollection.size() == 0) {
			TChatApplication.getContext().sendBroadcast(
					new Intent(Constants.ROSTER_EMPTY));
		}
		return rosterModelCollection;
	}

	protected RosterModel fromCursor(Cursor cursor) {
		/*
		 * Pulls the values fromUser the cursor object and returns it TO_USER
		 * the caller
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
		rosterModel.lastSeenTimestamp = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.LAST_SEEN_TIMESTAMP));
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
		dest.writeString(lastSeenTimestamp);
		dest.writeString(lastMessageTimestamp);
		dest.writeString(lastMessage);
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

	/** recreate object fromUser parcel */
	protected RosterModel(Parcel in) {
		this.user = in.readString();
		this.name = in.readString();
		this.status = in.readString();
		this.presenceStatus = in.readString();
		this.presenceType = in.readString();
		this.lastSeenTimestamp = in.readString();
		this.lastMessageTimestamp = in.readString();
		this.lastMessage = in.readString();
		this.resourceName = in.readString();
	}

}
