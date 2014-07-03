package co.uk.tusksolutions.tchat.android.xmpp;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings.Secure;
import android.widget.Toast;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.listeners.XMPPChatMessageListener;
import co.uk.tusksolutions.tchat.android.listeners.XMPPConnectionListener;
import co.uk.tusksolutions.tchat.android.listeners.XMPPGroupChatMessageListener;
import co.uk.tusksolutions.tchat.android.listeners.XMPPPresenceListener;

public class XMPPConnectionManager {

	static final String TAG = "XMPPConnectionManager";

	public static void connect(final String username, final String password) {

		new Thread() {
			public void run() {
				TChatApplication.connection = new XMPPConnection(
						Constants.CURRENT_SERVER);
				try {
					try {
						TChatApplication.connection.connect();
					} catch (Exception e1) {
						e1.printStackTrace();
					}

					try {
						TChatApplication.connection.login(
								username,
								password,
								"TChat-Android-"
										+ Secure.getString(TChatApplication
												.getContext()
												.getContentResolver(),
												Secure.ANDROID_ID));

						/**
						 * 1) Add Connection Listener
						 */
						try {
							TChatApplication.connection
									.addConnectionListener(new XMPPConnectionListener());
						} catch (Exception e) {
							e.printStackTrace();
						}
						/**
						 * 2) Set up chat messages packet listener
						 */
						try {
							PacketFilter chatFilter = new MessageTypeFilter(
									Message.Type.chat);
							TChatApplication.connection.addPacketListener(
									new XMPPChatMessageListener(), chatFilter);
						} catch (Exception e) {
							e.printStackTrace();
						}

						/**
						 * 3) Set up group chat messages packet listener
						 */
						try {
							PacketFilter groupChatFilter = new MessageTypeFilter(
									Message.Type.groupchat);
							TChatApplication.connection.addPacketListener(
									new XMPPGroupChatMessageListener(),
									groupChatFilter);
						} catch (Exception e) {
							e.printStackTrace();
						}

						/**
						 * 4) Add Presence packet listener
						 */
						try {
							PacketFilter presenceFilter = new PacketTypeFilter(
									Presence.class);
							TChatApplication.connection.addPacketListener(
									new XMPPPresenceListener(), presenceFilter);

						} catch (Exception e) {
							e.printStackTrace();
						}

						/*
						 * Show Toast who is logged in
						 */
						new Handler(Looper.getMainLooper())
								.post(new Runnable() {
									@Override
									public void run() { // Show who we are
														// logged in as
										Toast.makeText(
												TChatApplication.getContext(),
												(String) TAG
														+ " Logged in as: "
														+ TChatApplication.connection
																.getUser(),
												Toast.LENGTH_LONG).show();
										/*
										 * Send login successful broadcast
										 */
										TChatApplication
												.getContext()
												.sendBroadcast(
														new Intent(
																Constants.LOGIN_SUCCESSFUL));
									}
								});

					} catch (Exception e) {
						/**
						 * Error connecting. This could be Internet off or some
						 * issue. We need TO_USER remove connection object.
						 */
						TChatApplication.connection = null;
						/*
						 * Show Toast who is logged in
						 */
						new Handler(Looper.getMainLooper())
								.post(new Runnable() {
									@Override
									public void run() { // Show who we are
														// logged in as
										Toast.makeText(
												TChatApplication.getContext(),
												(String) TAG
														+ " Unable TO_USER login",
												Toast.LENGTH_SHORT).show();
										TChatApplication.tearDownAndLogout();
									}
								});

						e.printStackTrace();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();

	}

}