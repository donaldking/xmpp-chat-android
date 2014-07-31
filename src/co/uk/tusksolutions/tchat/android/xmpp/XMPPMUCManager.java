package co.uk.tusksolutions.tchat.android.xmpp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.MultiUserChat;

import android.content.Context;
import android.util.Log;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.listeners.XMPPMucParticipantsListener;
import co.uk.tusksolutions.tchat.android.listeners.XMPPMucSubjectUpdatedListener;
import co.uk.tusksolutions.tchat.android.listeners.XMPPMucUserStatusListener;
import co.uk.tusksolutions.tchat.android.models.RosterModel;

public class XMPPMUCManager {
	private static String TAG = "XMPPMUCManager";
	private static final int JOIN_TIMEOUT = 5000;
	private Context context;
	private Map<String, MultiUserChat> mRooms = new HashMap<String, MultiUserChat>();

	private static XMPPMUCManager XMPPMUCManager;

	public XMPPMUCManager(Context ctx) {
		this.context = ctx;
	}

	public void mucServiceDiscovery() {
		mRooms.clear();
		try {
			Collection<String> mucComponents = MultiUserChat
					.getServiceNames(TChatApplication.connection);
			if (mucComponents.size() > 0) {
				Iterator<String> i = mucComponents.iterator();

			}
		} catch (XMPPException e) {
			// This is not fatal, just log a warning
			Log.v("TAG", "Could not discover local MUC component: ");
		}
	}

	public static XMPPMUCManager getInstance(Context ctx) {
		if (XMPPMUCManager == null) {
			XMPPMUCManager = new XMPPMUCManager(ctx);
		}
		return XMPPMUCManager;
	}

	private MultiUserChat CreateRoom(String roomName, String roomJID,
			String nickname, String password) throws XMPPException {

		MultiUserChat multiUserChat = null;

		Log.i("Creating room [%s]", roomJID);

		// See issue 136
		try {
			if (TChatApplication.connection != null)

			{
				multiUserChat = new MultiUserChat(TChatApplication.connection,
						roomJID);
			} else {
				TChatApplication.reconnect();
				Log.e("connection closed ", "connection "
						+ TChatApplication.connection);
				multiUserChat = new MultiUserChat(TChatApplication.connection,
						roomJID);
			}

		} catch (Exception e) {
			// This is not a fatal exception, Just to handle exceptions
			Log.v(TAG, "Exception in create room " + e.getLocalizedMessage());
		}

		try {
			// multiUserChat.create(nickname);
			multiUserChat.join(nickname);

		} catch (Exception e) {
			Log.e(TAG, "MUC creation failed: ");
			e.printStackTrace();
			throw new XMPPException("MUC creation failed for " + nickname
					+ ": " + e.getLocalizedMessage(), e);

		}
		/*
		 * try { // We send an empty configuration to the server. For some
		 * reason the // server doesn't accept or process our // completed form,
		 * so we just send an empty one. The server defaults // will be used
		 * which are fine. // multiUserChat.sendConfigurationForm(new
		 * Form(Form.TYPE_SUBMIT)); //multiUserChat.changeSubject(roomName);
		 * 
		 * } catch (XMPPException e1) { Log.d(e1.toString(),
		 * "Unable to send conference room configuration form."); // then we
		 * also should not send an invite as the room will be locked throw e1; }
		 */

		// Sleep few seconds between creation and invite new user
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {

		}

		/*
		 * if (info.isPasswordProtected()) { multiUserChat.join(nickname,
		 * password, discussionHistory, JOIN_TIMEOUT); } else {
		 * multiUserChat.join(nickname, null, discussionHistory, JOIN_TIMEOUT);
		 * }
		 */

		multiUserChat.join(nickname, null, null, JOIN_TIMEOUT);
		// IMPORTANT you should join before registerRoom
		// registerRoom(multiUserChat, roomJID, roomName, password);

		return multiUserChat;
	}

