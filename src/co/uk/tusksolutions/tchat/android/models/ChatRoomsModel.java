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

public class ChatRoomsModel implements Parcelable {

	final static String TAG = "ChatRoomsModel";
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

	public ChatRoomsModel() {

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
	
	public boolean deleteChatRooms() {

		db = TChatApplication.getTChatDBWritable();
		db.delete(TChatDBHelper.CHATROOMS_TABLE, null, null);
		db.close();

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
	
	public ArrayList<ChatRoomsModel> queryChatrooms() {

		ArrayList<ChatRoomsModel> chatroomsModelCollection = new ArrayList<ChatRoomsModel>();

		Cursor cursor = TChatApplication.getTChatDBReadable().query(TABLE,
				null, null, null, null, null, null);

		while (cursor.moveToNext()) {

			chatroomsModelCollection.add(fromCursor(cursor));
		}

		return chatroomsModelCollection;
	}
	protected ChatRoomsModel fromCursor(Cursor cursor) {
		/*
		 * Pulls the values fromUser the cursor object and returns it TO_USER
		 * the caller
		 */
		ChatRoomsModel chatRoomsModel = new ChatRoomsModel();
		chatRoomsModel.chatroom_jid = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.CR_CHATROOM_ID));
		chatRoomsModel.chatroom_name = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.CR_CHATROOM_NAME));
		chatRoomsModel.chatroom_owner = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.CR_ADMIN));
		chatRoomsModel.start_timestamp = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.CR_START_TIMESTAMP));

		chatRoomsModel.end_timestamp = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.CR_END_TIMESTAMP));

		chatRoomsModel.status = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.CR_STATUS));

		chatRoomsModel.max_guests = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.CR_MAX_GUESTS));
		chatRoomsModel.created_at=cursor.getString(cursor.getColumnIndex(TChatDBHelper.CR_CREATED_AT));

		return chatRoomsModel;

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

	public static final Parcelable.Creator<ChatRoomsModel> CREATOR = new Parcelable.Creator<ChatRoomsModel>() {
		public ChatRoomsModel createFromParcel(Parcel in) {
			return new ChatRoomsModel(in);
		}

		public ChatRoomsModel[] newArray(int size) {
			return new ChatRoomsModel[size];
		}
	};

	/** recreate object fromUser parcel */
	protected ChatRoomsModel(Parcel in) {

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
