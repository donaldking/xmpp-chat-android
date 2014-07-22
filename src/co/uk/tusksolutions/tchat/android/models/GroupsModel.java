package co.uk.tusksolutions.tchat.android.models;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.dbHelper.TChatDBHelper;

public class GroupsModel implements Parcelable {

	final static String TAG = "GroupsModel";
	private String TABLE = TChatDBHelper.GROUPS_TABLE;
	public String group_id;
	public String participants;
	public String group_name;
    public String group_admin;
	SQLiteDatabase db;

	public GroupsModel() {

	}

	public boolean saveGroupsToDB(JSONArray groupsJson) {
		db = TChatApplication.getTChatDBWritable();

		for (int i = 0; i < groupsJson.length(); i++) {

			try {
				JSONObject groupsObject = groupsJson.getJSONObject(i);

				ContentValues contentValues = new ContentValues();
				contentValues.put(TChatDBHelper.G_GROUP_ID,
						groupsObject.getString("group_id"));
				contentValues.put(TChatDBHelper.G_ADMIN, groupsObject.getString("group_admin"));
				contentValues.put(TChatDBHelper.G_GROUP_NAME,
						groupsObject.getString("group_name"));
				contentValues.put(TChatDBHelper.G_PARTICIPANTS,
						groupsObject.getString("participants"));
				// Insert
				db.insertWithOnConflict(TChatDBHelper.GROUPS_TABLE, null,
						contentValues, SQLiteDatabase.CONFLICT_REPLACE);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return true;
	}
	
	public boolean saveCreatedRoomInDB(String group_id, String group_name,String group_admin,String participants)
	{
		try {
			Log.d("Debug",participants);
			db = TChatApplication.getTChatDBWritable();
			ContentValues contentValues = new ContentValues();
			contentValues.put(TChatDBHelper.G_GROUP_ID,group_id);
			
			contentValues.put(TChatDBHelper.G_GROUP_NAME,group_name);
			contentValues.put(TChatDBHelper.G_ADMIN, group_admin);
			contentValues.put(TChatDBHelper.G_PARTICIPANTS,participants);
			contentValues.put(TChatDBHelper.G_TIMESTAMP, System.currentTimeMillis());
			// Insert
			db.insertWithOnConflict(TChatDBHelper.GROUPS_TABLE, null,
					contentValues, SQLiteDatabase.CONFLICT_REPLACE);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public ArrayList<GroupsModel> queryGroups() {

		ArrayList<GroupsModel> groupsModelCollection = new ArrayList<GroupsModel>();

		Cursor cursor = TChatApplication.getTChatDBReadable().query(TABLE,
				null, null, null, null, null, null);

		while (cursor.moveToNext()) {

			groupsModelCollection.add(fromCursor(cursor));
		}

		return groupsModelCollection;
	}
	
	public boolean deleteGroups() {

		db = TChatApplication.getTChatDBWritable();
		db.delete(TChatDBHelper.GROUPS_TABLE, null, null);
		db.close();

		return true;
	}

	protected GroupsModel fromCursor(Cursor cursor) {
		/*
		 * Pulls the values fromUser the cursor object and returns it TO_USER
		 * the caller
		 */
		GroupsModel groupsModel = new GroupsModel();
		groupsModel.group_id = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.G_GROUP_ID));
		groupsModel.group_name = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.G_GROUP_NAME));
		groupsModel.participants = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.G_PARTICIPANTS));
		groupsModel.group_admin=cursor.getString(cursor.getColumnIndex(TChatDBHelper.G_ADMIN));

		return groupsModel;

	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(group_id);
		dest.writeString(group_name);
		dest.writeString(participants);
		dest.writeString(group_admin);

	}

	public static final Parcelable.Creator<GroupsModel> CREATOR = new Parcelable.Creator<GroupsModel>() {
		public GroupsModel createFromParcel(Parcel in) {
			return new GroupsModel(in);
		}

		public GroupsModel[] newArray(int size) {
			return new GroupsModel[size];
		}
	};

	/** recreate object fromUser parcel */
	protected GroupsModel(Parcel in) {
		this.group_id = in.readString();
		this.group_name = in.readString();
		this.participants = in.readString();
		this.group_admin=in.readString();

	}
}
