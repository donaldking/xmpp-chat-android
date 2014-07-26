package co.uk.tusksolutions.tchat.android.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.jivesoftware.smack.util.StringUtils;

import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.xmpp.XMPPChatMessageManager;
import co.uk.tusksolutions.utility.Utility;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

@SuppressWarnings("deprecation")
public class APIPostFile {
	String sender;
	String receiver;
	String selectedFile;
	String buddyName;

	private APIPostFileTask mTask = null;

	public void doPostFile(String sender, String receiver, String selectedFile,String buddyName) {

		if (mTask != null) {
			return;
		}
		this.sender = sender;
		this.receiver = receiver;
		this.selectedFile = selectedFile;
		this.buddyName=buddyName;

		mTask = new APIPostFileTask();
		mTask.execute((Void) null);
	}

	private class APIPostFileTask extends AsyncTask<Void, Void, Boolean> {

		
		String link;
		@Override
		protected Boolean doInBackground(Void... params) {

			boolean apiResult = false;

			try {
				File file = new File(selectedFile);

				try {
					link = UploadFile(sender.replace("@"+Constants.CURRENT_SERVER,""), receiver.replace("@"+Constants.CURRENT_SERVER,""), file);
					Log.e("upload file link ", link);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				apiResult = true;

			} catch (Exception e) {
				apiResult = false;
			}
			return apiResult;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			mTask = null;

			if (result) {
				
				XMPPChatMessageManager.sendMessage(receiver, buddyName,
						link, 0, "text");
				// Save to cloud
				APICloudStorage cloudStorage = new APICloudStorage();
				cloudStorage.saveToCloud(TChatApplication.getUserModel()
						.getUsername(), receiver,
						link, "none", 0, "CHAT");

			} else {

			}
		}

		@Override
		protected void onCancelled() {
			mTask = null;
			return;
		}
	}

	@SuppressWarnings("deprecation")
	public String UploadFile(String sender, String receiver, File filepath)
			throws UnsupportedEncodingException {
		String responsed = null;
		Log.e("File Uploaded", "In file upload " + filepath.toString());
		DefaultHttpClient httpclient;
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
				HttpVersion.HTTP_1_1);
		httpclient = new DefaultHttpClient(params);
		HttpPost httpPost = new HttpPost(Constants.HTTP_SCHEME
				+ Constants.CURRENT_SERVER + Constants.UPLOAD_FILE_ENDPOINT);

		MultipartEntity builder = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

		builder.addPart("sender", new StringBody(sender));
		builder.addPart("receiver", new StringBody(receiver));

		if (filepath != null && filepath.exists())
			builder.addPart("upfile", new FileBody(filepath));

		httpPost.setEntity(builder);

		HttpResponse httpResponse = null;
		try {
			httpResponse = httpclient.execute(httpPost);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			httpResponse = httpclient.execute(httpPost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		InputStream inputStream = null;
		try {
			inputStream = httpResponse.getEntity().getContent();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Utility utility = new Utility();
		if (inputStream != null)

			responsed = utility.convertStreamToString(inputStream);
		System.out.println("Response: " + responsed);
		return responsed;
	}

}
