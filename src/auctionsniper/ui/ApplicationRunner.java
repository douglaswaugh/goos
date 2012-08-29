package auctionsniper.ui;

import auction.FakeAuctionServer;

public class ApplicationRunner {
	public static final String XMPP_HOSTNAME = "localhost";
	public static final String SNIPER_ID = "sniper";
	public static final String SNIPER_PASSWORD = "sniper";
	public static final String SNIPER_XMPP_ID = "sniper@localhost/auction";	
	private AuctionSniperDriver driver;
	private String itemId;
	
	public void startBiddingIn(final FakeAuctionServer auction){
		itemId = auction.getItemId();
		Thread thread = new Thread("Test Application"){
			@Override public void run (){
				try{
					Main.main(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD, auction.getItemId());
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
		driver = new AuctionSniperDriver(1000);
		driver.showSniperStatus(Main.STATUS_JOINING);
	}
	
	public void stop(){
		if (driver != null){
			driver.dispose();
		}
	} 

	public void showsSniperHasLostAuction() {
		driver.showSniperStatus(Main.STATUS_LOST);
	}

	public void hasShownSniperIsBiding(int lastPrice, int lastBid) throws InterruptedException {
		driver.showSniperStatus(itemId, lastPrice, lastBid, Main.STATUS_BIDDING);
	}

	public void showsSniperHasWonAuction(int lastPrice) {
		driver.showSniperStatus(itemId, lastPrice, lastPrice, Main.STATUS_WON);		
	}

	public void hasShownSniperIsWinning(int winningBid) {
		driver.showSniperStatus(itemId, winningBid, winningBid, Main.STATUS_WINNING);		
	}
}