	public MultiUserChat inviteToRoom(String roomName, String nickname,
			String buddyJID, String password, String roomJID, String group_name)
			throws XMPPException {
		MultiUserChat muc;

		if (!mRooms.containsKey(roomJID)) {
			muc = CreateRoom(roomName, roomJID, nickname, password);
			mRooms.put(roomJID, muc);
		} else {
			muc = mRooms.get(roomJID);
		}

		if (muc != null) {

			muc.changeSubject(group_name);
			muc.invite(buddyJID, group_name);

		}

		return muc;
	}

	public boolean kickFromRoom(Connection conn,
			ArrayList<RosterModel> friendArrayList, String roomJID) {
		/**
		 * Kicks the participants from the room and also bans the user to
		 * prevent them from joining
		 */

		MultiUserChat muc = new MultiUserChat(conn, roomJID);

		for (RosterModel rosterModel : friendArrayList) {
			try {
				try {
					/*
					 * Try to kick the user if they are online
					 */
					muc.kickParticipant(
							StringUtils.parseName(rosterModel.user), "");
					muc.revokeMembership(rosterModel.user);
				} catch (Exception e) {

					e.printStackTrace();
				}
				/*
				 * Ban the user from joining the room again
				 */
				muc.banUser(rosterModel.user, "");
			} catch (XMPPException e) {
				e.printStackTrace();
			}
		}
		return true;

	}

	public void joinRoom(Connection conn, String roomJID,
			final String password, final String nickname) {

		if (!mRooms.containsKey(roomJID)) {
			MultiUserChat muc = new MultiUserChat(conn, roomJID);
			mRooms.put(roomJID, muc);
		}

		MultiUserChat muc = mRooms.get(roomJID);

		try {
			// Use DiscussionHistory here and specify how many messages you want
			// to receive.
			muc.join(nickname, password, null, JOIN_TIMEOUT);
			muc.addParticipantStatusListener(new XMPPMucParticipantsListener());
			muc.addSubjectUpdatedListener(new XMPPMucSubjectUpdatedListener());
			muc.addUserStatusListener(new XMPPMucUserStatusListener());

			// registerRoom(muc, roomJID,
			// MyRoom.getRoomNameFromRoomJID(roomJID), password);
		} catch (XMPPException e) {
			/*
			 * TODO: All these toasts don't work. This listener is called from a
			 * background thread. You can't create and show a toast from a
			 * background thread. This should be refactored to use a handler to
			 * the UIThread or fire an event / broadcast intent to something on
			 * the UIThread that can create the toast This is just not
			 * working...... - jeroen
			 */
			/*
			 * switch (e.getXMPPError().getCode()) { case 401:
			 * Toast.makeText(context, "Password is required!",
			 * Toast.LENGTH_SHORT).show();
			 * 
			 * break; case 403: Toast.makeText(context,
			 * "You are banned from this room!", Toast.LENGTH_SHORT).show();
			 * 
			 * break; case 404: Toast.makeText(context,
			 * "Room does not exist or is locked!", Toast.LENGTH_SHORT).show();
			 * 
			 * break; case 406: Toast.makeText(context,
			 * "Room does not accept this user!", Toast.LENGTH_SHORT).show();
			 * 
			 * break; case 407: Toast.makeText(context,
			 * "You are not on the members list!", Toast.LENGTH_SHORT).show();
			 * 
			 * break; case 409: Toast.makeText( context,
			 * "You must change your nickname in order to join this room!",
			 * Toast.LENGTH_SHORT).show();
			 * 
			 * break; }
			 */
			switch (e.getXMPPError().getCode()) {
			case 403:
				Log.i(TAG, "User banned from room with ID: " + roomJID);
				// Delete group from local db
				break;

			default:
				break;
			}
			Log.e(TAG, e.getLocalizedMessage());
		}
	}

}
