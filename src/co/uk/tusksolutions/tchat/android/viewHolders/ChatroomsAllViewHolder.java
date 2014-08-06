package co.uk.tusksolutions.tchat.android.viewHolders;

import android.view.View;
import co.uk.tusksolutions.extensions.RobotoRegularTextView;
import co.uk.tusksolutions.tchat.android.R;

public class ChatroomsAllViewHolder {
	public RobotoRegularTextView chatroomName;
	public View joinchatroom;
	 public ChatroomsAllViewHolder(View v)
	 {
		 chatroomName=(RobotoRegularTextView)v.findViewById(R.id.chatroom_name);
		 joinchatroom = v.findViewById(R.id.chat_room_join_layout);
		 joinchatroom.setVisibility(View.GONE);
		 
	 }
}
