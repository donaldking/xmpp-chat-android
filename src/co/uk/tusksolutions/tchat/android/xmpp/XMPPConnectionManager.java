package co.uk.tusksolutions.tchat.android.xmpp;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.XHTMLManager;
import org.jivesoftware.smackx.bytestreams.ibb.provider.CloseIQProvider;
import org.jivesoftware.smackx.bytestreams.ibb.provider.DataPacketProvider;
import org.jivesoftware.smackx.bytestreams.ibb.provider.OpenIQProvider;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.packet.AttentionExtension;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.Nick;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.HeaderProvider;
import org.jivesoftware.smackx.provider.HeadersProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.pubsub.provider.AffiliationProvider;
import org.jivesoftware.smackx.pubsub.provider.AffiliationsProvider;
import org.jivesoftware.smackx.pubsub.provider.ConfigEventProvider;
import org.jivesoftware.smackx.pubsub.provider.EventProvider;
import org.jivesoftware.smackx.pubsub.provider.FormNodeProvider;
import org.jivesoftware.smackx.pubsub.provider.ItemProvider;
import org.jivesoftware.smackx.pubsub.provider.ItemsProvider;
import org.jivesoftware.smackx.pubsub.provider.PubSubProvider;
import org.jivesoftware.smackx.pubsub.provider.RetractEventProvider;
import org.jivesoftware.smackx.pubsub.provider.SimpleNodeProvider;
import org.jivesoftware.smackx.pubsub.provider.SubscriptionProvider;
import org.jivesoftware.smackx.pubsub.provider.SubscriptionsProvider;
import org.jivesoftware.smackx.search.UserSearch;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings.Secure;
import android.widget.Toast;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.api.APIGetGroups;
import co.uk.tusksolutions.tchat.android.api.APIGetGroups.OnGetGroupsCompleted;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.listeners.XMPPChatMessageListener;
import co.uk.tusksolutions.tchat.android.listeners.XMPPConnectionListener;
import co.uk.tusksolutions.tchat.android.listeners.XMPPGroupChatMessageListener;
import co.uk.tusksolutions.tchat.android.listeners.XMPPPresenceListener;

public class XMPPConnectionManager {

	static final String TAG = "XMPPConnectionManager";

