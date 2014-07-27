package co.uk.tusksolutions.tchat.android.viewHolders;

import android.view.View;
import android.widget.ImageView;
import co.uk.tusksolutions.extensions.RobotoLightTextView;
import co.uk.tusksolutions.tchat.android.R;

public class GroupChatFromImageViewHolder {

	public ImageView imageReceived;
	public RobotoLightTextView chatMessageFromUser;
	public RobotoLightTextView chatMessageTimestampTextView;

	public GroupChatFromImageViewHolder(View v) {

		imageReceived = (ImageView) v.findViewById(R.id.imageReceived);
		chatMessageFromUser = (RobotoLightTextView) v
				.findViewById(R.id.chat_to_user);
		chatMessageTimestampTextView = (RobotoLightTextView) v
				.findViewById(R.id.chat_to_timestamp);
	}
}
