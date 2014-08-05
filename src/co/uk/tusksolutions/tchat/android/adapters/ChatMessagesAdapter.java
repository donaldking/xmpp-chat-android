package co.uk.tusksolutions.tchat.android.adapters;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


import org.apache.http.util.ByteArrayBuffer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Toast;
import co.uk.tusksolutions.extensions.TimeAgo;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.models.ChatMessagesModel;
import co.uk.tusksolutions.tchat.android.tasks.DownloadFilesTask;
import co.uk.tusksolutions.tchat.android.viewHolders.ChatFromImageViewHolder;
import co.uk.tusksolutions.tchat.android.viewHolders.ChatFromViewHolder;
import co.uk.tusksolutions.tchat.android.viewHolders.ChatToImageViewHolder;
import co.uk.tusksolutions.tchat.android.viewHolders.ChatToViewHolder;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;


/**
 * Created by donaldking on 27/06/2014.
 */
public class ChatMessagesAdapter extends BaseAdapter {
	private Context context;
	private ChatMessagesModel mModel;
	private ArrayList<ChatMessagesModel> chatMessagesModelCollection;

	public ChatMessagesAdapter(String to, String from, int action, long id) {
		this.context = TChatApplication.getContext();
		mModel = new ChatMessagesModel();

		switch (action) {
		case 1:
			chatMessagesModelCollection = mModel.getAllMessagesFromDB(to, from);
			notifyDataSetChanged();
			break;
		}
	}

	@Override
	public int getCount() {
		return chatMessagesModelCollection.size();
	}

