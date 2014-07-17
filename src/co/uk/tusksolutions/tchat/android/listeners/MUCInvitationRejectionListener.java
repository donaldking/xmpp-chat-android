package co.uk.tusksolutions.tchat.android.listeners;

import org.jivesoftware.smackx.muc.InvitationRejectionListener;

import android.util.Log;


/**
 * @author Sebastian Gansca sebigansca@gmail.com
 *         <p/>
 *         Copyright 2012 Gemoro Mobile Media All rights reserved
 */
public class MUCInvitationRejectionListener implements InvitationRejectionListener {
    @Override
    public void invitationDeclined(String invitee, String reason) {
        Log.v("MucInvitation Listner","Buddy [%s] rejected with reason [%s]"+invitee+" "+reason);
    }
}
