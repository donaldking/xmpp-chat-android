package co.uk.tusksolutions.tchat.android.api;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.utility.Utility;

public class APIGetLastOnlineTime {

	JSONArray jsonArray;
	public String buddyJid;
	public String lastOnline;
	private AsyncApiGetLastOnlineTime mTask = null;
	OnGetLastOnlineCompleted callbackObject;

	public void doGetLastOnlineTime(String buddyJid,
			OnGetLastOnlineCompleted callbackObject) {

		if (mTask != null) {
			return;
		}

		this.callbackObject = callbackObject;
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
						lastOnline = parseLastOnline(jsonArray.getJSONObject(0));
						Log.e("last online ","last online "+lastOnline);
						if(lastOnline.equalsIgnoreCase("private"))
						{
							
							apiResult=false;
						}
						else if (lastOnline.matches("[0-9]+")
								&& lastOnline.length() >= 1) {
							apiResult = true;
						} else {
							apiResult = false;
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
				callbackObject.OnGetLastOnlineAvailable(lastOnline);
			} else {
				callbackObject.OnGetLastOnlinePrivate();
			}
		}

		@Override
		protected void onCancelled() {
			mTask = null;
			return;
		}
	}

	public String parseLastOnline(JSONObject jsonObject) {
		String lastSeen = null;
		try {
			lastSeen = jsonObject.getString("seconds");
			return lastSeen;

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * Interface to handle last online result
	 * 
	 * @author donaldking
	 * 
	 */
	public interface OnGetLastOnlineCompleted {
		void OnGetLastOnlineAvailable(String lastOnlineTime);

		void OnGetLastOnlinePrivate();
	}
}
