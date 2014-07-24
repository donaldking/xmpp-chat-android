package co.uk.tusksolutions.tchat.android.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import co.uk.tusksolutions.tchat.android.constants.Constants;

public class APIAddUserToGroup {

	String group_id;
	String user_id;

	private AsyncApiAddUserToGroup mTask = null;

	public void doAddUserToGroup(String group_id, String user_id) {

		if (mTask != null) {
			return;
		}
		this.group_id = group_id;
		this.user_id = user_id;

		mTask = new AsyncApiAddUserToGroup();
		mTask.execute((Void) null);
	}

	/*
	 * Performing Network request
	 */
	private class AsyncApiAddUserToGroup extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			boolean apiResult = false;

			HttpPost httpPost = new HttpPost(Constants.HTTP_SCHEME
					+ Constants.CURRENT_SERVER
					+ Constants.ADD_USER_TO_GROUP_ENDPOINT);

			HttpClient httpclient = new DefaultHttpClient();

			try {

				List<BasicNameValuePair> postParams = new ArrayList<BasicNameValuePair>();
				postParams.add(new BasicNameValuePair("group_id", group_id));
				postParams.add(new BasicNameValuePair("user_id", user_id));
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

			} else {

			}
		}

		@Override
		protected void onCancelled() {
			mTask = null;
			return;
		}
	}
}
