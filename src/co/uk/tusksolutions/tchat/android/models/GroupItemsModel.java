package co.uk.tusksolutions.tchat.android.models;

import java.util.ArrayList;

import android.content.Intent;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.dbHelper.TChatDBHelper;

public class GroupItemsModel implements Parcelable {

	public String id;
	public String name;
	public String user;

	public String status;
	public String presenceStatus;
	public String presenceType;
	public String lastSeenTimestamp;
	public String lastMessageTimestamp;
	public String lastMessage;
	public String resourceName;
	private String TABLE = TChatDBHelper.ROSTER_TABLE;

	public static final String TAG = "GroupItemModel";

	public GroupItemsModel() {
	}

	public ArrayList<GroupItemsModel> queryAllFriends() {

		ArrayList<GroupItemsModel> rosterModelCollection = new ArrayList<GroupItemsModel>();

		String orderBy = TChatDBHelper.NAME + " ASC";

		Cursor cursor = TChatApplication.getTChatDBReadable().query(TABLE,
				null, null, null, null, null, orderBy);

		while (cursor.moveToNext()) {
			/*
			 * Request for the values TO_USER be pulled fromUser this cursor and
			 * returned back TO_USER us.
			 */
			rosterModelCollection.add(fromCursor(cursor));
		}
		return rosterModelCollection;

	}

	public ArrayList<GroupItemsModel> querySearch(String text) {

		ArrayList<GroupItemsModel> rosterModelCollection = new ArrayList<GroupItemsModel>();

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

	protected GroupItemsModel fromCursor(Cursor cursor) {
		/*
		 * Pulls the values fromUser the cursor object and returns it TO_USER
		 * the caller
		 */
		GroupItemsModel rosterModel = new GroupItemsModel();
		rosterModel.id = cursor.getString(0);
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
		dest.writeString(id);
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
	protected GroupItemsModel(Parcel in) {
		this.id = in.readString();
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