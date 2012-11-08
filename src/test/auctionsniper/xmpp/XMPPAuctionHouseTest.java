package test.auctionsniper.xmpp;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import test.auctionsniper.ApplicationRunner;
import test.auctionsniper.FakeAuctionServer;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import auctionsniper.AuctionHouse;
import auctionsniper.xmpp.XMPPAuction;
import auctionsniper.xmpp.XMPPAuctionHouse;

public class XMPPAuctionHouseTest {
	private static final String XMPP_HOSTNAME = "localhost";
	private static final String SNIPER_ID = "sniper";
	private static final String SNIPER_PASSWORD = "sniper";
	private static final FakeAuctionServer auctionServer = new FakeAuctionServer("item-54321");
	private Mockery context = new Mockery();
	private AuctionHouse auctionHouse;

	@Before
	public void startAuction() throws XMPPException {	
		auctionServer.startSellingItem();	
	}
	
	@Before
	public void connect() throws XMPPException {
		auctionHouse = XMPPAuctionHouse.connect(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD);
	}
	
	@After
	public void stopAuction(){
		auctionServer.stop();
	}
	
	@After
	public void disconnect(){
		if (auctionHouse != null){
			auctionHouse.disconnect();
		}
	}
	
	@Test
	public void receivesEventsFromAuctionServerAfterJoining() throws Exception{
		CountDownLatch auctionWasClosed = new CountDownLatch(1);
				
		Auction auction = auctionHouse.auctionFor(auctionServer.getItemId());
		auction.addAuctionEventListener(auctionClosedListener(auctionWasClosed));
		
		auction.join();
		auctionServer.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
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
			public void auctionFailed() {
				// TODO Auto-generated method stub
			}
		};
	}
}
