package co.uk.tusksolutions.tchat.android.viewHolders;


import android.view.View;
import android.widget.ImageView;
import co.uk.tusksolutions.extensions.CheckableRelativeLayout;
import co.uk.tusksolutions.extensions.RobotoBoldTextView;
import co.uk.tusksolutions.extensions.RobotoRegularTextView;
import co.uk.tusksolutions.tchat.android.R;

public class GroupViewHolder {
	public ImageView rosterAvatar;
	public RobotoRegularTextView rosterName;

	public CheckableRelativeLayout rosterPresenceFrame;

	public GroupViewHolder(View v) {
		rosterPresenceFrame = (CheckableRelativeLayout)v.findViewById(R.id.layout);
		rosterPresenceFrame.setVisibility(View.GONE);
		rosterAvatar = (ImageView) v.findViewById(R.id.roster_avatar);
		rosterName = (RobotoRegularTextView) v.findViewById(R.id.roster_name);
		
	}
}