	public static void connect(final String username, final String password) {

		if (!TChatApplication.getUserModel().getCurrentPresence()
				.equalsIgnoreCase("offline")) {

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
										new XMPPChatMessageListener(),
										chatFilter);
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
										new XMPPPresenceListener(),
										presenceFilter);

							} catch (Exception e) {
								e.printStackTrace();
							}

							/*
							 * Send login successful broadcast
							 */
							TChatApplication.getContext().sendBroadcast(
									new Intent(Constants.LOGIN_SUCCESSFUL));

							ServiceDiscoveryManager serviceDiscoMgr = ServiceDiscoveryManager
									.getInstanceFor(TChatApplication.connection);

							if (serviceDiscoMgr != null) {
								XHTMLManager.setServiceEnabled(
										TChatApplication.connection, false);
								serviceDiscoMgr
										.addFeature("http://jabber.org/protocol/disco#info");
								serviceDiscoMgr
										.addFeature("http://jabber.org/protocol/muc");
								serviceDiscoMgr
										.addFeature("http://jabber.org/protocol/bytestreams");
								serviceDiscoMgr
										.addFeature("http://jabber.org/protocol/feature-neg");
								serviceDiscoMgr
										.addFeature("http://jabber.org/protocol/si/profile/file-transfer");
								serviceDiscoMgr
										.addFeature("http://jabber.org/protocol/si");
							}

							configureProviderManager(TChatApplication.connection);
							
							// Get groups and login
							APIGetGroups groupsApi = new APIGetGroups();
							groupsApi.getGroups(new OnGetGroupsCompleted() {
								
								@Override
								public void OnGetGroupsSuccess() {
									// TODO Auto-generated method stub
									TChatApplication.joinAllGroups();
								}
								
								@Override
								public void OnGetGroupsFailed() {
									// TODO Auto-generated method stub
									
								}
							});


						} catch (Exception e) {
							/**
							 * Error connecting. This could be Internet off or
							 * some issue. We need TO_USER remove connection
							 * object.
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
													TChatApplication
															.getContext(),
													(String) TAG
															+ " Unable TO_USER login",
													Toast.LENGTH_SHORT).show();
											// TChatApplication.tearDownAndLogout();
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
	
	public static void configureProviderManager(XMPPConnection connection) {
		ProviderManager pm = ProviderManager.getInstance();
		ProviderManager
				.getInstance()
				.addIQProvider(
						"query",
						"http://jabber.org/protocol/bytestreams",
						new BytestreamsProvider());
		ProviderManager.getInstance().addIQProvider("query",
				"http://jabber.org/protocol/disco#items",
				new DiscoverItemsProvider());
		ProviderManager.getInstance().addIQProvider("query",
				"http://jabber.org/protocol/disco#info",
				new DiscoverInfoProvider());

		ProviderManager.getInstance().addIQProvider("query",
				"http://jabber.org/protocol/bytestreams",
				new BytestreamsProvider());
		ProviderManager.getInstance().addIQProvider("query",
				"http://jabber.org/protocol/disco#items",
				new DiscoverItemsProvider());
		ProviderManager.getInstance().addIQProvider("query",
				"http://jabber.org/protocol/disco#info",
				new DiscoverInfoProvider());

		ServiceDiscoveryManager sdm = ServiceDiscoveryManager
				.getInstanceFor(connection);
		if (sdm == null)
			sdm = new ServiceDiscoveryManager(connection);

		sdm.addFeature("http://jabber.org/protocol/disco#info");
		sdm.addFeature("http://jabber.org/protocol/disco#item");
		sdm.addFeature("jabber:iq:privacy");

		

		// The order is the same as in the smack.providers file

		// Private Data Storage
		pm.addIQProvider("query", "jabber:iq:private",
				new PrivateDataManager.PrivateDataIQProvider());
		// Time
		try {
			pm.addIQProvider("query", "jabber:iq:time",
					Class.forName("org.jivesoftware.smackx.packet.Time"));
		} catch (ClassNotFoundException e) {
			System.err
					.println("Can't load class for org.jivesoftware.smackx.packet.Time");
		}

		// Roster Exchange
		pm.addExtensionProvider("x", "jabber:x:roster",
				new RosterExchangeProvider());
		// Message Events
		pm.addExtensionProvider("x", "jabber:x:event",
				new MessageEventProvider());
		// Chat State
		pm.addExtensionProvider("active",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("composing",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("paused",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("inactive",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("gone",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());

		// XHTML
		pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im",
				new XHTMLExtensionProvider());

		// Group Chat Invitations
		pm.addExtensionProvider("x", "jabber:x:conference",
				new GroupChatInvitation.Provider());
		// Service Discovery # Items
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#items",
				new DiscoverItemsProvider());
		// Service Discovery # Info
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",
				new DiscoverInfoProvider());
		// Data Forms
		pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());
		// MUC User
		pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user",
				new MUCUserProvider());
		// MUC Admin
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin",
				new MUCAdminProvider());
		// MUC Owner
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner",
				new MUCOwnerProvider());
		// Delayed Delivery
		pm.addExtensionProvider("x", "jabber:x:delay",
				new DelayInformationProvider());
		pm.addExtensionProvider("delay", "urn:xmpp:delay",
				new DelayInformationProvider());
		// Version
		try {
			pm.addIQProvider("query", "jabber:iq:version",
					Class.forName("org.jivesoftware.smackx.packet.Version"));
		} catch (ClassNotFoundException e) {
			System.err
					.println("Can't load class for org.jivesoftware.smackx.packet.Version");
		}
		// VCard
		pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());
		// Offline Message Requests
		pm.addIQProvider("offline", "http://jabber.org/protocol/offline",
				new OfflineMessageRequest.Provider());
		// Offline Message Indicator
		pm.addExtensionProvider("offline",
				"http://jabber.org/protocol/offline",
				new OfflineMessageInfo.Provider());
		// Last Activity
		pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());
		// User Search
		pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());
		// SharedGroupsInfo
		pm.addIQProvider("sharedgroup",
				"http://www.jivesoftware.org/protocol/sharedgroup",
				new SharedGroupsInfo.Provider());

		// JEP-33: Extended Stanza Addressing
		pm.addExtensionProvider("addresses",
				"http://jabber.org/protocol/address",
				new MultipleAddressesProvider());

		// FileTransfer
		pm.addIQProvider("si", "http://jabber.org/protocol/si",
				new StreamInitiationProvider());
		pm.addIQProvider(
				"query",
				"http://jabber.org/protocol/bytestreams",
				new BytestreamsProvider());
		pm.addIQProvider("open", "http://jabber.org/protocol/ibb",
				new OpenIQProvider());
		pm.addIQProvider("data", "http://jabber.org/protocol/ibb",
				new DataPacketProvider());
		pm.addIQProvider("close", "http://jabber.org/protocol/ibb",
				new CloseIQProvider());
		pm.addExtensionProvider("data", "http://jabber.org/protocol/ibb",
				new DataPacketProvider());

		// Privacy
		pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());

		// SHIM
		pm.addExtensionProvider("headers", "http://jabber.org/protocol/shim",
				new HeadersProvider());
		pm.addExtensionProvider("header", "http://jabber.org/protocol/shim",
				new HeaderProvider());

		// PubSub
		pm.addIQProvider("pubsub", "http://jabber.org/protocol/pubsub",
				new PubSubProvider());
		pm.addExtensionProvider("create", "http://jabber.org/protocol/pubsub",
				new SimpleNodeProvider());
		pm.addExtensionProvider("items", "http://jabber.org/protocol/pubsub",
				new ItemsProvider());
		pm.addExtensionProvider("item", "http://jabber.org/protocol/pubsub",
				new ItemProvider());
		pm.addExtensionProvider("subscriptions",
				"http://jabber.org/protocol/pubsub",
				new SubscriptionsProvider());
		pm.addExtensionProvider("subscription",
				"http://jabber.org/protocol/pubsub", new SubscriptionProvider());
		pm.addExtensionProvider("affiliations",
				"http://jabber.org/protocol/pubsub", new AffiliationsProvider());
		pm.addExtensionProvider("affiliation",
				"http://jabber.org/protocol/pubsub", new AffiliationProvider());
		pm.addExtensionProvider("options", "http://jabber.org/protocol/pubsub",
				new FormNodeProvider());
		// PubSub owner
		pm.addIQProvider("pubsub", "http://jabber.org/protocol/pubsub#owner",
				new PubSubProvider());
		pm.addExtensionProvider("configure",
				"http://jabber.org/protocol/pubsub#owner",
				new FormNodeProvider());
		pm.addExtensionProvider("default",
				"http://jabber.org/protocol/pubsub#owner",
				new FormNodeProvider());
		// PubSub event
		pm.addExtensionProvider("event",
				"http://jabber.org/protocol/pubsub#event", new EventProvider());
		pm.addExtensionProvider("configuration",
				"http://jabber.org/protocol/pubsub#event",
				new ConfigEventProvider());
		pm.addExtensionProvider("delete",
				"http://jabber.org/protocol/pubsub#event",
				new SimpleNodeProvider());
		pm.addExtensionProvider("options",
				"http://jabber.org/protocol/pubsub#event",
				new FormNodeProvider());
		pm.addExtensionProvider("items",
				"http://jabber.org/protocol/pubsub#event", new ItemsProvider());
		pm.addExtensionProvider("item",
				"http://jabber.org/protocol/pubsub#event", new ItemProvider());
		pm.addExtensionProvider("retract",
				"http://jabber.org/protocol/pubsub#event",
				new RetractEventProvider());
		pm.addExtensionProvider("purge",
				"http://jabber.org/protocol/pubsub#event",
				new SimpleNodeProvider());

		// Nick Exchange
		pm.addExtensionProvider("nick", "http://jabber.org/protocol/nick",
				new Nick.Provider());

		// Attention
		pm.addExtensionProvider("attention", "urn:xmpp:attention:0",
				new AttentionExtension.Provider());

		// input
		pm.addIQProvider("si", "http://jabber.org/protocol/si",
				new StreamInitiationProvider());
		pm.addIQProvider(
				"query",
				"http://jabber.org/protocol/bytestreams",
				new BytestreamsProvider());
		pm.addIQProvider("open", "http://jabber.org/protocol/ibb",
				new OpenIQProvider());
		pm.addIQProvider("close", "http://jabber.org/protocol/ibb",
				new CloseIQProvider());
		pm.addExtensionProvider("data", "http://jabber.org/protocol/ibb",
				new DataPacketProvider());

	}

}