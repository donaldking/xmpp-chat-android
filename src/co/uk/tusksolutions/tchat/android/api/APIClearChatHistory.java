package co.uk.tusksolutions.tchat.android.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.constants.Constants;

public class APIClearChatHistory {

	String sender, receiver;
	Activity mContext;

	private AsyncApiClearChat mTask = null;

	public void doClearChatHistory(Activity act, String sender, String receiver) {
		this.sender = sender;
		this.receiver = receiver;
		this.mContext = act;
		if (mTask != null) {
			return;
		}
		mTask = new AsyncApiClearChat();
		mTask.execute((Void) null);

	}

	/*
	 * Performing Network request
	 */
	private class AsyncApiClearChat extends AsyncTask<Void, Void, Boolean> {
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog = new ProgressDialog(mContext);
			dialog.setMessage("Clearing Chat History");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean apiResult = false;

			HttpPost httpPost = new HttpPost(Constants.HTTP_SCHEME
					+ Constants.CURRENT_SERVER + Constants.CLEAR_CHAT_ENDPOINT);

			HttpClient httpclient = new DefaultHttpClient();

			try {

				List<BasicNameValuePair> postParams = new ArrayList<BasicNameValuePair>();
				postParams.add(new BasicNameValuePair("sender", sender));
				postParams.add(new BasicNameValuePair("receiver", receiver));
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
			dialog.dismiss();
			if (result) {

				Toast.makeText(TChatApplication.getContext(),
						"Chat History Cleared", Toast.LENGTH_SHORT).show();

				TChatApplication.getContext().sendBroadcast(
						new Intent(Constants.MESSAGE_READY));
			} else {
				TChatApplication.getContext().sendBroadcast(
						new Intent(Constants.CHAT_MESSAGE_EMPTY));
			}
		}

		@Override
		protected void onCancelled() {
			mTask = null;
			return;
		}
	}
}
