package co.uk.tusksolutions.tchat.android.models;

import java.util.ArrayList;

import android.content.Intent;
import android.database.Cursor;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.dbHelper.TChatDBHelper;

public class RecentsModel extends RosterModel {

	public String lastMessageTimestamp;

	public ArrayList<RosterModel> queryRecents() {

		ArrayList<RosterModel> recentsModelCollection = new ArrayList<RosterModel>();

		Cursor cursor = TChatApplication.getTChatDBReadable().rawQuery(
				"SELECT * FROM " + TABLE + " WHERE "
						+ TChatDBHelper.LAST_MESSAGE_TIMESTAMP
						+ " IS NOT NULL ORDER BY "
						+ TChatDBHelper.LAST_MESSAGE_TIMESTAMP + " DESC", null);

		while (cursor.moveToNext()) {

			recentsModelCollection.add(fromCursor(cursor));
		}

		if (recentsModelCollection.size() == 0) {
			/*
			 * No recent message to anyone
			 */
			TChatApplication.getContext().sendBroadcast(
					new Intent(Constants.RECENTS_EMPTY));
		}
		return recentsModelCollection;
	}
}
