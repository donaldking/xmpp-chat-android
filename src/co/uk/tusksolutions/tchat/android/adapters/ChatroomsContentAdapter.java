package co.uk.tusksolutions.tchat.android.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.activities.GroupChatActivity;
import co.uk.tusksolutions.tchat.android.models.ChatRoomsModel;
import co.uk.tusksolutions.tchat.android.viewHolders.ChatroomsAllViewHolder;

public class ChatroomsContentAdapter extends BaseAdapter {

	static String TAG = "ChatroomsContentAdapter";
	static float DEFAULT_ALPHA = 1.0f;
	static float SELECTED_ALPHA = 0.5f;
	private Context context;
	private ChatRoomsModel mModel;

	private ArrayList<ChatRoomsModel> chatroomsModelCollection;

	public ChatroomsContentAdapter(Context context) {
		this.context = TChatApplication.getContext();
		mModel = new ChatRoomsModel();
		chatroomsModelCollection = mModel.queryChatrooms();
		notifyDataSetChanged();
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
				GroupChatActivity.class);
		intent.putExtra("groupChatToRoomBundle", b);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		TChatApplication.getContext().startActivity(intent);
	}
}
