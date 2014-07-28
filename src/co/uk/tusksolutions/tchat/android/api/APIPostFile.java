package co.uk.tusksolutions.tchat.android.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.activities.ChatActivity;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.models.ChatMessagesModel;
import co.uk.tusksolutions.tchat.android.xmpp.XMPPChatMessageManager;

public class APIPostFile {
	String sender;
	String receiver;
	String selectedFile;
	String buddyName;
	String mid;

	private APIPostFileTask mTask = null;

	Activity act;

	public void doPostFile(String sender, String receiver, String selectedFile,
			String buddyName, Activity act, String mid) {

		if (mTask != null) {
			return;
		}
		this.sender = sender;
		this.receiver = receiver;
		this.selectedFile = selectedFile;
		this.buddyName = buddyName;
		this.act = act;
		this.mid = mid;
		
		mTask = new APIPostFileTask();
		mTask.execute((Void) null);
	}

	private class APIPostFileTask extends AsyncTask<Void, Void, Boolean> {

		String link;

		@Override
		protected Boolean doInBackground(Void... params) {

			boolean apiResult = false;

			try {
				// File file = new File(selectedFile);

				try {

					link = postFile(sender.replace("@"
							+ Constants.CURRENT_SERVER, ""), receiver.replace(
							"@" + Constants.CURRENT_SERVER, ""), selectedFile);

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
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);

		}

		@Override
		protected void onPostExecute(Boolean result) {
			mTask = null;

			if (result) {

				XMPPChatMessageManager.sendMessage(receiver, buddyName, link,
						0, "FileTransfer", mid);
				 
				if(receiver.contains("@conference"))
				{
					receiver=receiver.replace("@conference."+Constants.CURRENT_SERVER, "");
				}
				else
				{
					receiver=receiver.replace("@"+Constants.CURRENT_SERVER, "");
				}
                
				APICloudStorage cloudStorage = new APICloudStorage();
				cloudStorage.saveToCloud(TChatApplication.getUserModel()
						.getUsername(), receiver, link,
						mid, 0, "FileTransfer");

			} else {

			}
		}

		@Override
		protected void onCancelled() {
			mTask = null;
			return;
		}
	}

	public static String postFile(String sender, String receiver,
			String fileName) throws Exception {
		Log.e("File send", fileName);
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(Constants.HTTP_SCHEME
				+ Constants.CURRENT_SERVER + Constants.UPLOAD_FILE_ENDPOINT);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

		final File file = new File(fileName);
		FileBody fb = new FileBody(file);

		builder.addPart("upfile", fb);
		builder.addTextBody("sender", sender);
		builder.addTextBody("receiver", receiver);

		final HttpEntity httpEntity = builder.build();

		class ProgressiveEntity implements HttpEntity {
			@Override
			public void consumeContent() throws IOException {
				httpEntity.consumeContent();
			}

			@Override
			public InputStream getContent() throws IOException,
					IllegalStateException {
				return httpEntity.getContent();
			}

			@Override
			public Header getContentEncoding() {
				return httpEntity.getContentEncoding();
			}

			@Override
			public long getContentLength() {
				return httpEntity.getContentLength();
			}

			@Override
			public Header getContentType() {
				return httpEntity.getContentType();
			}

			@Override
			public boolean isChunked() {
				return httpEntity.isChunked();
			}

			@Override
			public boolean isRepeatable() {
				return httpEntity.isRepeatable();
			}

			@Override
			public boolean isStreaming() {
				return httpEntity.isStreaming();
			} // CONSIDER put a _real_ delegator into here!

			@Override
			public void writeTo(OutputStream outstream) throws IOException {

				class ProxyOutputStream extends FilterOutputStream {
					/**
					 * @author Stephen Colebourne
					 */

					public ProxyOutputStream(OutputStream proxy) {
						super(proxy);
					}

					public void write(int idx) throws IOException {
						out.write(idx);
					}

					public void write(byte[] bts) throws IOException {
						out.write(bts);
					}

					public void write(byte[] bts, int st, int end)
							throws IOException {
						out.write(bts, st, end);
					}

					public void flush() throws IOException {
						out.flush();
					}

					public void close() throws IOException {
						out.close();
					}
				} // CONSIDER import this class (and risk more Jar File Hell)

				class ProgressiveOutputStream extends ProxyOutputStream {
					public ProgressiveOutputStream(OutputStream proxy) {
						super(proxy);
					}

					public void write(byte[] bts, int st, int end)
							throws IOException {

						// FIXME Put your progress bar stuff here!

						out.write(bts, st, end);

					}
				}

				httpEntity.writeTo(new ProgressiveOutputStream(outstream));
			}

		}
		
		ProgressiveEntity progressiveEntity = new ProgressiveEntity();

		post.setEntity(progressiveEntity);
		HttpResponse response = client.execute(post);

		return getContent(response);

	}

	public static String getContent(HttpResponse response) throws IOException {
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		String body = "";
		String content = "";

		while ((body = rd.readLine()) != null) {
			content += body + "\n";
		}
		return content.trim();
	}

	public void saveFile(String to, String message, int isGroupMessage,
			String messageType) {
		try {

			ChatMessagesModel mChatMessageModel = new ChatMessagesModel();
			mChatMessageModel.saveMessageToDB(to,
					TChatApplication.getCurrentJid(), Constants.XMPP_RESOURCE,
					buddyName, message, isGroupMessage, messageType,
					System.currentTimeMillis(), 1, TChatApplication.getMid());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
