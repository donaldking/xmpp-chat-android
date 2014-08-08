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
import co.uk.tusksolutions.tchat.android.models.ChatRoomsModel;
import co.uk.tusksolutions.utility.Utility;

public class APIGetChatroomparticipants {
	JSONArray jsonArray;
	
	private AsyncApiGetChatroomParticipants mTask = null;
    String room_id;
    OnGetparticipantsCount callbackObject;
    ChatRoomsModel chatRoomsModel;
	public void doGetChatroomparticipants(String room_id,OnGetparticipantsCount getparticipantsCount) {

		if (mTask != null) {
			return;
		}
		this.room_id=room_id;
        this.callbackObject=getparticipantsCount;
		mTask = new AsyncApiGetChatroomParticipants();
		mTask.execute((Void) null);
	}

	/*
	 * Performing Network request
	 */
	private class AsyncApiGetChatroomParticipants extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			boolean apiResult = false;

			HttpGet request = new HttpGet(Constants.HTTP_SCHEME
					+ Constants.CURRENT_SERVER + Constants.GET_CHAT_ROOM_PARTICIPANTS_ENDPOINT
					+ "?room_id="
					+ room_id);

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
					    {
					    	
					    chatRoomsModel=new ChatRoomsModel();
					    chatRoomsModel.updateParticipantsinChatroom(room_id, jsonArray);
					    callbackObject.OnGetChatRoomParticipantsSuccess(room_id, jsonArray.length());
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
				
			} else {
				
			}
		}

		@Override
		protected void onCancelled() {
			mTask = null;
			return;
		}
	}

	public interface OnGetparticipantsCount {
		void OnGetChatRoomParticipantsSuccess(String room_id,int count);

		
	}
}
