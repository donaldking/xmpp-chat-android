package co.uk.tusksolutions.tchat.android.dbHelper;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
	public static final String LAST_MESSAGE = "lastMessage";
	public static final String LAST_MESSAGE_STATUS = "lastMessageStatus";
	public static final String LAST_SEEN_TIMESTAMP = "lastSeenTimestamp";
	public static final String LAST_MESSAGE_TIMESTAMP = "lastMessageTimestamp";
	public static final String RESOURCE = "resource";

	/*
	 * Chat messages table definition
	 */
	public static final String CM_UID = "_id";
	public static final String MESSAGE = "message";
	public static final String MESSAGE_DATE = "messageDate";
	public static final String FROM_USER = "fromUser";
	public static final String TO_USER = "toUser";
	public static final String MESSAGE_STATUS = "messageStatus";

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
			+ " varchar(255) DEFAULT NULL," + LAST_MESSAGE
			+ " varchar(255) DEFAULT NULL," + LAST_MESSAGE_STATUS
			+ " varchar(255) DEFAULT NULL," + LAST_SEEN_TIMESTAMP
			+ " varchar(255) DEFAULT NULL," + LAST_MESSAGE_TIMESTAMP
			+ " varchar(255) DEFAULT NULL);";

	/*
	 * Chat messages table schema definition
	 */
	private static final String CREATE_CHAT_MESSAGES_TABLE = "CREATE TABLE "
			+ CHAT_MESSAGES_TABLE + " ( " + CM_UID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + "  " + MESSAGE + " blob,"
			+ MESSAGE_DATE + " varchar(255) DEFAULT NULL," + "  " + FROM_USER
			+ " varchar(255) DEFAULT NULL," + "  " + TO_USER
			+ " varchar(255) DEFAULT NULL," + MESSAGE_STATUS
			+ " varchar(255) DEFAULT NULL);";

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL(CREATE_PROFILE_TABLE);
			db.execSQL(CREATE_ROSTER_TABLE);
			db.execSQL(CREATE_CHAT_MESSAGES_TABLE);
		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
