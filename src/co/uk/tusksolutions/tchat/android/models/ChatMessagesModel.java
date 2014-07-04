package co.uk.tusksolutions.tchat.android.models;

import java.util.ArrayList;

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

	public String toUser;
	public String fromUser;
	public String message;
	public String messageDate;
	public String messageStatus;
	private String TABLE = TChatDBHelper.CHAT_MESSAGES_TABLE;
	private SQLiteDatabase db;

	static final String TAG = "ChatMessagesModel";

	public ChatMessagesModel() {

	}

	public boolean saveMessageToDB(String to, String from, String message,
			long timeStamp, int messageStatus) {
		db = TChatApplication.getTChatDBWritable();

		try {

			ContentValues contentValues = new ContentValues();
			contentValues.put(TChatDBHelper.TO_USER, to);
			contentValues.put(TChatDBHelper.FROM_USER, from);
			contentValues.put(TChatDBHelper.MESSAGE, message);
			contentValues.put(TChatDBHelper.MESSAGE_DATE, timeStamp);
			contentValues.put(TChatDBHelper.MESSAGE_STATUS, messageStatus);

			// Insert
			long id = db.insert(TABLE, null, contentValues);

			/*
			 * if (id > 0) { Intent i = new Intent(); i.putExtra("id", id);
			 * i.setAction(Constants.CHAT_MESSAGE_READY);
			 * TChatApplication.getContext().sendBroadcast(i); }
			 */

			if (updateRosterTable(to.equalsIgnoreCase(TChatApplication
					.getCurrentJid()) ? from : to, message, timeStamp) == true)
				;
			{
				Log.i(TAG, "Chat Message insert complete! send BroadCast!");
				Intent i = new Intent();
				i.putExtra("id", id);
				i.setAction(Constants.CHAT_MESSAGE_READY);
				TChatApplication.getContext().sendBroadcast(i);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	private boolean updateRosterTable(String to, String message, long timeStamp) {
		try {

			ContentValues contentValues = new ContentValues();
			contentValues.put(TChatDBHelper.LAST_MESSAGE, message);
			contentValues.put(TChatDBHelper.LAST_MESSAGE_TIMESTAMP, timeStamp);
			contentValues.put(TChatDBHelper.LAST_MESSAGE_STATUS, messageStatus);

			// Insert
			String whereClause = TChatDBHelper.USER + " = ? ";
			String[] whereArgs = new String[] { to };

			db.update("ROSTER_TABLE", contentValues, whereClause, whereArgs);
			return true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

	// Get chat messages: to & from
	public ArrayList<ChatMessagesModel> getAllMessagesFromDB(String to,
			String from) {
		ArrayList<ChatMessagesModel> chatMessageModelCollection = new ArrayList<ChatMessagesModel>();

		String whereClause = TChatDBHelper.TO_USER + " LIKE ? AND "
				+ TChatDBHelper.FROM_USER + " LIKE ? OR "
				+ TChatDBHelper.TO_USER + " LIKE ? AND "
				+ TChatDBHelper.FROM_USER + " LIKE ?";

		String[] whereArgs = new String[] { to, from, from, to };
		String orderBy = TChatDBHelper.MESSAGE_DATE + " ASC";
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
			 * Request for the values TO_USER be pulled fromUser this cursor and
			 * returned back TO_USER us.
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

		chatMessageModel.toUser = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.TO_USER));
		chatMessageModel.fromUser = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.FROM_USER));
		chatMessageModel.message = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.MESSAGE));
		chatMessageModel.messageDate = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.MESSAGE_DATE));
		chatMessageModel.messageStatus = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.MESSAGE_STATUS));

		return chatMessageModel;

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
		dest.writeString(toUser);
		dest.writeString(fromUser);
		dest.writeString(message);
		dest.writeString(messageDate);
		dest.writeString(messageStatus);
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
		this.toUser = in.readString();
		this.fromUser = in.readString();
		this.message = in.readString();
		this.messageDate = in.readString();
		this.messageStatus = in.readString();
	}

}
