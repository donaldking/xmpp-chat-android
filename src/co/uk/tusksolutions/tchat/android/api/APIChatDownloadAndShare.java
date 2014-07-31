package co.uk.tusksolutions.tchat.android.api;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.constants.Constants;

public class APIChatDownloadAndShare {

	String sender, receiver;
	Activity mContext;
	Boolean share;

	private AsyncApiChatDownload mTask = null;
	File dir;

	public void doChatDownloadAndShare(Activity act, String sender, String receiver,Boolean share) {
		this.sender = sender;
		this.receiver = receiver;
		this.mContext = act;
		this.share=share;
		if (mTask != null) {
			return;
		}
		Log.e("sneder and receiver ","sender "+sender+" receiver "+receiver);
		mTask = new AsyncApiChatDownload();
		mTask.execute((Void) null);

	}

	/*
	 * Performing Network request
	 */
	private class AsyncApiChatDownload extends AsyncTask<Void, Void, Boolean> {
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog = new ProgressDialog(mContext);
			if(share)
			{
				dialog.setMessage("Please wait..");
			}
			else
			{
			dialog.setMessage("Downloading Chat History");
			}
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean apiResult = false;
			int count;
			String TextFileDownloadURL = Constants.HTTP_SCHEME
					+ Constants.CURRENT_SERVER
					+ Constants.DOWNLOAD_CHAT_HISTORY_TEXT_ENDPOINT+"?";

			try {
				URL url = new URL(TextFileDownloadURL + "sender=" + sender
						+ "&receiver="+receiver);
				URLConnection conection = url.openConnection();
				conection.connect();
				// download the file
				InputStream input = new BufferedInputStream(url.openStream(),
						8192);
				dir = new File(Environment.getExternalStorageDirectory()
						.toString() + "/YookosChat/Media/");
				try {
					if (dir.mkdirs()) {
						System.out.println("Directory created");
					} else {
						System.out.println("Directory is not created");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				// Output stream
				OutputStream output = new FileOutputStream(dir.toString() + "/"
						+ receiver+ ".txt");

				byte data[] = new byte[1024];

				while ((count = input.read(data)) != -1) {
					// writing data to file
					output.write(data, 0, count);
				}

				// flushing output
				output.flush();

				// closing streams
				output.close();
				input.close();
				apiResult = true;

			} catch (Exception e) {
				Log.e("Error: ", e.getMessage());
				apiResult = false;
			}

			return apiResult;

		}

		@Override
		protected void onPostExecute(Boolean result) {
			mTask = null;
			dialog.dismiss();
			if (result) {
				File f = new File(dir.toString()+"/"+receiver+".txt");
				if (share&&(f.exists())) {
					Intent share = new Intent(Intent.ACTION_SEND);
					share.setType("text/*");

					share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
					share.putExtra(Intent.EXTRA_TEXT,
							"" + mContext.getString(R.string.app_name));

					mContext.startActivity(Intent.createChooser(share,
							"Share Chat"));
				}

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
