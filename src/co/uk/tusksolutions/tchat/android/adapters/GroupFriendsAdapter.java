package co.uk.tusksolutions.tchat.android.adapters;

import java.util.ArrayList;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.activities.GroupFriendsSelectionActivity;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.models.GroupItemsModel;
import co.uk.tusksolutions.tchat.android.viewHolders.GroupFriendsViewHolder;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

public class GroupFriendsAdapter extends ArrayAdapter<GroupItemsModel> implements Filterable {

	private LayoutInflater li;
	 GroupItemsModel item;
	 
	ArrayList<GroupItemsModel> rosterModelCollection=new ArrayList<GroupItemsModel>();
	public GroupFriendsAdapter(Context context, ArrayList<GroupItemsModel> items) {
		super(context, 0, items);
		li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rosterModelCollection=GroupFriendsSelectionActivity.rosterModelCollection;
		
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// The item we want to get the view for
		// --
		  item = getItem(position);


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

	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                //rosterModelCollection = (ArrayList<GroupItemsModel>) results.values;
                notifyDataSetChanged();
              
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                ArrayList<GroupItemsModel> FilteredArrayNames = new ArrayList<GroupItemsModel>();

                if (rosterModelCollection == null)    {
                	rosterModelCollection = new ArrayList<GroupItemsModel>(rosterModelCollection);
                   
                }
                if (constraint == null || constraint.length() == 0) {
                    results.count = rosterModelCollection.size();
                    results.values = rosterModelCollection;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < rosterModelCollection.size(); i++) {
                        String dataNames = rosterModelCollection.get(i).name;
                        String dataUser=rosterModelCollection.get(i).user;
                        String datastatus=rosterModelCollection.get(i).status;
                        String presenceStatus = rosterModelCollection.get(i).presenceStatus;
                		String presenceType = rosterModelCollection.get(i).presenceType;
                		String lastSeenTimestamp = rosterModelCollection.get(i).lastSeenTimestamp;
                		String resourceName = rosterModelCollection.get(i).resourceName;
                        if (dataNames.toLowerCase().contains(constraint.toString())||dataUser.toLowerCase().contains(constraint.toString()))  {
                            //FilteredArrayNames.add(dataNames);
                        	GroupItemsModel rosterModel = new GroupItemsModel();
                        	rosterModel.name=dataNames;
                        	rosterModel.user=dataUser;
                        	rosterModel.status=datastatus;
                        	rosterModel.presenceStatus=presenceStatus;
                        	rosterModel.presenceType=presenceType;
                        	rosterModel.lastSeenTimestamp=lastSeenTimestamp;
                        	rosterModel.resourceName=resourceName;
                        	FilteredArrayNames.add(rosterModel);
                        }
                    }

                    results.count = FilteredArrayNames.size();
                    System.out.println(results.count);

                    results.values = FilteredArrayNames;
                    Log.e("VALUES", results.values.toString());
                }

                return results;
            }
        };
		return filter;
	}
}
