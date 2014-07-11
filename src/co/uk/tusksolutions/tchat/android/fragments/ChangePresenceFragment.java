package co.uk.tusksolutions.tchat.android.fragments;

import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.packet.Presence;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.listeners.XMPPPresenceListener;
import co.uk.tusksolutions.tchat.android.models.UserModel;

public class ChangePresenceFragment extends DialogFragment implements
		OnClickListener {

	private RadioGroup mRadioPresenceGroup;
	private RadioButton mRadioOnline, mRadioInvisible, mRadioOffline;
	Handler handler;
	UserModel mUserModel;

	public ChangePresenceFragment() {

		if (TChatApplication.presenceDialogHandler != null) {
			this.handler = TChatApplication.presenceDialogHandler;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_change_presence_dialog,
				container);
		mRadioPresenceGroup = (RadioGroup) view
				.findViewById(R.id.radio_presence_group);

		mRadioOnline = (RadioButton) mRadioPresenceGroup
				.findViewById(R.id.radio_online);
		mRadioOnline.setOnClickListener(this);

		mRadioInvisible = (RadioButton) mRadioPresenceGroup
				.findViewById(R.id.radio_invisible);
		mRadioInvisible.setOnClickListener(this);

		mRadioOffline = (RadioButton) mRadioPresenceGroup
				.findViewById(R.id.radio_offline);
		mRadioOffline.setOnClickListener(this);

		getDialog().setTitle("Availability");

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onResume() {
		super.onResume();

		mUserModel = new UserModel();
		setSelectedPresence();
	}
	
	public void setSelectedPresence() {

		String presence = mUserModel.getCurrentPresence();

		if (presence.equalsIgnoreCase("online")) {
			mRadioOnline.setChecked(true);
		} else if (presence.equalsIgnoreCase("invisible")) {
			mRadioInvisible.setChecked(true);
		} else if (presence.equalsIgnoreCase("offline")) {
			mRadioOffline.setChecked(true);
		} else {
			mRadioOffline.setChecked(true);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.radio_online:
			this.setPresence("online");
			if (TChatApplication.connection == null) {
				TChatApplication.reconnect();
			}
			break;
		case R.id.radio_invisible:
			this.setPresence("invisible");
			if (TChatApplication.connection != null) {
				XMPPPresenceListener.setXMPPPresence(Presence.Type.unavailable);
			}
			break;
		case R.id.radio_offline:
			this.setPresence("offline");
			if (TChatApplication.connection != null) {
				XMPPPresenceListener.setXMPPPresence(Presence.Type.unavailable);
			}
			break;

		default:
			break;
		}

	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		handler.sendEmptyMessage(0);
	}

	private void setPresence(String presence) {
		if (TChatApplication.getUserModel().updateCurrentPresence(presence)) {

			Intent i = new Intent();
			i.putExtra("presence", presence);
			i.setAction(Constants.USER_PRESENCE_CHANGED);
			getActivity().sendBroadcast(i);

			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					getDialog().dismiss();
				}
			}, 100);
		}
	}
}
