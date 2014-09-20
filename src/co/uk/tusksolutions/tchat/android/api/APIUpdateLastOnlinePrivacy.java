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
import android.util.Log;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.constants.Constants;

public class APIUpdateLastOnlinePrivacy {
	JSONArray jsonArray;
	private AsyncApiUpdateLastOnline mTask = null;
	String status;

	public void updateLastOnlinePrivacy(String status) {

		this.status = status;

		if (mTask != null) {
			return;
		}
		mTask = new AsyncApiUpdateLastOnline();
		mTask.execute((Void) null);
	}

	/*
	 * Performing Network request
	 */
	private class AsyncApiUpdateLastOnline extends
			AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			boolean apiResult = false;

			HttpPost httpPost = new HttpPost(Constants.HTTP_SCHEME
					+ Constants.CURRENT_SERVER
					+ Constants.UPDATE_LAST_ONLINE_PRIVACY_ENDPOINT);

			HttpClient httpclient = new DefaultHttpClient();
			;

			try {

				List<BasicNameValuePair> postParams = new ArrayList<BasicNameValuePair>();
				postParams.add(new BasicNameValuePair("username",
						TChatApplication.getUserModel().getUsername()));
				postParams.add(new BasicNameValuePair("status", status));
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
				// TChatApplication.getContext().sendBroadcast(
				// new Intent(Constants.CLOUD_SAVE_SUCCESS));
				Log.v("Update Last Seen ","Succesfully update Last seen status "+status);
			} else {
				// TChatApplication.getContext().sendBroadcast(
				// new Intent(Constants.CLOUD_SAVE_ERROR));
				Log.v("Update Last Seen ","Failed to update Last seen status "+status);
			}
		}

		@Override
		protected void onCancelled() {
			mTask = null;
			return;
		}
	}
}
