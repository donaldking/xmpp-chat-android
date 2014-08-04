package co.uk.tusksolutions.tchat.android.api;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import android.os.AsyncTask;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.models.ChatRoomsModel;
import co.uk.tusksolutions.utility.Utility;

public class APIGetChatRooms {
	JSONArray jsonArray;
	private ChatRoomsModel mChatroomsModel;
	private AsyncApiGetChatrooms mTask = null;
	OnGetChatroomsCompleted callbackObject;

	public void getChatrooms(OnGetChatroomsCompleted callbackObject) {

		if (mTask != null) {
			return;
		}

		this.callbackObject = callbackObject;
		mTask = new AsyncApiGetChatrooms();
		mTask.execute((Void) null);
	}

	/*
	 * Performing Network request
	 */
	private class AsyncApiGetChatrooms extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			boolean apiResult = false;

			HttpGet request = new HttpGet(Constants.HTTP_SCHEME
					+ Constants.CURRENT_SERVER + Constants.GET_CHATROOMS_ENDPOINT
					+ "?username="
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
						mChatroomsModel = new ChatRoomsModel();
						if (mChatroomsModel.saveChatroomsToDB(jsonArray)) {
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
				callbackObject.OnGetChatRoomSuccess();
			} else {
				callbackObject.OnGetChatRoomFailed();
			}
		}

		@Override
		protected void onCancelled() {
			mTask = null;
			return;
		}
	}

	public interface OnGetChatroomsCompleted {
		void OnGetChatRoomSuccess();

		void OnGetChatRoomFailed();
	}
}
