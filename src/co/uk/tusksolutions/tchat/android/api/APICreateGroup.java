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

public class APICreateGroup {
	
	String group_id;
	String group_name;
	String admin_name;
	String password;
	private AsyncApiPostGroup mTask = null;

	public void doPostGroup(String group_id, String group_name,
			String admin_name, String password) {

		if (mTask != null) {
			return;
		}
		this.group_id = group_id;
		this.group_name = group_name;
		this.admin_name = admin_name;
		this.password = password;
		mTask = new AsyncApiPostGroup();
		mTask.execute((Void) null);
	}

	/*
	 * Performing Network request
	 */
	private class AsyncApiPostGroup extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			boolean apiResult = false;

			HttpPost httpPost = new HttpPost(Constants.HTTP_SCHEME
					+ Constants.CURRENT_SERVER
					+ Constants.CREATE_GROUP_ENDPOINT);

			HttpClient httpclient = new DefaultHttpClient();

			try {

				List<BasicNameValuePair> postParams = new ArrayList<BasicNameValuePair>();
				postParams.add(new BasicNameValuePair("group_id", group_id));
				postParams
						.add(new BasicNameValuePair("group_name", group_name));
				postParams
						.add(new BasicNameValuePair("admin_name", admin_name));
				postParams.add(new BasicNameValuePair("password", password));
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
