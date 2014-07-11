package co.uk.tusksolutions.tchat.android.viewHolders;


import android.view.View;
import android.widget.ImageView;
import co.uk.tusksolutions.extensions.RobotoBoldTextView;
import co.uk.tusksolutions.extensions.RobotoRegularTextView;
import co.uk.tusksolutions.tchat.android.R;

public class GroupViewHolder {
	public ImageView rosterAvatar;
	public RobotoRegularTextView rosterName;

	public View rosterPresenceFrame;

	public GroupViewHolder(View v) {
		rosterPresenceFrame = v.findViewById(R.id.roster_presence_frame);
		rosterPresenceFrame.setVisibility(View.GONE);
		rosterAvatar = (ImageView) v.findViewById(R.id.roster_avatar);
		rosterName = (RobotoRegularTextView) v.findViewById(R.id.roster_name);
		
	}
}

