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
import co.uk.tusksolutions.tchat.android.models.RecentsModel;
import co.uk.tusksolutions.utility.Utility;

public class APIRecents {
	JSONArray jsonArray;
	private RecentsModel mRecentsModel;
	private AsyncApiRecents mTask = null;

	public void getRecents() {

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

			/**
			 * Log.d("APIRecents", "BASE: " + Constants.HTTP_SCHEME +
			 * Constants.CURRENT_SERVER + Constants.RECENTS_ENDPOINT +
			 * "?sender="+ TChatApplication.getUserModel().getUsername());
			 */

			HttpGet request = new HttpGet(Constants.HTTP_SCHEME
					+ Constants.CURRENT_SERVER + Constants.RECENTS_ENDPOINT
					+ "?sender="
					+ TChatApplication.getUserModel().getUsername());

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
						mRecentsModel = new RecentsModel();
						if (mRecentsModel.saveRecentsToDB(jsonArray)) {
							getStreamMediaResult = true;
						}

					} else {
						getStreamMediaResult = false;
					}
				}
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
						new Intent(Constants.RECENTS_UPDATED));
			} else {
				TChatApplication.getContext().sendBroadcast(
						new Intent(Constants.RECENTS_EMPTY));
			}
		}

		@Override
		protected void onCancelled() {
			mTask = null;
			return;
		}
	}
}
