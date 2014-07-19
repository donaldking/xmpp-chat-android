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
import co.uk.tusksolutions.tchat.android.models.GroupUserModel;
import co.uk.tusksolutions.utility.Utility;

public class APIGetGroups {
	JSONArray jsonArray;
	private GroupUserModel mGroupUserModel;
	private AsyncApiGetProfile mTask = null;

	public void getGroups() {

		if (mTask != null) {
			return;
		}

		mTask = new AsyncApiGetProfile();
		mTask.execute((Void) null);
	}

	/*
	 * Performing Network request
	 */
	private class AsyncApiGetProfile extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			boolean apiResult = false;

			HttpGet request = new HttpGet(Constants.HTTP_SCHEME
					+ Constants.CURRENT_SERVER + Constants.GET_GROUPS_ENDPOINT
					+ "?user_id="
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
						mGroupUserModel = new GroupUserModel();
						if (mGroupUserModel
								.saveGroupsToDB(jsonArray)) {
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
						new Intent(Constants.PROFILE_UPDATED));
			} else {
				TChatApplication.getContext().sendBroadcast(
						new Intent(Constants.PROFILE_NOT_UPDATED));
			}
		}

		@Override
		protected void onCancelled() {
			mTask = null;
			return;
		}
	}
}
