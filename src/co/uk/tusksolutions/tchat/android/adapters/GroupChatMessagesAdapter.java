package co.uk.tusksolutions.tchat.android.adapters;

import java.io.File;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import co.uk.tusksolutions.extensions.TimeAgo;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.models.ChatMessagesModel;
import co.uk.tusksolutions.tchat.android.models.UserModel;
import co.uk.tusksolutions.tchat.android.tasks.DownloadFilesTask;
import co.uk.tusksolutions.tchat.android.viewHolders.GroupChatFromImageViewHolder;
import co.uk.tusksolutions.tchat.android.viewHolders.GroupChatFromViewHolder;
import co.uk.tusksolutions.tchat.android.viewHolders.GroupChatToImageViewHolder;
import co.uk.tusksolutions.tchat.android.viewHolders.GroupChatToViewHolder;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

/**
 * Created by donaldking on 27/06/2014.
 */
public class GroupChatMessagesAdapter extends BaseAdapter {
	private Context context;
	private ChatMessagesModel mModel;
	private ArrayList<ChatMessagesModel> groupChatMessagesModelCollection;

	public GroupChatMessagesAdapter(String to, String from, int action, long id) {
		this.context = TChatApplication.getContext();
		mModel = new ChatMessagesModel();

		switch (action) {
		case 1:
			
			groupChatMessagesModelCollection = mModel.getAllMessagesFromDB(to,
					from);
			notifyDataSetChanged();
			break;
		}
	}

	@Override
	public int getCount() {
		return groupChatMessagesModelCollection.size();
	}

	@Override
	public Object getItem(int position) {
		return groupChatMessagesModelCollection.get(position);
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
			message = groupChatMessagesModelCollection.get(position).message;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 if((message!=null)&&message.contains("href="))
         {
        	 if(groupChatMessagesModelCollection.get(position).receiver.equalsIgnoreCase(TChatApplication.getCurrentJid()))
        	 {
        		 rowType=1;
        	 }
        	 else
        	 {
        		 rowType=0;
        	 }
        	 
        	 
         }
    	 else if ((message != null)
				&& groupChatMessagesModelCollection.get(position).receiver
						.equalsIgnoreCase(TChatApplication.getCurrentJid())) {
			if (groupChatMessagesModelCollection.get(position).message
					.contains("src")) {
				rowType = 3;
			} else {
				rowType = 0;
			}
		} else if ((message != null)
				&& groupChatMessagesModelCollection.get(position).message
						.contains("src")
				&& !(groupChatMessagesModelCollection.get(position).receiver
						.equalsIgnoreCase(TChatApplication.getCurrentJid()))) {
			rowType = 2;
		} else {
			if ((message != null)
					&& (groupChatMessagesModelCollection.get(position).messageType != null)
					&& groupChatMessagesModelCollection.get(position).messageType
							.toString().equalsIgnoreCase("FileTransfer")) {
				rowType = 2;
			} else {
				rowType = 1;
			}
		}
		Log.e("return row type","rowtype "+rowType);
		return rowType;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		GroupChatToViewHolder groupChatToViewHolder = null;
		GroupChatFromViewHolder groupChatFromViewHolder = null;
		GroupChatToImageViewHolder groupChatToImageViewHolder = null;
		GroupChatFromImageViewHolder groupChatFromImageViewHolder = null;

		/**
		 * Get result from Model Query
		 * 
		 */
		final ChatMessagesModel chatMessagesModel = groupChatMessagesModelCollection
				.get(position);

		int type = getItemViewType(position);

		switch (type) {
		case 0:

			// Buddy is the sender!

			if (row == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.group_chat_from_row, parent,
						false);

				groupChatFromViewHolder = new GroupChatFromViewHolder(row);
				row.setTag(groupChatFromViewHolder);

			} else {
				groupChatFromViewHolder = (GroupChatFromViewHolder) row
						.getTag();
			}

			String nameFrom = TChatApplication.getRosterModel()
					.getBuddyName(
							chatMessagesModel.resource + "@"
									+ Constants.CURRENT_SERVER);

			if (nameFrom == null) {
				nameFrom = chatMessagesModel.resource;
			}

			groupChatFromViewHolder.chatMessageFromUser.setText(nameFrom);
			
			
			if ((chatMessagesModel.message!=null)&&chatMessagesModel.message.contains("href=")) {
				groupChatFromViewHolder.chatMessageTextView.setTextColor(Color.WHITE);
				
				groupChatFromViewHolder.chatMessageTextView
				.setText((Html.fromHtml(chatMessagesModel.message)));
			}
			else
			{
				groupChatFromViewHolder.chatMessageTextView
				.setText(chatMessagesModel.message);
			}
			
			// }
			groupChatFromViewHolder.chatMessageTimestampTextView
					.setText(TimeAgo.getTimeAgo(
							Long.parseLong(chatMessagesModel.timeStamp),
							context));
			
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
		case 1:

			// I am the sender!

			if (row == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.group_chat_to_row, parent,
						false);

				groupChatToViewHolder = new GroupChatToViewHolder(row);
				row.setTag(groupChatToViewHolder);

			} else {
				groupChatToViewHolder = (GroupChatToViewHolder) row.getTag();
			}

