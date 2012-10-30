package auctionsniper.ui;

import auction.FakeAuctionServer;
import auctionsniper.Main;
import auctionsniper.SniperState;

public class ApplicationRunner {
	public static final String XMPP_HOSTNAME = "localhost";
	public static final String SNIPER_ID = "sniper";
	public static final String SNIPER_PASSWORD = "sniper";
	public static final String SNIPER_XMPP_ID = "sniper@localhost/auction";
	private static final SniperState JOINING = SniperState.JOINING;
	private AuctionSniperDriver driver;
	
	public void startBiddingIn(final FakeAuctionServer... auctions){
		startSniper();
		for (FakeAuctionServer auction : auctions) {
			final String itemId = auction.getItemId();
			driver.startBiddingFor(itemId);
			driver.showSniperStatus(auction.getItemId(), 0, 0, SnipersTableModel.textFor(JOINING));
		}
	}

	private void startSniper() {
		Thread thread = new Thread("Test Application"){
			@Override public void run (){
				try{
					Main.main(arguments());
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
	}

	protected static String[] arguments() {
		String[] arguments = new String[3];
		arguments[0] = XMPP_HOSTNAME;
		arguments[1] = SNIPER_ID;
		arguments[2] = SNIPER_PASSWORD;
		
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
