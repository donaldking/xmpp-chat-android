package co.uk.tusksolutions.tchat.android.viewHolders;

import android.view.View;
import co.uk.tusksolutions.extensions.RobotoBoldTextView;
import co.uk.tusksolutions.extensions.RobotoLightTextView;
import co.uk.tusksolutions.tchat.android.R;

import com.mikhaellopez.circularimageview.CircularImageView;

public class RosterViewHolder {
	public CircularImageView rosterAvatar;
	public RobotoBoldTextView rosterName;
	public RobotoLightTextView rosterPresenceType;

	public RosterViewHolder(View v) {
		rosterAvatar = (CircularImageView) v.findViewById(R.id.roster_avatar);
		rosterName = (RobotoBoldTextView) v.findViewById(R.id.roster_name);
		rosterPresenceType = (RobotoLightTextView) v
				.findViewById(R.id.roster_presence_type);
	}
}
