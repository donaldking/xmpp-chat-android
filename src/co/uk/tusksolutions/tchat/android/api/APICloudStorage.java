package co.uk.tusksolutions.tchat.android.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import android.content.Intent;
import android.os.AsyncTask;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.constants.Constants;

public class APICloudStorage {
	JSONArray jsonArray;
	private AsyncApiRecents mTask = null;
	String sender, receiver, message, mid;

	public void saveToCloud(String sender, String receiver, String message,
			String mid) {

		this.sender = sender;
		this.receiver = receiver;
		this.message = message;
		this.mid = mid;

		if (mTask != null) {
			return;
		}
		mTask = new AsyncApiRecents();
		mTask.execute((Void) null);
	}

	/*
	 * Performing Network request
	 */
	private class AsyncApiRecents extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			boolean getStreamMediaResult = false;

			HttpPost httpPost = new HttpPost(Constants.HTTP_SCHEME
					+ Constants.CURRENT_SERVER
					+ Constants.CLOUD_STORAGE_ENDPOINT);

			HttpClient httpclient = new DefaultHttpClient();
			;

			try {

				List<BasicNameValuePair> postParams = new ArrayList<BasicNameValuePair>();
				postParams.add(new BasicNameValuePair("sender", sender));
				postParams.add(new BasicNameValuePair("receiver", receiver));
				postParams.add(new BasicNameValuePair("message", message));
				postParams.add(new BasicNameValuePair("mid", mid));
				httpPost.setEntity(new UrlEncodedFormEntity(postParams));

				HttpResponse response = httpclient.execute(httpPost);

				getStreamMediaResult = true;

			} catch (Exception e) {
				getStreamMediaResult = false;
			}
			return getStreamMediaResult;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			mTask = null;

			if (result) {
				TChatApplication.getContext().sendBroadcast(
						new Intent(Constants.CLOUD_SAVE_SUCCESS));
			} else {
				TChatApplication.getContext().sendBroadcast(
						new Intent(Constants.CLOUD_SAVE_ERROR));
			}
		}

		@Override
		protected void onCancelled() {
			mTask = null;
			return;
		}
	}
}
