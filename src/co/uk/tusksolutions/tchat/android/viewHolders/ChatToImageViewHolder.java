package co.uk.tusksolutions.tchat.android.viewHolders;

import android.view.View;
import android.widget.ImageView;
import co.uk.tusksolutions.extensions.RobotoLightTextView;
import co.uk.tusksolutions.tchat.android.R;

public class ChatToImageViewHolder {

	public ImageView imagesent;
	// public ProgressBar bar;
	public RobotoLightTextView chatMessageTimestampTextView;

	public ChatToImageViewHolder(View v) {

		imagesent = (ImageView) v.findViewById(R.id.imageSent);
		// bar=(ProgressBar)v.findViewById(R.id.upload_progress);
		chatMessageTimestampTextView = (RobotoLightTextView) v
				.findViewById(R.id.chat_to_timestamp);
	}
}