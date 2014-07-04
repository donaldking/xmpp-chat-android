package co.uk.tusksolutions.tchat.android.viewHolders;

import android.view.View;
import android.widget.ImageView;
import co.uk.tusksolutions.extensions.RobotoLightTextView;
import co.uk.tusksolutions.extensions.RobotoRegularTextView;
import co.uk.tusksolutions.tchat.android.R;

public class RecentsViewHolder {
	public ImageView rosterAvatar;
	public RobotoRegularTextView rosterName;
	public RobotoLightTextView lastMessage;
	public RobotoLightTextView lastMessageTimestamp;

	public RecentsViewHolder(View v) {
		rosterAvatar = (ImageView) v.findViewById(R.id.roster_avatar);
		rosterName = (RobotoRegularTextView) v.findViewById(R.id.roster_name);
		lastMessage = (RobotoLightTextView) v.findViewById(R.id.last_message);
		lastMessageTimestamp = (RobotoLightTextView) v.findViewById(R.id.last_message_timestamp);
	}
}
