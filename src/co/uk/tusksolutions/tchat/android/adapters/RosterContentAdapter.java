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
import co.tusksolutions.tchat.android.activities.ChatActivity;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.models.RosterModel;
import co.uk.tusksolutions.tchat.android.viewHolders.RosterViewHolder;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

public class RosterContentAdapter extends BaseAdapter {

	static String TAG = "RosterContentAdapter";
	static float DEFAULT_ALPHA = 1.0f;
	static float SELECTED_ALPHA = 0.5f;
	Context context;

	ArrayList<RosterModel> rosterModelCollection;

	public RosterContentAdapter(Context context, String params) {
		this.context = context;
		RosterModel mModel = new RosterModel();

		/*
		 * Pull all media from model (DB)
		 */
		rosterModelCollection = mModel.query();
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

		final RosterModel rosterModel = rosterModelCollection.get(position);

		String[] username = rosterModel.user.split("@");
		UrlImageViewHelper.setUrlDrawable(holder.rosterAvatar,
				Constants.PROXY_SERVER + username[0] + "/avatar/48&return=png",R.drawable.mondobar_jewel_friends_on);
		holder.rosterName.setText(rosterModel.name);
		holder.rosterPresenceType.setText(rosterModel.presenceType);

		row.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				doSelectionAnimationForView(v);

				Bundle b = new Bundle();
				b.putString("fromName", rosterModel.name);

				Intent intent = new Intent(context, ChatActivity.class);
				intent.putExtra("chatFromFriendBundle", b);
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
