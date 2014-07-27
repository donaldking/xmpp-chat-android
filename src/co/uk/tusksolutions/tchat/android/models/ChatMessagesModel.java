package co.uk.tusksolutions.tchat.android.models;

import java.util.ArrayList;

import org.json.JSONArray;
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

public class ChatMessagesModel implements Parcelable {

	public String objectId;
	public String sender;
	public String receiver;
	public String resource;
	public int isGroupMessage;
	public String messageType;
	public String message;
	public String mid;
	public String isRead;
	public String timeStamp;

	private String TABLE = TChatDBHelper.CHAT_MESSAGES_TABLE;
	private SQLiteDatabase db;

	static final String TAG = "ChatMessagesModel";

	public ChatMessagesModel() {

	}

	private void sendBroadcast(long id, String messageType) {
		Log.i(TAG, "Chat Message insert complete! send BroadCast!");
		Intent i = new Intent();
		i.putExtra("id", id);
		i.putExtra("type", messageType);
		i.setAction(Constants.MESSAGE_READY);
		TChatApplication.getContext().sendBroadcast(i);
	}

	public boolean saveMessageToDB(JSONArray messages) {
		int counter = 0;
		db = TChatApplication.getTChatDBWritable();

		/*
		 * This method inserts the recent chats for the current user to db.
		 */
		for (int i = 0; i < messages.length(); i++) {

			try {
				JSONObject messageObject = messages.getJSONObject(i);

				ContentValues contentValues = new ContentValues();
				contentValues.put(TChatDBHelper.CM_OBJECT_ID,
						messageObject.getString("id"));
				contentValues.put(TChatDBHelper.CM_SENDER,
						messageObject.getString("sender"));
				contentValues.put(TChatDBHelper.CM_RECEIVER,
						messageObject.getString("receiver"));
				contentValues.put(TChatDBHelper.CM_IS_GROUP_MESSAGE,
						messageObject.getString("isGroupMessage"));
				contentValues.put(TChatDBHelper.CM_MESSAGE_TYPE,
						messageObject.getString("messageType"));
				contentValues.put(TChatDBHelper.CM_MESSAGE,
						messageObject.getString("message"));
				contentValues.put(TChatDBHelper.CM_MESSAGE_ID,
						messageObject.getString("mid"));
				contentValues.put(TChatDBHelper.CM_IS_READ,
						messageObject.getString("isRead"));
				contentValues.put(TChatDBHelper.CM_TIMESTAMP,
						messageObject.getString("time_stamp"));

				// Insert
				db.insertWithOnConflict(TABLE, null, contentValues,
						SQLiteDatabase.CONFLICT_IGNORE);

				counter++;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Log.d(TAG, "Total Messages " + counter);
		return true;
	}

	public boolean saveMessageToDB(String to, String from, String resource,
			String buddyName, String message, int isGroupMessage,
			String messageType, long timeStamp, int isRead) {

		db = TChatApplication.getTChatDBWritable();

		try {

			ContentValues contentValues = new ContentValues();

			contentValues.put(TChatDBHelper.CM_SENDER, from);
			contentValues.put(TChatDBHelper.CM_RECEIVER, to);
			contentValues.put(TChatDBHelper.CM_RESOURCE, resource);
			contentValues.put(TChatDBHelper.CM_MESSAGE, message);
			contentValues.put(TChatDBHelper.CM_MESSAGE_ID, mid);
			contentValues.put(TChatDBHelper.CM_TIMESTAMP, timeStamp);
			contentValues.put(TChatDBHelper.CM_MESSAGE_TYPE, messageType);
			contentValues.put(TChatDBHelper.CM_IS_READ, isRead);

			// Insert
			long id = db.insert(TABLE, null, contentValues);
			Log.e("Insert in chat Table", "inserted " + id);

			/**
			 * Check message type
			 * 
			 */
			if (updateRecentsTable(to.equalsIgnoreCase(TChatApplication
					.getCurrentJid()) ? from : to, resource, message,
					timeStamp, isRead) == true) {

				sendBroadcast(id, messageType);

			} else {
				if (saveToRecentsTable(to, from, buddyName, message, resource,
						isGroupMessage, messageType, timeStamp, isRead) == true) {

					sendBroadcast(id, messageType);

				} else {
					Log.d("TAG", "ERROR");
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	private boolean updateRecentsTable(String to, String resource,
			String message, long timeStamp, int isRead) {
		try {

			ContentValues contentValues = new ContentValues();
			contentValues.put(TChatDBHelper.R_RESOURCE, resource);
			contentValues.put(TChatDBHelper.R_MESSAGE, message);
			contentValues.put(TChatDBHelper.R_TIMESTAMP, timeStamp);
			contentValues.put(TChatDBHelper.R_IS_READ, isRead);

			// Update
			String whereClause = TChatDBHelper.R_CHAT_WITH + " = ? ";
			String[] whereArgs = new String[] { to };

			long id = db.update(TChatDBHelper.RECENTS_TABLE, contentValues,
					whereClause, whereArgs);

			if (id > 0) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

	private boolean saveToRecentsTable(String to, String from,
			String buddyName, String resource, String message,
			int isGroupMessage, String messageType, long timeStamp, int isRead) {
		try {

			ContentValues contentValues = new ContentValues();

			contentValues.put(TChatDBHelper.R_CHAT_WITH, to
					.equalsIgnoreCase(TChatApplication.getCurrentJid()) ? from
					: to);
			contentValues.put(TChatDBHelper.R_NAME, buddyName);
			contentValues.put(TChatDBHelper.R_SENDER, from);
			contentValues.put(TChatDBHelper.R_RECEIVER, to);
			contentValues.put(TChatDBHelper.R_RESOURCE, resource);
			contentValues.put(TChatDBHelper.R_IS_GROUP_MESSAGE, isGroupMessage);
			contentValues.put(TChatDBHelper.R_MESSAGE_TYPE, messageType);
			contentValues.put(TChatDBHelper.R_MESSAGE, message);
			contentValues.put(TChatDBHelper.R_MESSAGE_ID, mid);
			contentValues.put(TChatDBHelper.R_TIMESTAMP, timeStamp);
			contentValues.put(TChatDBHelper.R_IS_READ, isRead);

			// Insert
			long id = db.insertWithOnConflict(TChatDBHelper.RECENTS_TABLE,
					null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);

			if (id > 0) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

	// Get chat messages: to & from
	public ArrayList<ChatMessagesModel> getAllMessagesFromDB(String to,
			String from) {
		ArrayList<ChatMessagesModel> chatMessageModelCollection = new ArrayList<ChatMessagesModel>();

		String whereClause = TChatDBHelper.CM_RECEIVER + " LIKE ? AND "
				+ TChatDBHelper.CM_SENDER + " LIKE ? OR "
				+ TChatDBHelper.CM_RECEIVER + " LIKE ? AND "
				+ TChatDBHelper.CM_SENDER + " LIKE ?";

		String[] whereArgs = new String[] { to, from, from, to };
		String orderBy = TChatDBHelper.CM_TIMESTAMP + " ASC";
		Cursor cursor = TChatApplication.getTChatDBReadable().query(TABLE,
				null, whereClause, whereArgs, null, null, orderBy);

		while (cursor.moveToNext()) {

			chatMessageModelCollection.add(fromCursor(cursor));
		}
		Log.d("ChatMessagesModel", "Found " + chatMessageModelCollection.size()
				+ " Messages");
		return chatMessageModelCollection;
	}

	public ArrayList<ChatMessagesModel> query() {
		ArrayList<ChatMessagesModel> chatMessageModelCollection = new ArrayList<ChatMessagesModel>();

		Cursor cursor = TChatApplication.getTChatDBReadable().query(TABLE,
				null, null, null, null, null, null);

		while (cursor.moveToNext()) {
			/*
			 * Request for the values RECEIVER be pulled fromUser this cursor
			 * and returned back SENDER us.
			 */
			chatMessageModelCollection.add(fromCursor(cursor));
		}
		return chatMessageModelCollection;
	}

	private ChatMessagesModel fromCursor(Cursor cursor) {
		/*
		 * Pulls the values fromUser the cursor object and returns it TO_USER
		 * the caller
		 */
		ChatMessagesModel chatMessageModel = new ChatMessagesModel();

		chatMessageModel.receiver = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.CM_OBJECT_ID));
		chatMessageModel.receiver = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.CM_RECEIVER));
		chatMessageModel.sender = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.CM_SENDER));

		chatMessageModel.resource = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.CM_RESOURCE));

		chatMessageModel.isGroupMessage = cursor.getInt(cursor
				.getColumnIndex(TChatDBHelper.CM_IS_GROUP_MESSAGE));
		chatMessageModel.messageType = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.CM_MESSAGE_TYPE));
		chatMessageModel.message = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.CM_MESSAGE));
		chatMessageModel.mid = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.CM_MESSAGE_ID));
		chatMessageModel.timeStamp = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.CM_TIMESTAMP));
		chatMessageModel.isRead = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.CM_IS_READ));

		return chatMessageModel;

	}

	public boolean deleteAllChats() {
		db = TChatApplication.getTChatDBWritable();
		/*
		 * Delete all chat messages
		 */
		db.delete(TChatDBHelper.CHAT_MESSAGES_TABLE, null, null);

		return true;
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
		dest.writeString(objectId);
		dest.writeString(receiver);
		dest.writeString(sender);
		dest.writeString(resource);
		dest.writeInt(isGroupMessage);
		dest.writeString(messageType);
		dest.writeString(message);
		dest.writeString(mid);
		dest.writeString(timeStamp);
		dest.writeString(isRead);
	}

	public static final Parcelable.Creator<ChatMessagesModel> CREATOR = new Parcelable.Creator<ChatMessagesModel>() {
		public ChatMessagesModel createFromParcel(Parcel in) {
			return new ChatMessagesModel(in);
		}

		public ChatMessagesModel[] newArray(int size) {
			return new ChatMessagesModel[size];
		}
	};

	/** recreate object fromUser parcel */
	private ChatMessagesModel(Parcel in) {
		this.objectId = in.readString();
		this.receiver = in.readString();
		this.sender = in.readString();
		this.resource = in.readString();
		this.isGroupMessage = in.readInt();
		this.messageType = in.readString();
		this.message = in.readString();
		this.mid = in.readString();
		this.timeStamp = in.readString();
		this.isRead = in.readString();
	}

}
