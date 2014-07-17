package co.uk.tusksolutions.tchat.android.listeners;

import org.jivesoftware.smack.XMPPConnection;

public abstract class XmppConnectionChangeListener {
    
    public abstract void newConnection(XMPPConnection connection);

}
