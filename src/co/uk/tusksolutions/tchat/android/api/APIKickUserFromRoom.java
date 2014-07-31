package co.uk.tusksolutions.tchat.android.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import android.os.AsyncTask;
import co.uk.tusksolutions.tchat.android.constants.Constants;

public class APIKickUserFromRoom {
	JSONArray jsonArray;
	private AsyncApiKickUserFromRoom mTask = null;
	String user_id, group_id, admin_name;
	int roomType;

	public void kickUserFromRoom(String user_id, String group_id,
			String admin_name, int roomType) {

		this.user_id = user_id;
		this.group_id = group_id;
		this.admin_name = admin_name;
		this.roomType = roomType;

		if (mTask != null) {
			return;
		}
		mTask = new AsyncApiKickUserFromRoom();
		mTask.execute((Void) null);
	}

	/*
	 * Performing Network request
	 */
	private class AsyncApiKickUserFromRoom extends
			AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			boolean apiResult = false;
			String endpoint = roomType == 0 ? Constants.KICK_USER_FROM_GROUP_ENDPOINT
					: Constants.KICK_USER_FROM_CHAT_ROOM_ENDPOINT;

			try {
				HttpPost httpPost = new HttpPost(Constants.HTTP_SCHEME
						+ Constants.CURRENT_SERVER + endpoint);

				HttpClient httpclient = new DefaultHttpClient();

				List<BasicNameValuePair> postParams = new ArrayList<BasicNameValuePair>();
				postParams.add(new BasicNameValuePair("user_id", user_id));
				postParams.add(new BasicNameValuePair("group_id", group_id));
				postParams
						.add(new BasicNameValuePair("admin_name", admin_name));
				httpPost.setEntity(new UrlEncodedFormEntity(postParams));

				httpclient.execute(httpPost);

				apiResult = true;

			} catch (Exception e) {
				apiResult = false;
				e.printStackTrace();
			}
			return apiResult;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			mTask = null;
			
			if (result) {

				//
			} else {
				//
			}
		}

		@Override
		protected void onCancelled() {
			mTask = null;
			return;
		}
	}
}
