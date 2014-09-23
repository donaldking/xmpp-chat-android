package co.uk.tusksolutions.tchat.android.adapters;

import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Filter;
import android.widget.Filterable;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.enums.AddOrRemoveEnum;
import co.uk.tusksolutions.tchat.android.models.RosterModel;
import co.uk.tusksolutions.tchat.android.viewHolders.GroupFriendsSelectionViewHolder;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

public class GroupFriendsSelectionAdapter extends BaseAdapter implements
		Filterable {

	private RosterModel mModel;
	private Context context;
	public static CopyOnWriteArrayList<RosterModel> rosterModelCollection;
	private CopyOnWriteArrayList<RosterModel> rosterModelCollectionCopy;
	private CopyOnWriteArrayList<RosterModel> rosterModelCollectionTmp;
	private ValueFilter valueFilter;
	private final static String TAG = "GroupFriendsSelectionAdapter";
	
	public GroupFriendsSelectionAdapter() {
		this.context = TChatApplication.getContext();
		mModel = new RosterModel();
		rosterModelCollection = mModel.queryAll();
		rosterModelCollectionCopy = rosterModelCollection;
		getFilter();
	}

	public GroupFriendsSelectionAdapter(Context context,
			JSONArray participants, AddOrRemoveEnum action)
			throws JSONException {

		this.context = TChatApplication.getContext();
		mModel = new RosterModel();
		rosterModelCollection = mModel.queryAll();
		rosterModelCollectionTmp = rosterModelCollection;

		if (action == AddOrRemoveEnum.ADD_PEOPLE) {

			/*
			 * Remove people already added
			 */
			for (RosterModel rm : rosterModelCollectionTmp) {
				synchronized (rosterModelCollection) {
					for (int i = 0; i < participants.length(); i++) {
						if (rm.user.equals(participants.getJSONObject(i).get(
								"user_id"))) {
							Log.d(TAG, "Will remove: " + rm.user);
							rosterModelCollection.remove(rm);
						}
					}
				}
			}
			rosterModelCollectionCopy = rosterModelCollection;
			getFilter();
		} else if (action == AddOrRemoveEnum.REMOVE_PEOPLE) {

			rosterModelCollection = mModel.getUsers(participants);

		}
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return rosterModelCollection.size();
	}

	@Override
	public Object getItem(int position) {
		return rosterModelCollection.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View row = convertView;
		GroupFriendsSelectionViewHolder holder = null;
		
		
		final RosterModel rosterModel = rosterModelCollection.get(position);
	
		if (row == null) {

			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.group_selection_row, parent, false);

			holder = new GroupFriendsSelectionViewHolder(row);
			final GroupFriendsSelectionViewHolder vH = holder;
			row.setTag(holder);
			 final CheckBox ctv = holder.checkMark;
		
			ctv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					final RosterModel rosterModel = (RosterModel) vH.checkMark
							.getTag();
					rosterModel.setSelected(buttonView.isChecked());

					Log.d(TAG, "Model status: " + rosterModel.name
							+ " Selection: " + rosterModel.isSelected());
				}
			});
			row.setTag(holder);
			holder.checkMark.setTag(rosterModelCollection.get(position));

		} else {
			row = convertView;
			((GroupFriendsSelectionViewHolder) row.getTag()).checkMark
					.setTag(rosterModelCollection.get(position));
		}

		GroupFriendsSelectionViewHolder mHolder = (GroupFriendsSelectionViewHolder) row
				.getTag();
		mHolder.checkMark.setChecked(rosterModelCollection.get(position)
				.isSelected());

		String[] username = rosterModel.user.split("@");
		try {
			UrlImageViewHelper.setUrlDrawable(mHolder.rosterAvatar,
					Constants.PROXY_SERVER + username[0]
							+ "/avatar/1288&return=png",
					R.drawable.mondobar_jewel_friends_on);
		} catch (Exception e) {
			e.printStackTrace();
		}

		mHolder.rosterName.setText(rosterModel.name);
		return row;
	}

	@Override
	public Filter getFilter() {
		if (valueFilter == null) {
			valueFilter = new ValueFilter();
		}
		return valueFilter;
	}

	private class ValueFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			if (constraint != null && constraint.length() > 0) {
				CopyOnWriteArrayList<RosterModel> filterList = new CopyOnWriteArrayList<RosterModel>();
				for (int i = 0; i < rosterModelCollectionCopy.size(); i++) {
					if (rosterModelCollectionCopy.get(i).name.toLowerCase(
							Locale.ENGLISH).startsWith(
							constraint.toString().toLowerCase(Locale.ENGLISH))) {
						filterList.add(rosterModelCollectionCopy.get(i));
					}
				}
				results.count = filterList.size();
				results.values = filterList;
			} else {
				results.count = rosterModelCollectionCopy.size();
				results.values = rosterModelCollectionCopy;
			}
			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			rosterModelCollection = (CopyOnWriteArrayList<RosterModel>) results.values;
			notifyDataSetChanged();
		}
	}

	
	
}