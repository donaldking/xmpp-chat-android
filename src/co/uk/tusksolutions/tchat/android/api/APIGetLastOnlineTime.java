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
import co.uk.tusksolutions.tchat.android.models.RosterModel;
import co.uk.tusksolutions.utility.Utility;

public class APIGetLastOnlineTime {

	JSONArray jsonArray;
	private RosterModel mRosterModel;
	public String buddyJid;
	private AsyncApiGetLastOnlineTime mTask = null;

	public void doGetLastOnlineTime(String buddyJid) {

		if (mTask != null) {
			return;
		}

		this.buddyJid = buddyJid;
		mTask = new AsyncApiGetLastOnlineTime();
		mTask.execute((Void) null);
	}

	/*
	 * Performing Network request
	 */
	private class AsyncApiGetLastOnlineTime extends
			AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			boolean apiResult = false;

			HttpGet request = new HttpGet(Constants.HTTP_SCHEME
					+ Constants.CURRENT_SERVER
					+ Constants.GET_LAST_ONLINE_TIME_ENDPOINT + "?username="
					+ buddyJid);

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
						mRosterModel = new RosterModel();
						if (mRosterModel.updateLastOnline(
								jsonArray.getJSONObject(0), buddyJid)) {
							apiResult = true;
						}

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
				 TChatApplication.getContext().sendBroadcast(
				new Intent(Constants.LAST_ONLINE_TIME_STATE_CHANGED));
			}
		}

		@Override
		protected void onCancelled() {
			mTask = null;
			return;
		}
	}

}
