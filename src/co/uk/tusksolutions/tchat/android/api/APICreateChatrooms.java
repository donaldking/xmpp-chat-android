package co.uk.tusksolutions.tchat.android.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.util.Log;

import co.uk.tusksolutions.tchat.android.constants.Constants;

public class APICreateChatrooms {
	String chatroom_id;
	String chatroom_name;
	String chatroom_owner;
	String start_timestamp;
	String end_timestamp;
	String status;
	String max_guest;
	String created_at;
	private AsyncApiPostChatroom mTask = null;

	public void doPostChatroom(String chatroom_id, String chatroom_name,
			String chatroom_owner, String start_timestamp,
			String end_timestamp, String status, String max_guest,
			String created_at) {
		if (mTask != null) {
			return;
		}
		this.chatroom_id = chatroom_id;
		this.chatroom_name = chatroom_name;
		this.chatroom_owner = chatroom_owner;
		this.start_timestamp = start_timestamp;
		this.end_timestamp = end_timestamp;
		this.status = status;
		this.created_at = created_at;
		mTask = new AsyncApiPostChatroom();
		mTask.execute((Void) null);
	}

	/*
	 * Performing Network request
	 */
	private class AsyncApiPostChatroom extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			boolean apiResult = false;

			HttpPost httpPost = new HttpPost(Constants.HTTP_SCHEME
					+ Constants.CURRENT_SERVER
					+ Constants.CREATE_CHATROOM_ENDPOINT);

			HttpClient httpclient = new DefaultHttpClient();

			try {

				List<BasicNameValuePair> postParams = new ArrayList<BasicNameValuePair>();
				postParams.add(new BasicNameValuePair("room_id", chatroom_id));
				postParams.add(new BasicNameValuePair("room_name",
						chatroom_name));
				postParams.add(new BasicNameValuePair("room_owner",
						chatroom_owner));
				postParams.add(new BasicNameValuePair("start_timestamp",
						start_timestamp));
				postParams.add(new BasicNameValuePair("end_timestamp",
						end_timestamp));
				postParams.add(new BasicNameValuePair("status", status));
				postParams.add(new BasicNameValuePair("max_guests", max_guest));

				httpPost.setEntity(new UrlEncodedFormEntity(postParams));

				httpclient.execute(httpPost);

				apiResult = true;

			} catch (Exception e) {
				apiResult = false;
			}
			return apiResult;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			mTask = null;

			if (result) {
				Log.e("APICreateChatrooms", "Chatroom posted sucessfully");
			} else {
				Log.e("APICreateChatrooms", "Chatroom posted unsucessfull");
			}
		}

		@Override
		protected void onCancelled() {
			mTask = null;
			return;
		}
	}

}
