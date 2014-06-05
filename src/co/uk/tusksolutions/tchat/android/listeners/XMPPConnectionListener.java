package co.uk.tusksolutions.tchat.android.listeners;

import org.jivesoftware.smack.ConnectionListener;

import android.content.Intent;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.constants.Constants;

public class XMPPConnectionListener implements ConnectionListener {

	@Override
	public void connectionClosed() {
		TChatApplication.getContext().sendBroadcast(
				new Intent(Constants.CONNECTION_CLOSED_BY_USER));
	}

	@Override
	public void connectionClosedOnError(Exception arg0) {
		TChatApplication.getContext().sendBroadcast(
				new Intent(Constants.CONNECTION_CLOSED_IN_ERROR));
	}

	@Override
	public void reconnectingIn(int arg0) {
		TChatApplication.getContext().sendBroadcast(
				new Intent(Constants.RECONNECTING));
	}

	@Override
	public void reconnectionFailed(Exception arg0) {
		TChatApplication.connection = null;
		TChatApplication.getContext().sendBroadcast(
				new Intent(Constants.RECONNECTING_FAILED));
	}

	@Override
	public void reconnectionSuccessful() {
		TChatApplication.getContext().sendBroadcast(
				new Intent(Constants.RECONNECTION_SUCCESSFULL));
	}

}
