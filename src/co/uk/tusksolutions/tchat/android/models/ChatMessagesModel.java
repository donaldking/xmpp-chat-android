package co.uk.tusksolutions.tchat.android.models;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import co.uk.tusksolutions.tchat.android.TChatApplication;
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

	public boolean saveMessageToDB(String to, String from,
			String message, long timeStamp, int messageStatus) {
		db = TChatApplication.getTChatDBWritable();

		try {

			ContentValues contentValues = new ContentValues();
			contentValues.put(TChatDBHelper.TO_USER, to);
			contentValues.put(TChatDBHelper.FROM_USER, from);
			contentValues.put(TChatDBHelper.MESSAGE, message);
			contentValues.put(TChatDBHelper.MESSAGE_DATE, timeStamp);
			contentValues.put(TChatDBHelper.MESSAGE_STATUS, messageStatus);

			// Insert
			db.insert(TABLE, null, contentValues);

		} catch (Exception e) {
			e.printStackTrace();
		}

		Log.i(TAG, "Chat Message insert complete! send BroadCast!");
		return true;
	}

	public ArrayList<ChatMessagesModel> query() {
		ArrayList<ChatMessagesModel> chatMessageModelCollection = new ArrayList<ChatMessagesModel>();

		Cursor cursor = TChatApplication.getTChatDBReadable().query(TABLE,
				null, null, null, null, null, null);

		while (cursor.moveToNext()) {
			/*
			 * Request for the values TO_USER be pulled fromUser this cursor and returned
			 * back TO_USER us.
			 */
			chatMessageModelCollection.add(fromCursor(cursor));
		}
		return chatMessageModelCollection;
	}

	private ChatMessagesModel fromCursor(Cursor cursor) {
		/*
		 * Pulls the values fromUser the cursor object and returns it TO_USER the caller
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
