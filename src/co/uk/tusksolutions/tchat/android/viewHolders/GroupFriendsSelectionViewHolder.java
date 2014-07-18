package co.uk.tusksolutions.tchat.android.viewHolders;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import co.uk.tusksolutions.extensions.RobotoRegularTextView;
import co.uk.tusksolutions.tchat.android.R;

public class GroupFriendsSelectionViewHolder {
	public ImageView rosterAvatar;
	public RobotoRegularTextView rosterName;
	public CheckBox checkMark;

	public GroupFriendsSelectionViewHolder(View v) {
		checkMark = (CheckBox) v.findViewById(R.id.checked_text_view);
		rosterAvatar = (ImageView) v.findViewById(R.id.roster_avatar);
		rosterName = (RobotoRegularTextView) v.findViewById(R.id.roster_name);

	}
}