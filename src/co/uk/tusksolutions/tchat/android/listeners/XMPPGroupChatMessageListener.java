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
	String Message_Type;

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
<<<<<<< HEAD
				Log.e(TAG, "Message from "+roomJid);
				if(roomName==null)
				{
					
					ChatRoomsModel chatRoomsModel=new ChatRoomsModel();
					roomName=chatRoomsModel.getChatRoomName(roomJid.replace("@conference."+Constants.CURRENT_SERVER, ""));
				   Message_Type="CHAT_ROOM";
				}
				else
				{
					Message_Type="GROUP_CHAT";
				}
				Log.e(TAG, "Room name "+roomName);
				String senderJid = StringUtils.parseResource(message.getFrom())
						+ "@" + Constants.CURRENT_SERVER;
				String senderName = TChatApplication.getRosterModel()
						.getBuddyName(senderJid);

				Log.i(TAG,
						"New group message From Room Jid PKT: "
								+ packet.getFrom() + ", Room Name: " + roomName
								+ ", and sender: " + senderJid
								+ ", and Sender Name: " + senderName
								+ " Message: " + message.getBody()+"MessageType :"+Message_Type);

				if (TChatApplication.getChatActivityStatus() == CHAT_STATUS_ENUM.VISIBLE
						&& TChatApplication.chatSessionBuddy
								.equalsIgnoreCase(roomJid)) {

					// 1. Visible and chatting with buddy

					// Save to DB
					saveMessageToDb(packet, message);

				} else if (TChatApplication.getChatActivityStatus() == CHAT_STATUS_ENUM.VISIBLE
						&& !TChatApplication.chatSessionBuddy
								.equalsIgnoreCase(roomJid)) {

					// 2. Visible and not chatting with buddy
					/*
					 * Prepare message bundle.
					 */
					sendNotification(packet, message);

				} else if (TChatApplication.getChatActivityStatus() == CHAT_STATUS_ENUM.NOT_VISIBLE) {

					// 3. Not Visible and not chatting
					/*
					 * Prepare message bundle.
					 */
					sendNotification(packet, message);

				}
			}
		}
	}
=======
>>>>>>> db4c50ae58ec7b0825d6535dfe707fde29305bd3

				if (roomName == null) {
					ChatRoomsModel chatRoomsModel = new ChatRoomsModel();
					roomName = chatRoomsModel.getChatRoomName(roomJid.replace(
							"@conference." + Constants.CURRENT_SERVER, ""));

				}

<<<<<<< HEAD
		Bundle b = new Bundle();
		b.putString("roomJid", roomJid);
		b.putString("resource", resource);
		b.putString("roomName", roomName);
		b.putString("senderJid", senderJid);
		b.putString("senderName", senderName);
		b.putString("messageType",Message_Type);
		
		/*
		 * Image & File noticiations added
		 */
		String last_message = message.getBody();
		if (last_message.contains("<img src")) {
			last_message = "Image";
		}else if(last_message.contains("<a target")){
			last_message = "File";
=======
				// Save to DB
				saveMessageToDb(packet, message);
			}
>>>>>>> db4c50ae58ec7b0825d6535dfe707fde29305bd3
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
				+ message.getBody()+"MessageType: "+Message_Type);

		ChatMessagesModel mChatMessageModel = new ChatMessagesModel();

		mChatMessageModel.saveMessageToDB(TChatApplication.getCurrentJid(),
				roomJid, resource, roomName, message.getBody(), 1,
				Message_Type, System.currentTimeMillis(), 0, mid);
	}

}
