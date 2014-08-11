package co.uk.tusksolutions.tchat.android.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.Toast;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.activities.ChatRoomActivity;
import co.uk.tusksolutions.tchat.android.activities.GroupChatActivity;
import co.uk.tusksolutions.tchat.android.api.APICreateChatrooms;
import co.uk.tusksolutions.tchat.android.api.APIGetChatroomparticipants;
import co.uk.tusksolutions.tchat.android.api.APIGetChatroomparticipants.OnGetparticipantsCount;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.models.ChatRoomsModel;
import co.uk.tusksolutions.tchat.android.viewHolders.ChatroomsAllViewHolder;
import co.uk.tusksolutions.tchat.android.xmpp.XMPPMUCManager;

public class ChatroomsContentAdapter extends BaseAdapter {

	static String TAG = "ChatroomsContentAdapter";
	static float DEFAULT_ALPHA = 1.0f;
	static float SELECTED_ALPHA = 0.5f;
	private Context context;
	private ChatRoomsModel mModel;
	private ArrayList<ChatRoomsModel> chatroomsModelCollection;
	public int action;
	int participantsCount = 0;

	public ChatroomsContentAdapter(Context context, int action) {
		this.context = TChatApplication.getContext();
		mModel = new ChatRoomsModel();
		this.action = action;

		switch (action) {
		case 1:
			chatroomsModelCollection = mModel.queryAllChatrooms();
			notifyDataSetChanged();

			break;

		case 2:
			chatroomsModelCollection = mModel.queryActiveChatrooms();
			notifyDataSetChanged();

			break;
		case 3:
			chatroomsModelCollection = mModel.queryScheduledChatrooms();
			notifyDataSetChanged();

			break;

		default:
			break;
		}

	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return chatroomsModelCollection.size();
	}

	@Override
	public Object getItem(int position) {
		return chatroomsModelCollection.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View row = convertView;
		ChatroomsAllViewHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.chatrooms_all_row, parent, false);

			holder = new ChatroomsAllViewHolder(row);
			row.setTag(holder);

		} else {
			holder = (ChatroomsAllViewHolder) row.getTag();
		}

		/**
		 * Put values received fromUser model collection TO_USER view holder.
		 * 
		 */
		final ChatRoomsModel model = chatroomsModelCollection.get(position);

		holder.chatroomName.setText(model.chatroom_name);

		switch (action) {
		case 1:
			holder.joinchatroom.setVisibility(View.GONE);
			break;
		case 2:

			holder.joinchatroom.setVisibility(View.VISIBLE);
			if (model.chatroom_owner.toString().contains(
					TChatApplication.getUserModel().getUsername())) {
				if (model.status.toString().contains("Active")) {
					holder.joinchatroom
							.setBackgroundResource(R.drawable.joinbutton);
				} else {
					holder.joinchatroom
							.setBackgroundResource(R.drawable.startbutton);
				}
			} else {
				holder.joinchatroom
						.setBackgroundResource(R.drawable.joinbutton);
			}
			break;
		case 3:
			holder.joinchatroom.setVisibility(View.GONE);
			break;
		default:
			holder.joinchatroom.setVisibility(View.GONE);
			break;
		}
		row.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				APIGetChatroomparticipants apiGetChatroomparticipants = new APIGetChatroomparticipants();
				apiGetChatroomparticipants.doGetChatroomparticipants(
						model.chatroom_jid, new OnGetparticipantsCount() {

							@Override
							public void OnGetChatRoomParticipantsSuccess(
									String room_id, int count) {
								// TODO Auto-generated method stub
								participantsCount = count;
								Log.v("CHAT ROOM PARTICIPANTS", "Room id "
										+ room_id + " having member " + count);

							}
						});
				Long currentTime = System.currentTimeMillis();
				if (Long.valueOf(model.start_timestamp) > currentTime) {
					Toast.makeText(context, "This Chatroom not started yet",
							Toast.LENGTH_SHORT).show();
					Log.v(TAG, "Not started yet " + model.start_timestamp
							+ " current timestamp " + currentTime);
				} else if (model.chatroom_owner.toString().contains(
						TChatApplication.getUserModel().getUsername())) {

					doSelectionAnimationForView(v);
					TChatApplication.joinChatRoom(model.chatroom_jid);
					mModel.updateStatusofChatRoom(model.chatroom_jid, "Active");

					// CallApiToChangeStatus(model.chatroom_jid,
					// model.chatroom_name, model.chatroom_owner,
					// model.start_timestamp,model.end_timestamp,
					// "Active", model.max_guests, model.created_at);

					Bundle b = new Bundle();
					b.putString("roomJid", model.chatroom_jid + "@conference."
							+ Constants.CURRENT_SERVER);
					b.putString("roomName", model.chatroom_name);

					launchGroupChatActivity(b);

				} else if (participantsCount > 0) {
					TChatApplication.joinChatRoom(model.chatroom_jid);

					Bundle b = new Bundle();
					b.putString("roomJid", model.chatroom_jid + "@conference."
							+ Constants.CURRENT_SERVER);
					b.putString("roomName", model.chatroom_name);

					launchGroupChatActivity(b);

				}
			}

		});
		return row;
	}

	private void doSelectionAnimationForView(View v) {
		Animation fadeAnimation = new AlphaAnimation(DEFAULT_ALPHA,
				SELECTED_ALPHA);
		fadeAnimation.setDuration(50);
		v.startAnimation(fadeAnimation);
	}

	private void launchGroupChatActivity(Bundle b) {

		Intent intent = new Intent(TChatApplication.getContext(),
				ChatRoomActivity.class);
		intent.putExtra("groupChatToRoomBundle", b);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		TChatApplication.getContext().startActivity(intent);
	}

	private void CallApiToChangeStatus(String chatroom_id,
			String chatroom_name, String chatroom_owner,
			String start_timestamp, String end_timestamp, String status,
			String max_guest, String created_at) {
		// TODO Auto-generated method stub
		APICreateChatrooms apiCreateChatrooms = new APICreateChatrooms();
		apiCreateChatrooms.doPostChatroom(chatroom_id, chatroom_name,
				chatroom_owner, start_timestamp, end_timestamp, status,
				max_guest, created_at);
	}
}
