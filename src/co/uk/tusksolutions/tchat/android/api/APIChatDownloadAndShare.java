package co.uk.tusksolutions.tchat.android.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.constants.Constants;

public class APIChatDownloadAndShare {

	String sender, receiver;
	Activity mContext;
	Boolean share;

	private AsyncApiChatDownload mTask = null;
	File dir;
	public static DownloadManager mgr = null;
	public static long lastDownload = -1L;

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

		mgr = (DownloadManager) act.getSystemService(Context.DOWNLOAD_SERVICE);

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
			/*
			 * dialog = new ProgressDialog(mContext); if (share) {
			 * dialog.setMessage("Please wait.."); } else {
			 * dialog.setMessage("Downloading Chat History"); } dialog.show();
			 */
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean apiResult = false;
			int count;
			String TextFileDownloadURL = Constants.HTTP_SCHEME
					+ Constants.CURRENT_SERVER
					+ Constants.DOWNLOAD_CHAT_HISTORY_TEXT_ENDPOINT + "?";

			Uri uri = Uri.parse(TextFileDownloadURL + "sender=" + sender
					+ "&receiver=" + receiver);

			try {
				startDownload(uri, receiver + ".txt");
				apiResult = true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				apiResult = false;
			}

			/*
			 * try {
			 * 
			 * Uri uri=Uri.parse(TextFileDownloadURL + "sender=" + sender +
			 * "&receiver=" + receiver);
			 * 
			 * 
			 * URL url = new URL(TextFileDownloadURL + "sender=" + sender +
			 * "&receiver=" + receiver);
			 * 
			 * URLConnection conection = url.openConnection();
			 * conection.connect(); // download the file InputStream input = new
			 * BufferedInputStream(url.openStream(), 8192); dir = new
			 * File(Environment.getExternalStorageDirectory() .toString() +
			 * "/YookosChat/Media/"); try { if (dir.mkdirs()) {
			 * System.out.println("Directory created"); } else {
			 * System.out.println("Directory is not created"); } } catch
			 * (Exception e) { e.printStackTrace(); } // Output stream
			 * OutputStream output = new FileOutputStream(dir.toString() + "/" +
			 * receiver + ".txt");
			 * 
			 * byte data[] = new byte[1024];
			 * 
			 * while ((count = input.read(data)) != -1) { // writing data to
			 * file output.write(data, 0, count); }
			 * 
			 * // flushing output output.flush();
			 * 
			 * // closing streams output.close(); input.close(); apiResult =
			 * true;
			 * 
			 * } catch (Exception e) { Log.e("Error: ", e.getMessage());
			 * apiResult = false; }
			 */

			return apiResult;

		}

		@Override
		protected void onPostExecute(Boolean result) {
			mTask = null;
			// dialog.dismiss();
			if (result) {
				/*
				 * File f = new File(dir.toString() + "/" + receiver + ".txt");
				 * if (share && (f.exists())) { onShareClick(f); }
				 */

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

	private void startDownload(Uri uri, String filepath) {

		Boolean isSDPresent = android.os.Environment.getExternalStorageState()
				.equals(android.os.Environment.MEDIA_MOUNTED);

		if (isSDPresent) {
			DownloadManager.Request req = new DownloadManager.Request(uri);

			req.setAllowedNetworkTypes(
					DownloadManager.Request.NETWORK_WIFI
							| DownloadManager.Request.NETWORK_MOBILE)
					.setAllowedOverRoaming(false).setTitle("Downloding..  ")
					.setDescription(filepath)
					.setDestinationInExternalPublicDir("/yookoschat", filepath);

			lastDownload = mgr.enqueue(req);
			// queryStatus();
		}
	}

	public void onShareClick(File f) {

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
				intent.putExtra(Intent.EXTRA_SUBJECT,
						"Yookos Chat Conversation");
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
