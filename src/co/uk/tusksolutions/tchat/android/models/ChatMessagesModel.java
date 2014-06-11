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

	public String sender;
	public String receiver;
	public String message;
	public String messageDate;
	public String messageStatus;
	private String TABLE = TChatDBHelper.CHAT_MESSAGES_TABLE;
	private SQLiteDatabase db;

	static final String TAG = "ChatMessagesModel";

	public ChatMessagesModel() {

	}

	public boolean saveMessageToDB(String sender, String receiver,
			String message, String timeStamp, String messageStatus) {
		db = TChatApplication.getTChatDBWritable();

		try {
			Long tsLong = System.currentTimeMillis() / 1000;
			String ts = tsLong.toString();

			ContentValues contentValues = new ContentValues();
			
			contentValues.put(TChatDBHelper.SENDER, sender);
			contentValues.put(TChatDBHelper.RECEIVER, receiver);
			contentValues.put(TChatDBHelper.MESSAGE, message);
			contentValues.put(TChatDBHelper.MESSAGE_DATE, ts);
			contentValues.put(TChatDBHelper.MESSAGE_STATUS, messageStatus);

			// Insert
			db.insertWithOnConflict(TABLE, null, contentValues,
					SQLiteDatabase.CONFLICT_IGNORE);

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
			 * Request for the values to be pulled from this cursor and returned
			 * back to us.
			 */
			chatMessageModelCollection.add(fromCursor(cursor));
		}
		return chatMessageModelCollection;
	}

	private ChatMessagesModel fromCursor(Cursor cursor) {
		/*
		 * Pulls the values from the cursor object and returns it to the caller
		 */
		ChatMessagesModel chatMessageModel = new ChatMessagesModel();

		chatMessageModel.sender = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.SENDER));
		chatMessageModel.receiver = cursor.getString(cursor
				.getColumnIndex(TChatDBHelper.RECEIVER));
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
		dest.writeString(sender);
		dest.writeString(receiver);
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

	/** recreate object from parcel */
	private ChatMessagesModel(Parcel in) {
		this.sender = in.readString();
		this.receiver = in.readString();
		this.message = in.readString();
		this.messageDate = in.readString();
		this.messageStatus = in.readString();
	}

}
