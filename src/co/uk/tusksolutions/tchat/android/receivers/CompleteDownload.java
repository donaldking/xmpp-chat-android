package co.uk.tusksolutions.tchat.android.receivers;


import java.io.File;
import java.net.URI;


import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.api.APIChatDownloadAndShare;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class CompleteDownload extends BroadcastReceiver {
	public NotificationManager notificationManager;
	public Notification myNotification;

		@SuppressWarnings("deprecation")
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent
					.getAction())) {
			
			Query query = new Query();
	        query.setFilterById(APIChatDownloadAndShare.lastDownload);
	        Cursor c = APIChatDownloadAndShare.mgr.query(query);
	        if (c.moveToFirst()) {
	            int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
	            if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {

	                String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
	                Log.i("Debug", "downloaded file " + uriString); 
	             
	               
	                  
	                Toast.makeText(context, "Download  ",Toast.LENGTH_SHORT).show();
	        		notificationManager = (NotificationManager)context. getSystemService(Context.NOTIFICATION_SERVICE);
	        		myNotification = new Notification(R.drawable.ic_launcher," Yookos chat!", System.currentTimeMillis());
	        	
	        		String notificationTitle = "Download Completed";
	        		Intent myIntent=new Intent(Intent.ACTION_VIEW);
	        		myIntent.setDataAndType(Uri.parse(uriString), "text/plain");
	        		myNotification.icon = R.drawable.ic_launcher;
	        		myNotification.flags |= Notification.FLAG_AUTO_CANCEL;
	        		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,myIntent, Intent.FLAG_ACTIVITY_CLEAR_TASK);
	        		myNotification.setLatestEventInfo(context, notificationTitle,
	        				"Chat History Downlaoad Complete", pendingIntent);
	        		notificationManager.notify(43, myNotification);
	            } 
			}
			
			
			}
		}
		

}