	@Override
	public Object getItem(int position) {
		return chatMessagesModelCollection.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getViewTypeCount() {
		return 4;
	}

	@Override
	public int getItemViewType(int position) {
		/**
		 * Determine the type of row to create based on the "to" field value
		 */
		int rowType;
		String message = null;
		try {
			message = chatMessagesModelCollection.get(position).message;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
         if((message!=null)&&message.contains("href="))
         {
        	 if(chatMessagesModelCollection.get(position).receiver.equalsIgnoreCase(TChatApplication.getCurrentJid()))
        	 {
        		 rowType=1;
        	 }
        	 else
        	 {
        		 rowType=0;
        	 }
        	 
        	 
         }
       
		
         else if ((message != null)
				&& chatMessagesModelCollection.get(position).receiver
						.equalsIgnoreCase(TChatApplication.getCurrentJid())) {
			if (chatMessagesModelCollection.get(position).message
					.contains("src")) {
			
				rowType = 3;
			} else {
				rowType = 0;
			}
		} else if ((message != null)
				&& (message.contains("src="))
				&& !(chatMessagesModelCollection.get(position).receiver
						.equalsIgnoreCase(TChatApplication.getCurrentJid()))) {
			rowType = 2;
		} else {
			if ((message != null)
					&& (chatMessagesModelCollection.get(position).messageType != null)
					&& chatMessagesModelCollection.get(position).messageType
							.toString().equalsIgnoreCase("FileTransfer")) {
				rowType = 2;
			} else {
				rowType = 1;
			}
		}
		return rowType;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ChatToViewHolder chatToViewHolder = null;
		ChatFromViewHolder chatFromViewHolder = null;
		ChatToImageViewHolder chatToImageViewHolder = null;
		ChatFromImageViewHolder chatFromImageViewHolder = null;

		/**
		 * Get result from Model Query
		 * 
		 */
		final ChatMessagesModel chatMessagesModel = chatMessagesModelCollection
				.get(position);

		int type = getItemViewType(position);

		switch (type) {
		case 0:
			// I am the sender!
			if (row == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.chat_from_row, parent, false);

				chatFromViewHolder = new ChatFromViewHolder(row);
				row.setTag(chatFromViewHolder);

			} else {
				chatFromViewHolder = (ChatFromViewHolder) row.getTag();
			}
			if ((chatMessagesModel.message!=null)&&chatMessagesModel.message.contains("href=")) {
				chatFromViewHolder.chatMessageTextView
				.setText((Html.fromHtml(chatMessagesModel.message)));
			}
			else
			{
			chatFromViewHolder.chatMessageTextView
					.setText(chatMessagesModel.message);
			}
			
			
			row.setOnClickListener(new OnClickListener() { // Call Chat Page when
				// pressed
				@Override
				public void onClick(View arg0) {

					try {

						String LinkMessage = chatMessagesModel.message;
	                    	if (LinkMessage.contains("href")) {
							
	                    		 DownloadFilesTask downloadFilesTask=new DownloadFilesTask();
	  	                       downloadFilesTask.dodownloadFile(context,getDownloadUrl(LinkMessage),Html.fromHtml(LinkMessage).toString());
						} 
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			// }
			chatFromViewHolder.chatMessageTimestampTextView.setText(TimeAgo
					.getTimeAgo(Long.parseLong(chatMessagesModel.timeStamp),
							context));

			break;
		case 1:
			// Buddy is the sender!

			if (row == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.chat_to_row, parent, false);

				chatToViewHolder = new ChatToViewHolder(row);
				row.setTag(chatToViewHolder);

			} else {
				chatToViewHolder = (ChatToViewHolder) row.getTag();
			}
          
			
		
			if ((chatMessagesModel.message!=null)&&chatMessagesModel.message.contains("href=")) {
				// Spanned spanned = Html.fromHtml(a);

				Log.e("In ChatListAdaptter", chatMessagesModel.message);

				chatToViewHolder.chatMessageTextView.setText(Html.fromHtml(chatMessagesModel.message));
			} 
			else
			{
			chatToViewHolder.chatMessageTextView.setText(chatMessagesModel.message);
			}
			chatToViewHolder.chatMessageTimestampTextView.setText(TimeAgo
					.getTimeAgo(Long.parseLong(chatMessagesModel.timeStamp),context));
			
			row.setOnClickListener(new OnClickListener() { // Call Chat Page when
				// pressed
				@Override
				public void onClick(View arg0) {

					try {

						String LinkMessage = chatMessagesModel.message;
	                     if (LinkMessage.contains("href")) {
						
	                    	 DownloadFilesTask downloadFilesTask=new DownloadFilesTask();
	                       downloadFilesTask.dodownloadFile(context,getDownloadUrl(LinkMessage),Html.fromHtml(LinkMessage).toString());
	                     } 
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			break;

		case 2:
			// Images sent to server

			if (row == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.chat_to_image_row, parent,
						false);

				chatToImageViewHolder = new ChatToImageViewHolder(row);
				row.setTag(chatToImageViewHolder);

			} else {
				chatToImageViewHolder = (ChatToImageViewHolder) row.getTag();

			}
			chatToImageViewHolder.imagesent.setVisibility(View.VISIBLE);
			String ImagePath = chatMessagesModel.message;
			File imgFile = new File(ImagePath);
			if (imgFile.exists()) {
                       Toast.makeText(TChatApplication.getContext(), "File Exists "+ImagePath, Toast.LENGTH_SHORT).show();
                      Log.v("Image ","Image exist "+ImagePath); 
                       
				try {
					Bitmap myBitmap = decodeScaledBitmapFromSdCard(ImagePath,
							200, 200);

					chatToImageViewHolder.imagesent.setImageBitmap(myBitmap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				String path1 = getFirstImage(chatMessagesModel.message);

				try {
					UrlImageViewHelper.setUrlDrawable(
							chatToImageViewHolder.imagesent, path1,
							R.drawable.camera_focus_box);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// chatToImageViewHolder.imagesent.setVisibility(View.GONE);

			}

			chatToImageViewHolder.chatMessageTimestampTextView.setText(TimeAgo
					.getTimeAgo(Long.parseLong(chatMessagesModel.timeStamp),
							context));
			break;
		case 3:
			if (row == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.chat_from_image_row, parent,
						false);

				chatFromImageViewHolder = new ChatFromImageViewHolder(row);
				row.setTag(chatFromImageViewHolder);

			} else {
				chatFromImageViewHolder = (ChatFromImageViewHolder) row
						.getTag();

			}
			String path1 = getFirstImage(chatMessagesModel.message);

			try {
				UrlImageViewHelper.setUrlDrawable(
						chatFromImageViewHolder.imageReceived, path1,
						R.drawable.camera_focus_box);
			} catch (Exception e) {
				e.printStackTrace();
			}

			chatFromImageViewHolder.chatMessageTimestampTextView
					.setText(TimeAgo.getTimeAgo(
							Long.parseLong(chatMessagesModel.timeStamp),
							context));

			break;

		}

		return row;
	}

	private String getFirstImage(String htmlString) {

		if (htmlString == null)
			return null;
		if (htmlString.startsWith("&lt")) {
			htmlString = Html.fromHtml(htmlString).toString();
		}

		String img = "";
		Document doc = Jsoup.parse(htmlString);
		Elements imgs = doc.getElementsByTag("img");

		for (Element imageElement : imgs) {
			if (imageElement != null) {
				// for each element get the src url
				img = Constants.HTTP_SCHEME
						+ imageElement.attr("src").substring(3);
				return img;
			}
		}

		return null;
	}

	private String getDownloadUrl(String htmlString) {

		if (htmlString == null)
			return null;

		String img = "";
		Document doc = Jsoup.parse(htmlString);
		
		Element element2 = doc.select("a").first(); 
		

		img=Constants.HTTP_SCHEME+element2.attr("href").substring(3);
		return img;

		
	}
	
	public static Bitmap decodeScaledBitmapFromSdCard(String filePath,
			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}
	
	
}
