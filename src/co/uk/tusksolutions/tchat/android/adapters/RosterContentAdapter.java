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
import co.uk.tusksolutions.tchat.android.activities.ChatActivity;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.models.RosterModel;
import co.uk.tusksolutions.tchat.android.viewHolders.RosterViewHolder;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

public class RosterContentAdapter extends BaseAdapter {

	static String TAG = "RosterContentAdapter";
	static float DEFAULT_ALPHA = 1.0f;
	static float SELECTED_ALPHA = 0.5f;
	private Context context;
	private RosterModel mModel;
	private int action;

	private ArrayList<RosterModel> rosterModelCollection;

	public RosterContentAdapter(Context context, int action) {
		this.context = TChatApplication.getContext();
		mModel = new RosterModel();
		/**
		 * action is an integer of what data to query. 1 = All Friends
		 * (queryAll()) 2 = Online Friends (queryOnline())
		 */
		this.action = action;

		switch (action) {
		case 1:
			rosterModelCollection = mModel.queryAll();
			notifyDataSetChanged();
			break;
		case 2:
			rosterModelCollection = mModel.queryOnline();
			notifyDataSetChanged();
			break;
		default:
			rosterModelCollection = mModel.queryAll();
			notifyDataSetChanged();
			break;
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
		RosterViewHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.roster_row, parent, false);

			holder = new RosterViewHolder(row);
			row.setTag(holder);

		} else {
			holder = (RosterViewHolder) row.getTag();
		}

		/**
		 * Put values received from model collection to view holder.
		 * 
		 */
		final RosterModel rosterModel = rosterModelCollection.get(position);

		String[] username = rosterModel.user.split("@");
		try {
			UrlImageViewHelper.setUrlDrawable(holder.rosterAvatar,
					Constants.PROXY_SERVER + username[0]
							+ "/avatar/1288&return=png",
					R.drawable.mondobar_jewel_friends_on);
		} catch (Exception e) {
			e.printStackTrace();
		}

		holder.rosterName.setText(rosterModel.name);
		holder.resource.setText(rosterModel.resourceName);
		/*
		 * Show presenceType if we are loading action 2 (Online)
		 */
		switch (action) {
		case 1:
			holder.rosterPresenceFrame.setVisibility(View.GONE);
			break;
		case 2:
			holder.rosterPresenceFrame.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}

		row.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				doSelectionAnimationForView(v);
				Bundle b = new Bundle();
				b.putString("buddyJid", rosterModel.user);
				b.putString("friendName", rosterModel.name);

				Intent intent = new Intent(context, ChatActivity.class);
				intent.putExtra("chatWithFriendBundle", b);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
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
}
