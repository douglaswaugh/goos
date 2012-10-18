package auctionsniper.xmpp;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import auctionsniper.ui.Announcer;
import auctionsniper.ui.Main;

public class XMPPAuction implements Auction {
	private final Chat chat;	
	private final Announcer<AuctionEventListener> auctionEventListeners = Announcer.to(AuctionEventListener.class);
	private static final String AUCTION_RESOURCE = "auction";
	private static final String ITEM_ID_AS_LOGIN = "auction-%s";
	private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;	
	
	public XMPPAuction(XMPPConnection connection, String itemId) {
		this.chat = connection.getChatManager().createChat(
				auctionId(itemId, connection), 
				new AuctionMessageTranslator(connection.getUser(), 
						auctionEventListeners.announce()));
	}
	
	public void bid(int amount) {
		sendMessage(String.format(Main.BID_COMMAND_FORMAT, amount));
	}
	
	public void join() {
		sendMessage(Main.JOIN_COMMAND_FORMAT);
	}
	
	public Announcer<AuctionEventListener> getAuctionEventListeners() {
		return auctionEventListeners;
	}

	private void sendMessage(final String message) {
		try {			
			chat.sendMessage(message);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

	public void addAuctionEventListener(AuctionEventListener eventListener) {
		auctionEventListeners.addListener(eventListener);
	}	

	private static String auctionId(String itemId, XMPPConnection connection) {
		return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
	}
}