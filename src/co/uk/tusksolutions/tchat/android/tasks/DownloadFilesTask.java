package co.uk.tusksolutions.tchat.android.tasks;

import java.io.File;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

public class DownloadFilesTask {
	Uri url;
	String name;
	Context context;
	private DownloadFile mTask = null;
	public static  DownloadManager mgr = null;
	public static  long lastDownload = -1L;

	public void dodownloadFile(Context ctx, String url, String name) {
		if (mTask != null) {
			return;
		}
		this.url = Uri.parse(url);
		this.name = name;
  this.context=ctx;
  mgr = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
		mTask = new DownloadFile();
		mTask.execute((Void) null);

	}

	private class DownloadFile extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean apiResult = false;

			File dir = new File(Environment.getExternalStorageDirectory()
					.toString());
			if (!dir.exists())
				dir.mkdirs();

			try {
				//DownloadFile(url, name);
				startDownload(url, name);
				apiResult = true;
			} catch (Exception e) {
				// TODO Auto-generated catch block

				apiResult = false;
			}
			return apiResult;

		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
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

	
	
private void startDownload(Uri uri, String filepath) {
		
		Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

		if(isSDPresent)
		{
			DownloadManager.Request req = new DownloadManager.Request(uri);

			req.setAllowedNetworkTypes(
					DownloadManager.Request.NETWORK_WIFI
							| DownloadManager.Request.NETWORK_MOBILE)
					.setAllowedOverRoaming(false).setTitle("Downloding..  ")
					.setDescription(filepath)
					.setDestinationInExternalPublicDir("/yookoschat", filepath);
			
			lastDownload = mgr.enqueue(req);
			//queryStatus();
		}
	}
}
