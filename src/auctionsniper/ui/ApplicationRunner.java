package auctionsniper.ui;

import auction.FakeAuctionServer;
import auctionsniper.SniperState;

public class ApplicationRunner {
	public static final String XMPP_HOSTNAME = "localhost";
	public static final String SNIPER_ID = "sniper";
	public static final String SNIPER_PASSWORD = "sniper";
	public static final String SNIPER_XMPP_ID = "sniper@localhost/auction";
	private static final SniperState JOINING = SniperState.JOINING;
	private AuctionSniperDriver driver;
	
	public void startBiddingIn(final FakeAuctionServer... auctions){
		Thread thread = new Thread("Test Application"){
			@Override public void run (){
				try{
					Main.main(arguments(auctions));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
		driver = new AuctionSniperDriver(1000);
		driver.hasTitle(MainWindow.APPLICATION_TITLE);
		driver.hasColumnTitles();
		for (FakeAuctionServer auction : auctions) {
			driver.showSniperStatus(auction.getItemId(), 0, 0, SnipersTableModel.textFor(JOINING));
			System.out.println("check ui shows sniper status");
			//driver.showSniperStatus(SnipersTableModel.textFor(JOINING));
		}
		
	}

	protected static String[] arguments(FakeAuctionServer[] auctions) {
		String[] arguments = new String[auctions.length + 3];
		arguments[0] = XMPP_HOSTNAME;
		arguments[1] = SNIPER_ID;
		arguments[2] = SNIPER_PASSWORD;
		for (int i = 0; i < auctions.length; i++){
			arguments[i + 3] = auctions[i].getItemId();
		}
		
		return arguments;		
	}
	
	public void stop(){
		if (driver != null){
			driver.dispose();
		}
	} 

	public void showsSniperHasLostAuction() {
		driver.showSniperStatus(Main.STATUS_LOST);
	}

	public void hasShownSniperIsBiding(FakeAuctionServer auction, int lastPrice, int lastBid) throws InterruptedException {
		driver.showSniperStatus(auction.getItemId(), lastPrice, lastBid, Main.STATUS_BIDDING);
	}

	public void showsSniperHasWonAuction(FakeAuctionServer auction, int lastPrice) {
		driver.showSniperStatus(auction.getItemId(), lastPrice, lastPrice, Main.STATUS_WON);		
	}

	public void hasShownSniperIsWinning(FakeAuctionServer auction, int winningBid) {
		driver.showSniperStatus(auction.getItemId(), winningBid, winningBid, Main.STATUS_WINNING);		
	}
}
