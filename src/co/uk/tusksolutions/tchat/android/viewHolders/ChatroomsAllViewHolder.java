package co.uk.tusksolutions.tchat.android.viewHolders;

import android.view.View;
import co.uk.tusksolutions.extensions.RobotoRegularTextView;
import co.uk.tusksolutions.tchat.android.R;

public class ChatroomsAllViewHolder {
	public RobotoRegularTextView chatroomName;
	 public ChatroomsAllViewHolder(View v)
	 {
		 chatroomName=(RobotoRegularTextView)v.findViewById(R.id.chatroom_name);
		 
	 }
}
