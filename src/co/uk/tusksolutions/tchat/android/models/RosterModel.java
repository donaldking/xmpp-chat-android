package co.uk.tusksolutions.tchat.android.models;

import java.util.ArrayList;
import java.util.Collection;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.dbHelper.TChatDBHelper;

public class RosterModel implements Parcelable {

	public String user;
	public String name;
	public String status;
	public String presenceStatus;
	public String presenceType;
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
				Presence entryPresence = roster.getPresence(entry.getUser());

				contentValues.put(TChatDBHelper.PRESENCE_STATUS, entryPresence
						.getStatus() != null ? entryPresence.getStatus() : "");
				contentValues.put(TChatDBHelper.PRESENCE_TYPE, entryPresence
						.getType().toString());

				// Insert
				db.insertWithOnConflict(TABLE, null, contentValues,
						SQLiteDatabase.CONFLICT_IGNORE);

				counter++;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * Broadcast that we have data in now.
		 */
		Intent i = new Intent();
		i.putExtra("inserts", counter);
		i.setAction(Constants.ROSTER_UPDATED);
		TChatApplication.getContext().sendBroadcast(i);

		return true;
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

		return rosterModelCollection;
	}

	public ArrayList<RosterModel> queryAll() {

		ArrayList<RosterModel> rosterModelCollection = new ArrayList<RosterModel>();

		String whereClause = TChatDBHelper.PRESENCE_TYPE + " = ?";
		String[] whereArgs = new String[] { "unavailable" };
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
	}
}
