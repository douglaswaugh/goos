package auctionsniper.xmpp;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.Auction;
import auctionsniper.AuctionHouse;

public class XMPPAuctionHouse implements AuctionHouse {
	public static final String AUCTION_RESOURCE = "auction";
	private static XMPPConnection connection;

	@Override
	public Auction auctionFor(String itemId) {
		return new XMPPAuction(connection, itemId);
	}

	public static XMPPAuctionHouse connect(String hostname, String username, String password) throws XMPPException {
		connection = connection(hostname, username, password);
		return new XMPPAuctionHouse();
	}

	private static XMPPConnection connection(String hostname, String username,
			String password) throws XMPPException {
		XMPPConnection connection = new XMPPConnection(hostname);
		connection.connect();
		connection.login(username, password, AUCTION_RESOURCE);
		return connection;
	}

	public void disconnect() {
		connection.disconnect();		
	}
}
