package co.uk.tusksolutions.tchat.android.api;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import android.content.Intent;
import android.os.AsyncTask;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.models.ChatMessagesModel;
import co.uk.tusksolutions.utility.Utility;

public class APIGetMessages {
	JSONArray jsonArray;
	private ChatMessagesModel mChatMessagesModel;
	private AsyncApiGetMessages mTask = null;
	private String buddyUsername;
	private int offset, limit;

	public void getMessages(String buddyUsername, int offset, int limit) {

		if (mTask != null) {
			return;
		}

		this.buddyUsername = buddyUsername;
		this.offset = offset;
		this.limit = limit;

		mTask = new AsyncApiGetMessages();
		mTask.execute((Void) null);
	}

	/*
	 * Performing Network request
	 */
	private class AsyncApiGetMessages extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			boolean apiResult = false;

			/**
			 * Log.d("APIGetMessages", "BASE: " + Constants.HTTP_SCHEME +
			 * Constants.CURRENT_SERVER + Constants.CHAT_MESSAGES_ENDPOINT +
			 * "?sender=" + TChatApplication.getUserModel().getUsername() +
			 * "&receiver=" + buddyUsername + "&offset=" + offset + "&limit=" +
			 * limit);
			 */

			HttpGet request = new HttpGet(Constants.HTTP_SCHEME
					+ Constants.CURRENT_SERVER
					+ Constants.CHAT_MESSAGES_ENDPOINT + "?sender="
					+ TChatApplication.getUserModel().getUsername()
					+ "&receiver=" + buddyUsername + "&offset=" + offset
					+ "&limit=" + limit);

			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response;

			try {
				response = httpclient.execute(request);
				HttpEntity entity = response.getEntity();

				if (entity != null) {
					// JSON Response Read
					InputStream instream = entity.getContent();
					Utility utility = new Utility();
					jsonArray = utility.convertToJSON(utility
							.convertStreamToString(instream));

					if (jsonArray.length() >= 0) {
						mChatMessagesModel = new ChatMessagesModel();
						if (mChatMessagesModel.saveMessageToDB(jsonArray)) {
							apiResult = true;
						}

					} else {
						apiResult = false;
					}
				}
			} catch (Exception e) {
				apiResult = false;
			}
			return apiResult;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			mTask = null;

			if (result) {
				TChatApplication.getContext().sendBroadcast(
						new Intent(Constants.CHAT_MESSAGE_READY));
			} else {
				TChatApplication.getContext().sendBroadcast(
						new Intent(Constants.CHAT_MESSAGE_EMPTY));
			}
		}

		@Override
		protected void onCancelled() {
			mTask = null;
			return;
		}
	}
}
