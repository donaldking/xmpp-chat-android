package co.uk.tusksolutions.tchat.android.adapters;

import java.util.List;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import co.uk.tusksolutions.extensions.CheckableRelativeLayout;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.models.GroupItemsModel;
import co.uk.tusksolutions.tchat.android.viewHolders.GroupFriendsViewHolder;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class GroupFriendsAdapter extends ArrayAdapter<GroupItemsModel>{

	
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
		ViewHolder holder;
		if (convertView == null) {
			convertView = li.inflate(R.layout.item, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(R.id.holder, holder);
		} else {
			holder = (ViewHolder) convertView.getTag(R.id.holder);
		}
		
		String[] username = item.user.split("@");
		try {
			UrlImageViewHelper.setUrlDrawable(holder.id,
					Constants.PROXY_SERVER + username[0]
							+ "/avatar/1288&return=png",
					R.drawable.mondobar_jewel_friends_on);
		} catch (Exception e) {
			e.printStackTrace();
		}


		// Set some view properties
		holder.caption.setText(item.name);
		

		// Restore the checked state properly
		final ListView lv = (ListView) parent;
		holder.layout.setChecked(lv.isItemChecked(position));

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

	private LayoutInflater li;

	private static class ViewHolder {
		public ViewHolder(View root) {
			id = (ImageView) root.findViewById(R.id.roster_avatar);
			caption = (TextView) root.findViewById(R.id.roster_name);
			layout = (CheckableRelativeLayout) root.findViewById(R.id.layout);
		}

		public ImageView id;
		public TextView caption;
		public CheckableRelativeLayout layout;
	}
}
