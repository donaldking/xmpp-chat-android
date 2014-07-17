package co.uk.tusksolutions.tchat.android.listeners;

import android.content.Context;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.ChatState;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.ChatStateExtension;

import co.uk.tusksolutions.tchat.android.TChatApplication;

import java.util.Date;

public class MUCPacketListener implements PacketListener {
    private String roomJID;
    private Date lastDate;
    private MultiUserChat multiUserChat;
   
    private Context context;
    private XMPPConnection connection;
    
    /**
     * Creates a new MUCPacketListener
     * The MUC can either be use for a SMS chat/converstation or for a shell session
     * 
     * @param roomJID
     * @param muc
     * @param ctx
     */
    public MUCPacketListener(XMPPConnection connection, String roomJID, MultiUserChat muc, Context ctx) {
        this.roomJID = roomJID;
        this.lastDate = new Date(0);
        this.multiUserChat = muc;
      
        this.context = ctx;
        this.connection = connection;
    }

    @Override
    public void processPacket(Packet packet) {
        Message message = (Message) packet;
        String from = message.getFrom();
        String fromBareResource = StringUtils.parseResource(from);

       
        
        // messages from the room JID itself, are matched here, because they have no 
        // resource part these are normally status messages about the room we send them 
        // to the notification address
        if (from.contains(roomJID) && fromBareResource.length() > 0) {

            ChatState chatState = null;
            for (PacketExtension extension : message.getExtensions()) {
                if (extension instanceof ChatStateExtension) {
                    ChatStateExtension chatStateExtension = (ChatStateExtension) extension;
                    chatState = ChatState.valueOf(chatStateExtension.getElementName());
                }
            }
//            String test = StringUtils.parseServer(from);
//            String fromJID = StringUtils.parseBareAddress(from);
            
            if (!fromBareResource.equals(TChatApplication.getUserModel().getUsername())
                    && (message.getBody() != null || chatState != null)) {
             
               //
            	
            } else {
                
            }
        }
    }
}