package co.uk.tusksolutions.tchat.android.listeners;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.MultiUserChat;

import android.content.Context;
import android.util.Log;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.models.ChatMessagesModel;
import co.uk.tusksolutions.tchat.android.models.ChatRoomsModel;
import co.uk.tusksolutions.tchat.android.models.GroupsModel;

public class XMPPGroupChatMessageListener implements PacketListener {

	private static final String TAG = "XMPPGroupChatMessageListener";
	Context mContext = TChatApplication.getContext();
	public static final String EXTRA_CHAT_STATE = "chatState";
	public static final String ACTION_XMPP_CHAT_STATE_CHANGED = "XMPP_CHAT_STATE_CHANGED";
	public static final String EXTRA_CHAT_BUDDY_NAME = "roomJid";

	public XMPPGroupChatMessageListener() {
		// TODO Auto Join groups when it becomes available in db.

		// Add invitation listener
		MultiUserChat.addInvitationListener(TChatApplication.connection,
				new XMPPMucInvitationListener(TChatApplication.getContext()));
	}

	@Override
	public void processPacket(Packet packet) {

		Message message = (Message) packet;
		if (message.getType() == Message.Type.groupchat
				&& !StringUtils.parseResource(message.getFrom())
						.equalsIgnoreCase(
								TChatApplication.getUserModel().getUsername())) {

			if (message.getBody().length() > 0
					&& StringUtils.parseResource(message.getFrom()).length() > 0) {

				String roomJid = StringUtils
						.parseBareAddress(message.getFrom());
				GroupsModel gm = new GroupsModel();
				String roomName = gm.getGroupName(StringUtils
						.parseBareAddress(message.getFrom()));

				if (roomName == null) {
					ChatRoomsModel chatRoomsModel = new ChatRoomsModel();
					roomName = chatRoomsModel.getChatRoomName(roomJid.replace(
							"@conference." + Constants.CURRENT_SERVER, ""));

				}

				// Save to DB
				saveMessageToDb(packet, message);
			}
		}
	}

	private void saveMessageToDb(Packet packet, Message message) {
		/*
		 * Insert received message to db
		 */

		String mid = packet.getPacketID();
		String roomJid = StringUtils.parseBareAddress(message.getFrom());
		String resource = StringUtils.parseResource(message.getFrom());
		String roomName = TChatApplication.getGroupsModel().getGroupName(
				StringUtils.parseBareAddress(message.getFrom()));

		if (roomName == null) {
			ChatRoomsModel chatRoomsModel = new ChatRoomsModel();
			roomName = chatRoomsModel.getChatRoomName(roomJid.replace(
					"@conference." + Constants.CURRENT_SERVER, ""));

		}
		Log.d("saveMessageToDb", "Sender: " + packet.getFrom() + " Resource: "
				+ resource + ", Receiver: " + packet.getTo() + " Message: "
				+ message.getBody());

		ChatMessagesModel mChatMessageModel = new ChatMessagesModel();

		mChatMessageModel.saveMessageToDB(TChatApplication.getCurrentJid(),
				roomJid, resource, roomName, message.getBody(), 1,
				"GROUP_CHAT", System.currentTimeMillis(), 0, mid);
	}

}
