package co.uk.tusksolutions.tchat.android.viewHolders;

import android.view.View;
import android.widget.ImageView;
import co.uk.tusksolutions.extensions.RobotoLightTextView;
import co.uk.tusksolutions.extensions.RobotoRegularTextView;
import co.uk.tusksolutions.tchat.android.R;

/**
 * Created by donaldking on 27/06/2014.
 */
public class ChatToViewHolder {

	public RobotoRegularTextView chatMessageTextView;
	public RobotoLightTextView chatMessageTimestampTextView;

	public ImageView recivedImage;

	public ChatToViewHolder(View v) {
		chatMessageTextView = (RobotoRegularTextView) v
				.findViewById(R.id.chat_to_text_view);
		chatMessageTimestampTextView = (RobotoLightTextView) v
				.findViewById(R.id.chat_to_timestamp);
		recivedImage = (ImageView) v
				.findViewById(R.id.chatListItemReceivedTransferIcon);

	}
}
