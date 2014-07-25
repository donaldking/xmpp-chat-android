package co.uk.tusksolutions.tchat.android.adapters;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import co.uk.tusksolutions.extensions.TimeAgo;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.models.ChatMessagesModel;
import co.uk.tusksolutions.tchat.android.viewHolders.ChatFromViewHolder;
import co.uk.tusksolutions.tchat.android.viewHolders.ChatToViewHolder;
import co.uk.tusksolutions.utility.URLImageParser;

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
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		/**
		 * Determine the type of row to create based on the "to" field value
		 */
		int rowType;
		if (chatMessagesModelCollection.get(position).receiver
				.equalsIgnoreCase(TChatApplication.getCurrentJid())) {
			rowType = 0;
		} else {
			rowType = 1;
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

			if (chatMessagesModel.message.startsWith("&lt")) {
				/*
				 * Show ImageView in ChatRow @DEEPAK
				 */
				chatFromViewHolder.imagesent.setVisibility(View.VISIBLE);
				chatFromViewHolder.chatMessageTextView.setVisibility(View.GONE);

				/*
                 * Extract image src from uploaded Link @DEEPAK
                 */
				String path = getFirstImage(Html.fromHtml(
						chatMessagesModel.message).toString());

				try {
					UrlImageViewHelper.setUrlDrawable(
							chatFromViewHolder.imagesent, path,
							R.drawable.mondobar_jewel_friends_on);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// chatFromViewHolder.chatMessageTextView.setText(Html.fromHtml(chatMessagesModel.message));
			}

			else {
				chatFromViewHolder.imagesent.setVisibility(View.GONE);
				chatFromViewHolder.chatMessageTextView
						.setVisibility(View.VISIBLE);
				chatFromViewHolder.chatMessageTextView
						.setText(chatMessagesModel.message);
			}
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
			if (chatMessagesModel.message.contains("src=")) {
				/*
				 * Show ImageView in ChatRow @DEEPAK
				 */
				chatToViewHolder.recivedImage.setVisibility(View.VISIBLE);
				
				chatToViewHolder.chatMessageTextView.setVisibility(View.GONE);//Hide TextView Field
                 
				/*
                  * Extract image src from uploaded Link @DEEPAK
                  */
                 
				String path = getFirstImage(chatMessagesModel.message);

				try {
					UrlImageViewHelper.setUrlDrawable(
							chatToViewHolder.recivedImage, path,
							R.drawable.mondobar_jewel_friends_on);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			} else {
				chatToViewHolder.recivedImage.setVisibility(View.GONE);
				chatToViewHolder.chatMessageTextView
						.setVisibility(View.VISIBLE);
				chatToViewHolder.chatMessageTextView
						.setText(chatMessagesModel.message);
			}
			chatToViewHolder.chatMessageTimestampTextView.setText(TimeAgo
					.getTimeAgo(Long.parseLong(chatMessagesModel.timeStamp),
							context));

			break;
		}

		return row;
	}

	private String getFirstImage(String htmlString) {

		if (htmlString == null)
			return null;

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
}
