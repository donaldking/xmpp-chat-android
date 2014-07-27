package co.uk.tusksolutions.tchat.android.viewHolders;

import android.view.View;
import android.widget.ImageView;
import co.uk.tusksolutions.extensions.RobotoLightTextView;
import co.uk.tusksolutions.tchat.android.R;

public class GroupChatToImageViewHolder {

	public ImageView imagesent;
	// public ProgressBar bar;
	public RobotoLightTextView chatMessageToUser;
	public RobotoLightTextView chatMessageTimestampTextView;

	public GroupChatToImageViewHolder(View v) {

		imagesent = (ImageView) v.findViewById(R.id.imageSent);
		// bar=(ProgressBar)v.findViewById(R.id.upload_progress);
		chatMessageToUser = (RobotoLightTextView) v
				.findViewById(R.id.chat_to_user);
		chatMessageTimestampTextView = (RobotoLightTextView) v
				.findViewById(R.id.chat_to_timestamp);
	}
}