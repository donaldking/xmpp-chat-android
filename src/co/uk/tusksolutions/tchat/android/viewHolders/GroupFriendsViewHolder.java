package co.uk.tusksolutions.tchat.android.viewHolders;


import android.view.View;
import android.widget.ImageView;
import co.uk.tusksolutions.extensions.CheckableRelativeLayout;
import co.uk.tusksolutions.extensions.RobotoRegularTextView;
import co.uk.tusksolutions.tchat.android.R;

public class GroupFriendsViewHolder {
	public ImageView rosterAvatar;
	public RobotoRegularTextView rosterName;

	public CheckableRelativeLayout rosterPresenceFrame;

	public GroupFriendsViewHolder(View v) {
		rosterPresenceFrame = (CheckableRelativeLayout)v.findViewById(R.id.layout);
		rosterAvatar = (ImageView) v.findViewById(R.id.roster_avatar);
		rosterName = (RobotoRegularTextView) v.findViewById(R.id.roster_name);
		
	}
}