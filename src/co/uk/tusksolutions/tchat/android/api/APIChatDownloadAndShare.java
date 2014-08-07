package co.uk.tusksolutions.tchat.android.api;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.Html;
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

	public void doChatDownloadAndShare(Activity act, String sender,
			String receiver, Boolean share) {
		this.sender = sender;
		this.receiver = receiver;
		this.mContext = act;
		this.share = share;
		if (mTask != null) {
			return;
		}
		Log.e("sneder and receiver ", "sender " + sender + " receiver "
				+ receiver);
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
			if (share) {
				dialog.setMessage("Please wait..");
			} else {
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
					+ Constants.DOWNLOAD_CHAT_HISTORY_TEXT_ENDPOINT + "?";

			try {
				URL url = new URL(TextFileDownloadURL + "sender=" + sender
						+ "&receiver=" + receiver);
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
						+ receiver + ".txt");

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
				File f = new File(dir.toString() + "/" + receiver + ".txt");
				if (share && (f.exists())) {
					/*
					 * Intent share = new Intent(Intent.ACTION_SEND);
					 * share.setType("text/*");
					 * 
					 * share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
					 * 
					 * share.putExtra(android.content.Intent.EXTRA_SUBJECT,
					 * "Subject Here");
					 * share.putExtra(android.content.Intent.EXTRA_TEXT,
					 * "Yookos Chat Messanger");
					 * mContext.startActivity(Intent.createChooser(share,
					 * "Share Chat"));
					 */
					onShareClick(f);
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

	public void onShareClick(File f) {
		Resources resources = mContext.getResources();

		Intent emailIntent = new Intent();
		emailIntent.setAction(Intent.ACTION_SEND);
		// Native email client doesn't currently support HTML, but it doesn't
		// hurt to try in case they fix it
		emailIntent.putExtra(Intent.EXTRA_TEXT, "Yookos Chat");
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Yookos Chat Conversation");
		emailIntent.setType("message/rfc822");
	
		PackageManager pm = mContext.getPackageManager();
		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		sendIntent.setType("text/plain");

		Intent openInChooser = Intent.createChooser(emailIntent, "Share Chat");

		List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
		List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
		for (int i = 0; i < resInfo.size(); i++) {
			// Extract the label, append it, and repackage it in a LabeledIntent
			ResolveInfo ri = resInfo.get(i);
			String packageName = ri.activityInfo.packageName;
			if (packageName.contains("android.email")) {
				emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
				emailIntent.setPackage(packageName);
			} else if (packageName.contains("android.gm")) {
				Intent intent = new Intent();
				intent.setComponent(new ComponentName(packageName,
						ri.activityInfo.name));
				intent.setAction(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
				intent.putExtra(Intent.EXTRA_TEXT, "Yookos Chat");
				intent.putExtra(Intent.EXTRA_SUBJECT, "Yookos Chat Conversation");
				intent.setType("message/rfc822");
				

				intentList.add(new LabeledIntent(intent, packageName, ri
						.loadLabel(pm), ri.icon));
			}
		}

		// convert intentList to array
		LabeledIntent[] extraIntents = intentList
				.toArray(new LabeledIntent[intentList.size()]);

		openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
		mContext.startActivity(openInChooser);
	}
}
