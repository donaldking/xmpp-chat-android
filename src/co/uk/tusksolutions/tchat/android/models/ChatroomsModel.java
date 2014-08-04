package co.uk.tusksolutions.tchat.android.models;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.dbHelper.TChatDBHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

public class ChatroomsModel implements Parcelable {

	final static String TAG = "ChatroomsModel";
	private String TABLE = TChatDBHelper.CHATROOMS_TABLE;

	public String chatroom_jid;
    public String chatroom_name;
	public String chatroom_owner;
	public String start_timestamp;
	public String end_timestamp;
	public String status;
	public String created_at;
	public String max_guests;

	static SQLiteDatabase db;

	public ChatroomsModel() {

	}

	
	public boolean saveChatroomsToDB(JSONArray chatroomsJson) {
		db = TChatApplication.getTChatDBWritable();

		for (int i = 0; i < chatroomsJson.length(); i++) {

			try {
				JSONObject groupsObject = chatroomsJson.getJSONObject(i);

				ContentValues contentValues = new ContentValues();
				contentValues.put(TChatDBHelper.CR_CHATROOM_ID,
						groupsObject.getString("room_id"));
				contentValues.put(TChatDBHelper.CR_CHATROOM_NAME,
						groupsObject.getString("room_name"));
				contentValues.put(TChatDBHelper.CR_ADMIN,
						groupsObject.getString("room_owner"));
				contentValues.put(TChatDBHelper.CR_START_TIMESTAMP,
						groupsObject.getString("start_timestamp"));
				contentValues.put(TChatDBHelper.CR_END_TIMESTAMP,
						groupsObject.getString("end_timestamp"));
				contentValues.put(TChatDBHelper.CR_STATUS,
						groupsObject.getString("status"));
				contentValues.put(TChatDBHelper.CR_MAX_GUESTS,
						groupsObject.getString("max_guests"));

				contentValues.put(TChatDBHelper.CR_CREATED_AT,
						groupsObject.getString("created_at"));
				// Insert
				db.insertWithOnConflict(TABLE, null,
						contentValues, SQLiteDatabase.CONFLICT_REPLACE);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return true;
	}
	
	public boolean saveCreatedRoomInDB(String chatroom_jid, String chatroom_name,
			String chatroom_admin, String start_timestamp,String end_timestamp,String status,String max_guests,String create_at) {
		try {

			db = TChatApplication.getTChatDBWritable();
			ContentValues contentValues = new ContentValues();
			contentValues.put(TChatDBHelper.CR_CHATROOM_ID, chatroom_jid);

			contentValues.put(TChatDBHelper.CR_CHATROOM_NAME, chatroom_name);
			contentValues.put(TChatDBHelper.CR_ADMIN,chatroom_admin);
			contentValues.put(TChatDBHelper.CR_START_TIMESTAMP, start_timestamp);
			contentValues.put(TChatDBHelper.CR_END_TIMESTAMP,end_timestamp);
			
			contentValues.put(TChatDBHelper.CR_STATUS,status);
			contentValues.put(TChatDBHelper.CR_MAX_GUESTS,max_guests);
			contentValues.put(TChatDBHelper.CR_CREATED_AT,create_at);
			
			
			
			// Insert
			db.insertWithOnConflict(TABLE, null,
					contentValues, SQLiteDatabase.CONFLICT_REPLACE);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public ArrayList<ChatroomsModel> queryChatrooms() {

		ArrayList<ChatroomsModel> chatroomsModelCollection = new ArrayList<ChatroomsModel>();

		Cursor cursor = TChatApplication.getTChatDBReadable().query(TABLE,
				null, null, null, null, null, null);

		while (cursor.moveToNext()) {

			chatroomsModelCollection.add(fromCursor(cursor));
		}

		return chatroomsModelCollection;
	}
	protected ChatroomsModel fromCursor(Cursor cursor) {
		/*
		 * Pulls the values fromUser the cursor object and returns it TO_USER
		 * the caller
		 */
		ChatroomsModel chatroomsModel = new ChatroomsModel();
		chatroomsModel.chatroom_jid = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.CR_CHATROOM_ID));
		chatroomsModel.chatroom_name = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.CR_CHATROOM_NAME));
		chatroomsModel.chatroom_owner = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.CR_ADMIN));
		chatroomsModel.start_timestamp = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.CR_START_TIMESTAMP));

		chatroomsModel.end_timestamp = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.CR_END_TIMESTAMP));

		chatroomsModel.status = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.CR_STATUS));

		chatroomsModel.max_guests = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.CR_MAX_GUESTS));
		chatroomsModel.created_at=cursor.getString(cursor.getColumnIndex(TChatDBHelper.CR_CREATED_AT));

		return chatroomsModel;

	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(chatroom_jid);
		dest.writeString(chatroom_name);
		dest.writeString(chatroom_owner);
		dest.writeString(start_timestamp);
		dest.writeString(end_timestamp);
		dest.writeString(status);
		dest.writeString(created_at);
		dest.writeString(max_guests);

	}

	public static final Parcelable.Creator<ChatroomsModel> CREATOR = new Parcelable.Creator<ChatroomsModel>() {
		public ChatroomsModel createFromParcel(Parcel in) {
			return new ChatroomsModel(in);
		}

		public ChatroomsModel[] newArray(int size) {
			return new ChatroomsModel[size];
		}
	};

	/** recreate object fromUser parcel */
	protected ChatroomsModel(Parcel in) {

		this.chatroom_jid = in.readString();
		this.chatroom_name = in.readString();
		this.chatroom_owner = in.readString();
		this.start_timestamp = in.readString();
		this.end_timestamp = in.readString();
		this.status = in.readString();
		this.max_guests = in.readString();
		this.created_at = in.readString();

	}
}
