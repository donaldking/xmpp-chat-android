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

public class RecentsModel implements Parcelable {

	public String chatWithUser;
	public String name;
	public String objectId;
	public String sender;
	public String receiver;
	public int isGroupMessage;
	public String messageType;
	public String message;
	public String mid;
	public String isRead;
	public String timestamp;

	private static SQLiteDatabase db;
	private String TABLE = TChatDBHelper.RECENTS_TABLE;

	public static final String TAG = "RecentsModel";

	public RecentsModel() {
	}

	public boolean saveRecentsToDB(JSONArray recents) {
		int counter = 0;
		db = TChatApplication.getTChatDBWritable();

		/*
		 * This method inserts the recent chats for the current user to db.
		 */
		for (int i = 0; i < recents.length(); i++) {

			try {
				JSONObject recentsObject = recents.getJSONObject(i);
				JSONObject lastMessageObj = recentsObject
						.getJSONObject("lastMessage");

				ContentValues contentValues = new ContentValues();
				contentValues.put(TChatDBHelper.R_CHAT_WITH,
						recentsObject.getString("chatWithUser"));
				contentValues.put(TChatDBHelper.R_NAME,
						recentsObject.getString("name"));
				contentValues.put(TChatDBHelper.R_OBJECT_ID,
						lastMessageObj.getString("id"));
				contentValues.put(TChatDBHelper.R_SENDER,
						lastMessageObj.getString("sender"));
				contentValues.put(TChatDBHelper.R_RECEIVER,
						lastMessageObj.getString("receiver"));
				contentValues.put(TChatDBHelper.R_IS_GROUP_MESSAGE,
						lastMessageObj.getInt("isGroupMessage"));
				contentValues.put(TChatDBHelper.R_MESSAGE_TYPE,
						lastMessageObj.getString("messageType"));
				contentValues.put(TChatDBHelper.R_MESSAGE,
						lastMessageObj.getString("message"));
				contentValues.put(TChatDBHelper.R_MESSAGE_ID,
						lastMessageObj.getString("mid"));
				contentValues.put(TChatDBHelper.R_IS_READ,
						lastMessageObj.getString("isRead"));
				contentValues.put(TChatDBHelper.R_TIMESTAMP,
						lastMessageObj.getString("time_stamp"));

				// Insert
				db.insertWithOnConflict(TABLE, null, contentValues,
						SQLiteDatabase.CONFLICT_REPLACE);

				counter++;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Log.d(TAG, "Total Recents contacts " + counter);
		return true;
	}

	public ArrayList<RecentsModel> queryRecents() {

		ArrayList<RecentsModel> recentsModelCollection = new ArrayList<RecentsModel>();
		String orderBy = TChatDBHelper.R_TIMESTAMP + " DESC";

		Cursor cursor = TChatApplication.getTChatDBReadable().query(TABLE,
				null, null, null, null, null, orderBy);

		while (cursor.moveToNext()) {

			recentsModelCollection.add(fromCursor(cursor));
		}

		return recentsModelCollection;
	}

	protected RecentsModel fromCursor(Cursor cursor) {
		/*
		 * Pulls the values fromUser the cursor object and returns it TO_USER
		 * the caller
		 */
		RecentsModel recentsModel = new RecentsModel();
		recentsModel.chatWithUser = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.R_CHAT_WITH));
		recentsModel.name = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.R_NAME));
		recentsModel.objectId = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.R_OBJECT_ID));
		recentsModel.sender = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.R_SENDER));
		recentsModel.receiver = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.R_RECEIVER));
		recentsModel.isGroupMessage = cursor.getInt(cursor
				.getColumnIndex(TChatDBHelper.R_IS_GROUP_MESSAGE));
		recentsModel.messageType = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.R_MESSAGE_TYPE));
		recentsModel.message = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.R_MESSAGE));
		recentsModel.mid = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.R_MESSAGE_ID));
		recentsModel.isRead = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.R_IS_READ));
		recentsModel.timestamp = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.R_TIMESTAMP));

		return recentsModel;

	}

	public static boolean deleteRecentsHistoryLocal(String to, String from) {
		db = TChatApplication.getTChatDBWritable();
		/*
		 * Clear chat History of specific USER
		 */
		String whereClause = TChatDBHelper.R_RECEIVER + " LIKE ? AND "
				+ TChatDBHelper.R_SENDER + " LIKE ? OR "
				+ TChatDBHelper.R_RECEIVER + " LIKE ? AND "
				+ TChatDBHelper.R_SENDER + " LIKE ?";

		String[] whereArgs = new String[] { to, from, from, to };

		int i = db.delete(TChatDBHelper.RECENTS_TABLE, whereClause, whereArgs);
		db.close();
		Log.e("delete chat", "i " + i);
		return true;
	}

	public static boolean deleteGroupRecentsHistoryLocal(String receiver) {
		db = TChatApplication.getTChatDBWritable();
		/*
		 * Clear chat History of specific USER
		 */

		String whereClause = TChatDBHelper.R_CHAT_WITH + " LIKE ? ";

		String[] whereArgs = new String[] { receiver };

		int i = db.delete(TChatDBHelper.RECENTS_TABLE, whereClause, whereArgs);
		db.close();
		Log.e("delete chat", "i " + i);
		return true;
	}

	public boolean deleteRecents() {

		db = TChatApplication.getTChatDBWritable();
		db.delete(TChatDBHelper.RECENTS_TABLE, null, null);
		db.close();

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
		dest.writeString(chatWithUser);
		dest.writeString(name);
		dest.writeString(objectId);
		dest.writeString(sender);
		dest.writeString(receiver);
		dest.writeInt(isGroupMessage);
		dest.writeString(messageType);
		dest.writeString(message);
		dest.writeString(mid);
		dest.writeString(isRead);
		dest.writeString(timestamp);
	}

	public static final Parcelable.Creator<RecentsModel> CREATOR = new Parcelable.Creator<RecentsModel>() {
		public RecentsModel createFromParcel(Parcel in) {
			return new RecentsModel(in);
		}

		public RecentsModel[] newArray(int size) {
			return new RecentsModel[size];
		}
	};

	/** recreate object fromUser parcel */
	protected RecentsModel(Parcel in) {
		this.chatWithUser = in.readString();
		this.name = in.readString();
		this.objectId = in.readString();
		this.sender = in.readString();
		this.receiver = in.readString();
		this.isGroupMessage = in.readInt();
		this.messageType = in.readString();
		this.message = in.readString();
		this.mid = in.readString();
		this.isRead = in.readString();
		this.timestamp = in.readString();
	}
}
