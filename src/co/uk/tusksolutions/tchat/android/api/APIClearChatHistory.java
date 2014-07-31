package co.uk.tusksolutions.tchat.android.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.constants.Constants;

public class APIClearChatHistory {

	public String sender, receiver;
	public Context mContext;

	private AsyncApiClearChat mTask = null;
	private ProgressDialog progressDialog;
	int type = 0;

	public void doClearChatHistory(final Context context, String sender,
			String receiver) {
		this.sender = sender;
		this.receiver = receiver;
		this.mContext = context;
		if (mTask != null) {
			return;
		}
		// Check to see if receiver is group so we can attach the type
		// parameter = 1

		if (receiver.contains("@conference.")) {
			type = 1;
		}

		mTask = new AsyncApiClearChat();
		mTask.execute((Void) null);

	}

	/*
	 * Performing Network request
	 */
	private class AsyncApiClearChat extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {

			super.onPreExecute();

			if (type == 0) {
				progressDialog = new ProgressDialog(mContext);
				progressDialog.setMessage("Clearing Chat History");
				progressDialog.show();
				progressDialog.setCancelable(false);
			}
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
				postParams.add(new BasicNameValuePair("type", Integer
						.toString(type)));
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

			if (type == 0) {
				if (progressDialog != null) {
					progressDialog.dismiss();
				}

			}
			if (result) {

				// Toast.makeText(TChatApplication.getContext(),
				// "Chat History Cleared", Toast.LENGTH_SHORT).show();

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
