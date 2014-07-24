package co.uk.tusksolutions.tchat.android.adapters;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
			
			if(chatMessagesModel.message.contains("src="))
			{
				URLImageParser p = new URLImageParser(chatFromViewHolder.chatMessageTextView, TChatApplication.getContext());
		
				Document doc = Jsoup.parse(chatMessagesModel.message);
				
				Element element2 = doc.select("img").first(); // Get the
																// anchor
																// tag
																// element
			

				String path = Constants.HTTP_SCHEME
						+ element2.attr("src").substring(3).toString();
				Drawable d=p.getDrawable(path);
				chatFromViewHolder.chatMessageTextView.setBackgroundDrawable(d);
			}
			
			else
			{
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
			if(chatMessagesModel.message.contains("src="))
			{
				URLImageParser p = new URLImageParser(chatToViewHolder.chatMessageTextView, TChatApplication.getContext());
		
				Document doc = Jsoup.parse(chatMessagesModel.message);
				
				Element element2 = doc.select("img").first(); // Get the
																// anchor
																// tag
																// element
			

				String path = Constants.HTTP_SCHEME
						+ element2.attr("src").substring(3).toString();
				Drawable d=p.getDrawable(path);
				chatToViewHolder.chatMessageTextView.setBackgroundDrawable(d);
			}
			else
			{
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
}
