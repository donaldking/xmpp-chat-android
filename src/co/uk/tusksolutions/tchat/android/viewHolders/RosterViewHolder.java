package co.uk.tusksolutions.tchat.android.viewHolders;

import android.view.View;
import android.widget.ImageView;
import co.uk.tusksolutions.extensions.RobotoBoldTextView;
import co.uk.tusksolutions.extensions.RobotoRegularTextView;
import co.uk.tusksolutions.tchat.android.R;

public class RosterViewHolder {
	public ImageView rosterAvatar;
	public RobotoRegularTextView rosterName;
	public RobotoBoldTextView resource;
	public View rosterPresenceFrame;

	public RosterViewHolder(View v) {
		rosterPresenceFrame = v.findViewById(R.id.roster_presence_frame);
		rosterAvatar = (ImageView) v.findViewById(R.id.roster_avatar);
		rosterName = (RobotoRegularTextView) v.findViewById(R.id.roster_name);
		resource = (RobotoBoldTextView) v
				.findViewById(R.id.resource_name);
	}
}
