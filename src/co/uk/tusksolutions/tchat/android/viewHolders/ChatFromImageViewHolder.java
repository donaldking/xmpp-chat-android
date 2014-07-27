package co.uk.tusksolutions.tchat.android.viewHolders;

import android.view.View;
import android.widget.ImageView;
import co.uk.tusksolutions.extensions.RobotoLightTextView;
import co.uk.tusksolutions.tchat.android.R;

public class ChatFromImageViewHolder {

	public ImageView imageReceived;
	public RobotoLightTextView chatMessageTimestampTextView;

	public ChatFromImageViewHolder(View v) {
		imageReceived = (ImageView) v.findViewById(R.id.imageReceived);
		chatMessageTimestampTextView = (RobotoLightTextView) v
				.findViewById(R.id.chat_to_timestamp);
	}
}