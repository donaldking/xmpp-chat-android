package co.uk.tusksolutions.tchat.android.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.activities.GroupChatActivity;
import co.uk.tusksolutions.tchat.android.models.GroupsModel;
import co.uk.tusksolutions.tchat.android.viewHolders.GroupsViewHolder;

public class GroupsContentAdapter extends BaseAdapter {

	static String TAG = "GroupsContentAdapter";
	static float DEFAULT_ALPHA = 1.0f;
	static float SELECTED_ALPHA = 0.5f;
	private Context context;
	private GroupsModel mModel;

	private ArrayList<GroupsModel> groupsModelCollection;

	public GroupsContentAdapter(Context context) {
		this.context = TChatApplication.getContext();
		mModel = new GroupsModel();
		groupsModelCollection = mModel.queryGroups();
		notifyDataSetChanged();
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return groupsModelCollection.size();
	}

	@Override
	public Object getItem(int position) {
		return groupsModelCollection.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View row = convertView;
		GroupsViewHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.group_row, parent, false);

			holder = new GroupsViewHolder(row);
			row.setTag(holder);

		} else {
			holder = (GroupsViewHolder) row.getTag();
		}

		/**
		 * Put values received fromUser model collection TO_USER view holder.
		 * 
		 */
		final GroupsModel model = groupsModelCollection.get(position);

		holder.groupName.setText(model.group_name);

		row.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				doSelectionAnimationForView(v);
				
				Bundle b = new Bundle();
				b.putString("roomJid", model.group_id);
				b.putString("roomName", model.group_name);
				launchGroupChatActivity(b);

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
}
