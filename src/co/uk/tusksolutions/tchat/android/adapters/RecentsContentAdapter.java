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
import co.uk.tusksolutions.extensions.TimeAgo;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.activities.ChatActivity;
import co.uk.tusksolutions.tchat.android.activities.GroupChatActivity;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.models.RecentsModel;
import co.uk.tusksolutions.tchat.android.viewHolders.RecentsViewHolder;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

public class RecentsContentAdapter extends BaseAdapter {

	static String TAG = "RecentsContentAdapter";
	static float DEFAULT_ALPHA = 1.0f;
	static float SELECTED_ALPHA = 0.5f;
	private Context context;
	private RecentsModel mModel;

	private ArrayList<RecentsModel> recentsModelCollection;

	public RecentsContentAdapter(Context context) {
		this.context = TChatApplication.getContext();
		mModel = new RecentsModel();
		recentsModelCollection = mModel.queryRecents();
		notifyDataSetChanged();
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return recentsModelCollection.size();
	}

	@Override
	public Object getItem(int position) {
		return recentsModelCollection.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View row = convertView;
		RecentsViewHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.recents_row, parent, false);

			holder = new RecentsViewHolder(row);
			row.setTag(holder);

		} else {
			holder = (RecentsViewHolder) row.getTag();
		}

		/**
		 * Put values received fromUser model collection TO_USER view holder.
		 * 
		 */
		final RecentsModel model = recentsModelCollection.get(position);

		Log.d(TAG, "Recents model: " + model.toString());

		try {
			String[] username = model.chatWithUser.split("@");
			UrlImageViewHelper.setUrlDrawable(holder.rosterAvatar,
					Constants.PROXY_SERVER + username[0]
							+ "/avatar/1288&return=png",
					R.drawable.mondobar_jewel_friends_on);
		} catch (Exception e) {
			e.printStackTrace();
		}

		holder.rosterName.setText(model.name);
		if ((model.message!=null)&&model.message.contains("<img src")) {
			holder.lastMessage.setText("Image");
		} else if ((model.message!=null)&&model.message.contains("<a target")) {
			holder.lastMessage.setText("File");
		} else {
			holder.lastMessage.setText(model.message);
		}
		holder.lastMessageTimestamp.setText(TimeAgo.getTimeAgo(
				Long.parseLong(model.timestamp), context));

		row.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				doSelectionAnimationForView(v);

				Bundle b = new Bundle();
				b.putString("roomJid", model.chatWithUser);

				Log.d(TAG, "isGroupMessage " + model.isGroupMessage);

				if (model.isGroupMessage == 1) {
					b.putString("roomName", model.name);
					launchGroupChatActivity(b);
				} else {
					b.putString("friendName", model.name);
					launchNormalChatActivity(b);
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
				GroupChatActivity.class);
		intent.putExtra("groupChatToRoomBundle", b);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		TChatApplication.getContext().startActivity(intent);
	}

	private void launchNormalChatActivity(Bundle b) {

		Intent intent = new Intent(TChatApplication.getContext(),
				ChatActivity.class);
		intent.putExtra("chatWithFriendBundle", b);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		TChatApplication.getContext().startActivity(intent);
	}

}
