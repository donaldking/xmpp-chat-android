package co.uk.tusksolutions.tchat.android.adapters;

import java.util.List;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.models.GroupItemsModel;
import co.uk.tusksolutions.tchat.android.viewHolders.GroupFriendsViewHolder;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class GroupFriendsAdapter extends ArrayAdapter<GroupItemsModel>{

	private LayoutInflater li;
	public GroupFriendsAdapter(Context context, List<GroupItemsModel> items) {
		super(context, 0, items);
		li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// The item we want to get the view for
		// --
		final GroupItemsModel item = getItem(position);

		// Re-use the view if possible
		// --
		GroupFriendsViewHolder holder;
		if (convertView == null) {
			convertView = li.inflate(R.layout.group_chat_friends_row, null);
			holder = new GroupFriendsViewHolder(convertView);
			convertView.setTag(R.id.holder, holder);
		} else {
			holder = (GroupFriendsViewHolder) convertView.getTag(R.id.holder);
		}
		
		String[] username = item.user.split("@");
		try {
			UrlImageViewHelper.setUrlDrawable(holder.rosterAvatar,
					Constants.PROXY_SERVER + username[0]
							+ "/avatar/1288&return=png",
					R.drawable.mondobar_jewel_friends_on);
		} catch (Exception e) {
			e.printStackTrace();
		}


		// Set some view properties
		holder.rosterName.setText(item.name);
		

		// Restore the checked state properly
		final ListView lv = (ListView) parent;
		holder.rosterPresenceFrame.setChecked(lv.isItemChecked(position));

		return convertView;
	}

	@Override
	public long getItemId(int position) {
		return (Integer.parseInt(getItem(position).id));
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	

	
}
