package co.uk.tusksolutions.tchat.android.viewHolders;

import android.view.View;
import android.widget.ImageView;
import co.uk.tusksolutions.extensions.RobotoLightTextView;
import co.uk.tusksolutions.extensions.RobotoRegularTextView;
import co.uk.tusksolutions.tchat.android.R;

public class GroupsViewHolder {
	public RobotoRegularTextView groupName;
	
	public ImageView grouprosterAvatar;
	
	public RobotoLightTextView grouplastMessage;
	public RobotoLightTextView grouplastMessageTimestamp;
	public GroupsViewHolder(View v) {
		
		groupName = (RobotoRegularTextView) v.findViewById(R.id.group_name);
		grouprosterAvatar = (ImageView) v.findViewById(R.id.group_roster_avatar);
	
		
		grouplastMessage = (RobotoLightTextView) v.findViewById(R.id.group_last_message);
		grouplastMessage.setVisibility(View.GONE);
		
		grouplastMessageTimestamp = (RobotoLightTextView) v.findViewById(R.id.group_last_message_timestamp);
	  grouplastMessageTimestamp.setVisibility(View.GONE);
	}
}
