package co.uk.tusksolutions.tchat.android.models;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.api.APIKickUserFromRoom;
import co.uk.tusksolutions.tchat.android.dbHelper.TChatDBHelper;
import co.uk.tusksolutions.tchat.android.xmpp.XMPPMUCManager;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

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
				db.insertWithOnConflict(TABLE, null, contentValues,
						SQLiteDatabase.CONFLICT_REPLACE);

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

	public boolean saveCreatedRoomInDB(String chatroom_jid,
			String chatroom_name, String chatroom_admin,
			String start_timestamp, String end_timestamp, String status,
			String max_guests, String create_at) {
		try {

			db = TChatApplication.getTChatDBWritable();
			ContentValues contentValues = new ContentValues();
			contentValues.put(TChatDBHelper.CR_CHATROOM_ID, chatroom_jid);

			contentValues.put(TChatDBHelper.CR_CHATROOM_NAME, chatroom_name);
			contentValues.put(TChatDBHelper.CR_ADMIN, chatroom_admin);
			contentValues
					.put(TChatDBHelper.CR_START_TIMESTAMP, start_timestamp);
			contentValues.put(TChatDBHelper.CR_END_TIMESTAMP, end_timestamp);

			contentValues.put(TChatDBHelper.CR_STATUS, status);
			contentValues.put(TChatDBHelper.CR_MAX_GUESTS, max_guests);
			contentValues.put(TChatDBHelper.CR_CREATED_AT, create_at);

			// Insert
			db.insertWithOnConflict(TABLE, null, contentValues,
					SQLiteDatabase.CONFLICT_REPLACE);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public ArrayList<ChatRoomsModel> queryAllChatrooms() {

		ArrayList<ChatRoomsModel> chatroomsModelCollection = new ArrayList<ChatRoomsModel>();
		String orderBy = TChatDBHelper.CR_CREATED_AT + " DESC";
		Cursor cursor = TChatApplication.getTChatDBReadable().query(TABLE,
				null, null, null, null, null, orderBy);

		while (cursor.moveToNext()) {

			chatroomsModelCollection.add(fromCursor(cursor));
		}

		return chatroomsModelCollection;
	}

	public ArrayList<ChatRoomsModel> queryActiveChatrooms() {

		ArrayList<ChatRoomsModel> chatroomsModelCollection = new ArrayList<ChatRoomsModel>();

		String currentTime = String.valueOf(System.currentTimeMillis());

		String whereClause = TChatDBHelper.CR_START_TIMESTAMP + " <= ? ";
		         //+
				 //" AND "
			     //+ TChatDBHelper.CR_END_TIMESTAMP + " >= ?";
		String[] whereArgs = new String[] { currentTime };
		String orderBy = TChatDBHelper.CR_CREATED_AT + " DESC";

		Cursor cursor = TChatApplication.getTChatDBWritable().query(TABLE,
				null, whereClause, whereArgs, null, null, orderBy);
		Log.e("length", "active Size " + cursor.getCount());
		while (cursor.moveToNext()) {

			chatroomsModelCollection.add(fromCursor(cursor));
		}

		return chatroomsModelCollection;
	}

	public ArrayList<ChatRoomsModel> queryScheduledChatrooms() {

		ArrayList<ChatRoomsModel> chatroomsModelCollection = new ArrayList<ChatRoomsModel>();

		String currentTime = String.valueOf(System.currentTimeMillis());

		String whereClause = TChatDBHelper.CR_START_TIMESTAMP + " > ? ";

		String[] whereArgs = new String[] { currentTime };
		String orderBy = TChatDBHelper.CR_CREATED_AT + " DESC";

		Cursor cursor = TChatApplication.getTChatDBWritable().query(TABLE,
				null, whereClause, whereArgs, null, null, orderBy);

		Log.e("length", "scheduled Size " + cursor.getCount());
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
		chatRoomsModel.created_at = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.CR_CREATED_AT));

		return chatRoomsModel;

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

	public static void joinAllChatrooms() {

		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				// Rejoin all rooms so we can receive notifications
				// when new messages come in.

				ChatRoomsModel gm = new ChatRoomsModel();
				ArrayList<ChatRoomsModel> chatroomCollection = gm
						.queryChatrooms();
				for (ChatRoomsModel chatroomsModel : chatroomCollection) {
					// Join all rooms
					try {
						XMPPMUCManager.getInstance(
								TChatApplication.getContext())
								.mucServiceDiscovery();

						XMPPMUCManager.getInstance(
								TChatApplication.getContext()).joinRoom(
								TChatApplication.connection,
								chatroomsModel.chatroom_jid, "",
								TChatApplication.getUserModel().getUsername());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		};
		new Thread(runnable).start();
	}

	public static void kickUserFromGroup(final String roomJid,
			final String userJid) {
		Runnable runnable = new Runnable() {

			@Override
			public void run() {

				APIKickUserFromRoom kickUserFromRoom = new APIKickUserFromRoom();
				kickUserFromRoom.kickUserFromRoom(userJid, roomJid,
						TChatApplication.getCurrentJid(), 0);
			}

		};
		new Thread(runnable).start();
	}

	public static boolean isAdmin(String group_id, String group_admin) {

		db = TChatApplication.getTChatDBReadable();

		String[] columns = { TChatDBHelper.CR_ADMIN };
		String whereClause = TChatDBHelper.CR_CHATROOM_ID + " = ? AND "
				+ TChatDBHelper.CR_ADMIN + " = ?";

		String[] whereArgs = new String[] { group_id, group_admin };

		Cursor cursor = TChatApplication.getTChatDBReadable().query(
				TChatDBHelper.CHATROOMS_TABLE, columns, whereClause, whereArgs,
				null, null, null);

		while (cursor.moveToNext()) {
			return true;
		}

		return false;
	}

	public String getChatRoomName(String chatroomId) {
		db = TChatApplication.getTChatDBReadable();
		String groupName = null;

		String[] columns = { TChatDBHelper.CR_CHATROOM_NAME };
		String whereClause = TChatDBHelper.CR_CHATROOM_ID + " = ? ";

		String[] whereArgs = new String[] { chatroomId };

		Cursor cursor = TChatApplication.getTChatDBReadable().query(TABLE,
				columns, whereClause, whereArgs, null, null, null);

		while (cursor.moveToNext()) {
			groupName = cursor.getString(cursor
					.getColumnIndex(TChatDBHelper.CR_CHATROOM_NAME));
		}

		return groupName;
	}
	
	

	public boolean updateParticipantsinChatroom(String chatroom_id,
			JSONArray participantsarray) {

		String whereClause = TChatDBHelper.CR_CHATROOM_ID + " = ? ";
		String[] whereArgs = { chatroom_id };

		ContentValues contentValues = new ContentValues();
		contentValues.put(TChatDBHelper.CR_PARTICIPANTS,
				participantsarray.toString());

		TChatApplication.getTChatDBWritable().updateWithOnConflict(TABLE,
				contentValues, whereClause, whereArgs,
				SQLiteDatabase.CONFLICT_REPLACE);

		return true;

	}
	
	public boolean updateStatusofChatRoom(String chatroom_id,
			String status) {
		db = TChatApplication.getTChatDBReadable();
		String whereClause = TChatDBHelper.CR_CHATROOM_ID + " = ? ";
		String[] whereArgs = { chatroom_id };

		ContentValues contentValues = new ContentValues();
		contentValues.put(TChatDBHelper.CR_STATUS,
				status);

		TChatApplication.getTChatDBWritable().updateWithOnConflict(TABLE,
				contentValues, whereClause, whereArgs,
				SQLiteDatabase.CONFLICT_REPLACE);
		db.close();

		return true;

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
