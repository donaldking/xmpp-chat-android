package co.uk.tusksolutions.tchat.android.dbHelper;

import org.jivesoftware.smack.packet.Message;




import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

public class TChatDBHelper extends SQLiteOpenHelper {

	/*
	 * DB Definition
	 */
	private static final String DATABASE_NAME = "tchat.db";
	private static final int DATABASE_VERSION = 1;

	/*
	 * Table names
	 */
	public static final String PROFILE_TABLE = "PROFILE_TABLE";
	public static final String ROSTER_TABLE = "ROSTER_TABLE";
	public static final String GROUPS_TABLE = "GROUPS_TABLE";
	public static final String CHAT_MESSAGES_TABLE = "CHAT_MESSAGES_TABLE";
	public static final String GROUP_CHAT_MESSAGES_TABLE = "GROUP_CHAT_MESSAGES_TABLE";
	public static final String RECENTS_TABLE = "RECENTS_TABLE";

	/*
	 * Profile table definition
	 */
	public static final String P_UID = "_id";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String PROFILE_NAME = "name";
	public static final String LAST_LOGIN = "lastLogin";

	/*
	 * Roster table definition
	 */
	public static final String R_UID = "_id";
	public static final String USER = "user";
	public static final String NAME = "name";
	public static final String STATUS = "status";
	public static final String TYPE = "type";
	public static final String PRESENCE_STATUS = "presenceStatus";
	public static final String PRESENCE_TYPE = "presenceType";
	public static final String LAST_SEEN_TIMESTAMP = "lastSeenTimestamp";
	public static final String RESOURCE = "resource";

	/*
	 * Chat messages table definition
	 */
	public static final String CM_UID = "_id";
	public static final String CM_OBJECT_ID = "id";
	public static final String CM_MESSAGE = "message";
	public static final String CM_MESSAGE_ID = "mid";
	public static final String CM_TIMESTAMP = "timeStamp";
	public static final String CM_SENDER = "sender";
	public static final String CM_RECEIVER = "receiver";
	public static final String CM_IS_READ = "isRead";

	/*
	 * Recents Chat messages table definition
	 */
	public static final String R_R_UID = "_id";
	public static final String R_OBJECT_ID = "id";
	public static final String R_CHAT_WITH = "chatWithUser";
	public static final String R_NAME = "name";
	public static final String R_SENDER = "sender";
	public static final String R_RECEIVER = "receiver";
	public static final String R_MESSAGE = "message";
	public static final String R_MESSAGE_ID = "mid";
	public static final String R_IS_READ = "isRead";
	public static final String R_TIMESTAMP = "time_stamp";

	public TChatDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

	}

	/*
	 * Profile table schema definition
	 */
	private static final String CREATE_PROFILE_TABLE = "CREATE TABLE "
			+ PROFILE_TABLE + " ( " + P_UID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + "  " + USERNAME
			+ " varchar(255) DEFAULT NULL," + "  " + PASSWORD
			+ " varchar(255) DEFAULT NULL," + "  " + PROFILE_NAME
			+ " varchar(255) DEFAULT NULL," + "  " + LAST_LOGIN
			+ " varchar(255) DEFAULT NULL);";

	/*
	 * Roster table schema definition
	 */
	private static final String CREATE_ROSTER_TABLE = "CREATE TABLE "
			+ ROSTER_TABLE + " ( " + R_UID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + "  " + USER
			+ " varchar(255) UNIQUE," + "  " + NAME
			+ " varchar(255) DEFAULT NULL," + "  " + STATUS
			+ " varchar(255) DEFAULT NULL," + "  " + TYPE
			+ " varchar(255) DEFAULT NULL," + "  " + PRESENCE_STATUS
			+ " varchar(255) DEFAULT NULL," + PRESENCE_TYPE
			+ " varchar(255) DEFAULT NULL," + RESOURCE
			+ " varchar(255) DEFAULT NULL," + LAST_SEEN_TIMESTAMP
			+ " varchar(255) DEFAULT NULL);";

	/*
	 * Chat messages table schema definition
	 */
	private static final String CREATE_CHAT_MESSAGES_TABLE = "CREATE TABLE "
			+ CHAT_MESSAGES_TABLE + " ( " + CM_UID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + CM_OBJECT_ID
			+ " varchar(255) UNIQUE," + CM_MESSAGE + " blob," + CM_MESSAGE_ID
			+ " varchar(255) DEFAULT NULL," + CM_TIMESTAMP
			+ " varchar(255) DEFAULT NULL," + CM_SENDER
			+ " varchar(255) DEFAULT NULL," + CM_RECEIVER
			+ " varchar(255) DEFAULT NULL," + CM_IS_READ
			+ " varchar(255) DEFAULT NULL);";

	/*
	 * Recents chats messages table schema definition
	 */
	private static final String CREATE_RECENTS_MESSAGES_TABLE = "CREATE TABLE "
			+ RECENTS_TABLE + " ( " + R_R_UID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + R_OBJECT_ID
			+ " varchar(255) DEFAULT NULL," + R_CHAT_WITH
			+ " varchar(255) UNIQUE," + R_NAME + " varchar(255) DEFAULT NULL,"
			+ R_SENDER + " varchar(255) DEFAULT NULL," + R_RECEIVER
			+ " varchar(255) DEFAULT NULL," + R_MESSAGE + " blob,"
			+ R_MESSAGE_ID + " varchar(255) DEFAULT NULL," + R_IS_READ
			+ " varchar(11)  DEFAULT NULL," + R_TIMESTAMP
			+ " varchar(255) DEFAULT NULL);";

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL(CREATE_PROFILE_TABLE);
			db.execSQL(CREATE_ROSTER_TABLE);
			db.execSQL(CREATE_CHAT_MESSAGES_TABLE);
			db.execSQL(CREATE_RECENTS_MESSAGES_TABLE);
		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
	
	
	
	
	public void SearchResult(String searchText)
	{
		String[] projection = new String[] { ROSTER_TABLE + "." + R_UID, USER ,
				NAME, STATUS , TYPE , PRESENCE_STATUS ,
				PRESENCE_TYPE , LAST_SEEN_TIMESTAMP , RESOURCE };

		String selection = USER + "!=? AND " + NAME + " IS NULL";
		String[] selectionArgs = new String[] { Message.Type.groupchat.name() };
		String sortOrder = NAME + " DESC";

		if (searchText != null) {
			

			if (searchText != null && searchText.length() > 0) {
				selection = selection + " AND " + NAME + " LIKE '%" + searchText + "%'";
			}
		}

	}

}
