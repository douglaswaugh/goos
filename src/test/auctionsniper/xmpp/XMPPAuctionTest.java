package test.auctionsniper.xmpp;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.junit.Before;
import org.junit.Test;

import auction.FakeAuctionServer;
import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import auctionsniper.ui.ApplicationRunner;
import auctionsniper.xmpp.XMPPAuction;

public class XMPPAuctionTest {
	private static final String XMPP_HOSTNAME = "localhost";
	private static final String SNIPER_ID = "sniper";
	private static final String SNIPER_PASSWORD = "sniper";
	private static final String AUCTION_RESOURCE = "auction";
	private XMPPConnection connection;
	private static final FakeAuctionServer auctionServer = new FakeAuctionServer("item-54321");

	@Before
	public void startUp() throws XMPPException {		
		connection = connection(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD);		
		auctionServer.startSellingItem();
	}
	
	@Test
	public void receivesEventsFromAuctionServerAfterJoining() throws Exception{
		CountDownLatch auctionWasClosed = new CountDownLatch(1);
		
		Auction auction = new XMPPAuction(connection, auctionServer.getItemId());
		auction.addAuctionEventListener(auctionClosedListener(auctionWasClosed));
		
		auction.join();
		auctionServer.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		auctionServer.announceClosed();
		
		assertTrue("should have been closed", auctionWasClosed.await(2, TimeUnit.SECONDS));
	}
	
	private AuctionEventListener auctionClosedListener(final CountDownLatch auctionWasClosed) {
		return new AuctionEventListener() {
			public void auctionClosed() {
				auctionWasClosed.countDown();
			}
			public void currentPrice(int price, int increment, PriceSource priceSource) {
				// not implemented
			}
		};
	}
	
	private static XMPPConnection connection(String hostname, String username,
			String password) throws XMPPException {
		XMPPConnection connection = new XMPPConnection(hostname);
		connection.connect();
		connection.login(username, password, AUCTION_RESOURCE);
		return connection;
	}
}
