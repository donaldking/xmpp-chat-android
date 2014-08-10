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

public class APIUnRegisterPushNotifications {
	private AsyncApiUnRegisterPushNotifications mTask = null;
	String device_id;

	public void doUnRegisterPushNotifications(String device_id) {
		
		this.device_id = device_id;

		if (mTask != null) {
			return;
		}
		mTask = new AsyncApiUnRegisterPushNotifications();
		mTask.execute((Void) null);
	}

	/*
	 * Performing Network request
	 */
	private class AsyncApiUnRegisterPushNotifications extends
			AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			boolean apiResult = false;

			HttpPost httpPost = new HttpPost(Constants.HTTP_SCHEME
					+ Constants.CURRENT_SERVER
					+ Constants.UNREGISTER_PUSH_ENDPOINT);

			HttpClient httpclient = new DefaultHttpClient();

			try {
				Log.d("device_id", device_id);

				List<BasicNameValuePair> postParams = new ArrayList<BasicNameValuePair>();
				postParams.add(new BasicNameValuePair("device_id", device_id));
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
			Log.d("Unregister for Push", "Result: " + result);
			if (result) {

				GCMRegistrar.setRegisteredOnServer(
						TChatApplication.getContext(), false);
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
