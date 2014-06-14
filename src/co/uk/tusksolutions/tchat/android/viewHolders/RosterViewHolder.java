package co.uk.tusksolutions.tchat.android.viewHolders;

import android.view.View;
import co.uk.tusksolutions.extensions.RobotoBoldTextView;
import co.uk.tusksolutions.extensions.RobotoRegularTextView;
import co.uk.tusksolutions.tchat.android.R;

import com.mikhaellopez.circularimageview.CircularImageView;

public class RosterViewHolder {
	public CircularImageView rosterAvatar;
	public RobotoRegularTextView rosterName;
	public RobotoBoldTextView rosterPresenceType;
	public View rosterPresenceFrame;

	public RosterViewHolder(View v) {
		rosterPresenceFrame = v.findViewById(R.id.roster_presence_frame);
		rosterAvatar = (CircularImageView) v.findViewById(R.id.roster_avatar);
		rosterName = (RobotoRegularTextView) v.findViewById(R.id.roster_name);
		rosterPresenceType = (RobotoBoldTextView) v
				.findViewById(R.id.roster_presence_type);
	}
}
