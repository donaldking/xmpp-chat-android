package co.uk.tusksolutions.tchat.android.api;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import android.os.AsyncTask;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.utility.Utility;

public class APICreateGroup {
	JSONArray jsonArray;
	
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

			HttpGet request = new HttpGet(Constants.HTTP_SCHEME
					+ Constants.CURRENT_SERVER + Constants.CREATE_GROUP
					+ "?group_id="
					+ group_id+"&group_name="
					+group_name+"&admin_name="
					+admin_name+"&password="
					+password);

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
						
							apiResult = true;
						

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
