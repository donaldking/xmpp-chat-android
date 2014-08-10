package co.uk.tusksolutions.gcm;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.google.android.gcm.GCMRegistrar;

import android.os.AsyncTask;
import android.util.Log;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.constants.Constants;

public class APIRegisterPushNotifications {
	private AsyncApiRegisterPushNotifications mTask = null;
	String regId, username, device_id;

	public void doRegisterPushNotifications(String regId, String username,
			String device_id) {

		this.regId = regId;
		this.username = username;
		this.device_id = device_id;

		if (mTask != null) {
			return;
		}
		mTask = new AsyncApiRegisterPushNotifications();
		mTask.execute((Void) null);
	}

	/*
	 * Performing Network request
	 */
	private class AsyncApiRegisterPushNotifications extends
			AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			boolean apiResult = false;

			HttpPost httpPost = new HttpPost(Constants.HTTP_SCHEME
					+ Constants.CURRENT_SERVER
					+ Constants.REGISTER_PUSH_ENDPOINT);

			HttpClient httpclient = new DefaultHttpClient();

			try {
				Log.d("username ", username);
				Log.d("device_id", device_id);

				List<BasicNameValuePair> postParams = new ArrayList<BasicNameValuePair>();
				postParams.add(new BasicNameValuePair("name", username));
				postParams.add(new BasicNameValuePair("device_id", device_id));
				postParams.add(new BasicNameValuePair("regId", regId));
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
			Log.d("Register for Push", "Result: " + result);
			if (result) {

				GCMRegistrar.setRegisteredOnServer(
						TChatApplication.getContext(), true);
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