			UserModel um = new UserModel();
			String myName = um.getProfileName();

			if (myName == null) {
				myName = um.getUsername();
			}
			groupChatToViewHolder.chatMessageToUser.setText(myName);
			
			if ((chatMessagesModel.message!=null)&&chatMessagesModel.message.contains("href=")) {
				groupChatToViewHolder.chatMessageTextView.setTextColor(Color.WHITE);
				
				groupChatToViewHolder.chatMessageTextView
				.setText((Html.fromHtml(chatMessagesModel.message)));
			}
			else
			{
				groupChatToViewHolder.chatMessageTextView
				.setText(chatMessagesModel.message);
			}
			

			groupChatToViewHolder.chatMessageTimestampTextView.setText(TimeAgo
					.getTimeAgo(Long.parseLong(chatMessagesModel.timeStamp),
							context));

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
				row = inflater.inflate(R.layout.group_chat_to_image_row,
						parent, false);

				groupChatToImageViewHolder = new GroupChatToImageViewHolder(row);
				row.setTag(groupChatToImageViewHolder);

			} else {
				groupChatToImageViewHolder = (GroupChatToImageViewHolder) row
						.getTag();

			}
			groupChatToImageViewHolder.imagesent.setVisibility(View.VISIBLE);
			UserModel um1 = new UserModel();
			String myName1 = um1.getProfileName();

			if (myName1 == null) {
				myName1 = um1.getUsername();
			}
			groupChatToImageViewHolder.chatMessageToUser.setText(myName1);

			groupChatToImageViewHolder.chatMessageTimestampTextView
					.setText(TimeAgo.getTimeAgo(
							Long.parseLong(chatMessagesModel.timeStamp),
							context));
			String ImagePath = chatMessagesModel.message;
			File imgFile = new File(ImagePath);
			if (imgFile.exists()) {

				try {
					Bitmap myBitmap = decodeScaledBitmapFromSdCard(ImagePath,
							200, 200);

					groupChatToImageViewHolder.imagesent
							.setImageBitmap(myBitmap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				String path1 = getFirstImage(chatMessagesModel.message);

				try {
					UrlImageViewHelper.setUrlDrawable(
							groupChatToImageViewHolder.imagesent, path1,
							R.drawable.camera_focus_box);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// Add name of sender (me)
				UserModel ium = null;
				String imageFromName = null;
				try {
					ium = new UserModel();
					imageFromName = ium.getProfileName();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (imageFromName == null) {
					imageFromName = ium.getUsername();
				}

				groupChatToImageViewHolder.chatMessageToUser
						.setText(imageFromName);

			}

			break;
		case 3:
			if (row == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.group_chat_from_image_row,
						parent, false);

				groupChatFromImageViewHolder = new GroupChatFromImageViewHolder(
						row);
				row.setTag(groupChatFromImageViewHolder);

			} else {
				groupChatFromImageViewHolder = (GroupChatFromImageViewHolder) row
						.getTag();

			}

			String nameFrom_image = TChatApplication.getRosterModel()
					.getBuddyName(
							chatMessagesModel.resource + "@"
									+ Constants.CURRENT_SERVER);

			if (nameFrom_image == null) {
				nameFrom_image = chatMessagesModel.resource;
			}

			groupChatFromImageViewHolder.chatMessageFromUser
					.setText(nameFrom_image);
		    Log.e("chat model timestamp ","timestamp "+chatMessagesModel.timeStamp);
			try {
				groupChatFromImageViewHolder.chatMessageTimestampTextView
						.setText(TimeAgo.getTimeAgo(
								Long.parseLong(chatMessagesModel.timeStamp),
								context));
			} catch (NumberFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String path1 = getFirstImage(chatMessagesModel.message);

			try {
				UrlImageViewHelper.setUrlDrawable(
						groupChatFromImageViewHolder.imageReceived, path1,
						R.drawable.camera_focus_box);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Add name of sender (buddy)
			String imageToName = TChatApplication.getRosterModel()
					.getBuddyName(
							chatMessagesModel.resource + "@"
									+ Constants.CURRENT_SERVER);

			if (imageToName == null) {
				imageToName = chatMessagesModel.resource;
			}
			groupChatFromImageViewHolder.chatMessageFromUser
					.setText(imageToName);

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
	private String getDownloadUrl(String htmlString) {

		if (htmlString == null)
			return null;

		String img = "";
		Document doc = Jsoup.parse(htmlString);
		
		Element element2 = doc.select("a").first(); 
		

		img=Constants.HTTP_SCHEME+element2.attr("href").substring(3);
		return img;

		
	}
}
