package test.auctionsniper;

import org.junit.After;
import org.junit.Test;

import auction.FakeAuctionServer;
import auctionsniper.ui.ApplicationRunner;

public class AuctionSniperEndToEndTest {
	private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
	private final ApplicationRunner application = new ApplicationRunner();
	
	@Test 
	public void sniperJoinsAuctionUntilAuctionCloses() throws Exception {
		auction.startSellingItem();
		application.startBiddingIn(auction);
		auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		auction.announceClosed();
		application.showsSniperHasLostAuction();
	}	
	
	@Test 
	public void sniperMakesAHigherBidButLoses() throws Exception {
		auction.startSellingItem();
		application.startBiddingIn(auction);
		auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		auction.reportPrice(1000, 98, "other bidder");
		application.hasShownSniperIsBiding(1000, 1098);
		auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);
		auction.announceClosed();
		application.showsSniperHasLostAuction();
	}
	
	@Test 
	public void sniperWinsAnAuctionByBiddingHigher() throws Exception {
		auction.startSellingItem();
		application.startBiddingIn(auction);
		auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		auction.reportPrice(1000, 98, "other bidder");
		application.hasShownSniperIsBiding(1000,1098);
		auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);
		auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
		application.hasShownSniperIsWinning(1098);
		auction.announceClosed();
		application.showsSniperHasWonAuction(1098);
	}
	
	@After 
	public void stopAuction(){
		auction.stop();
	}
	
	@After 
	public void stopApplication(){
		application.stop();
	}
}
