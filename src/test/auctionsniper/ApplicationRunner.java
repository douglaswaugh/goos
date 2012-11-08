package test.auctionsniper;

import auctionsniper.Main;
import auctionsniper.SniperState;
import auctionsniper.ui.MainWindow;
import static auctionsniper.ui.SnipersTableModel.textFor;

public class ApplicationRunner {
	public static final String XMPP_HOSTNAME = "localhost";
	public static final String SNIPER_ID = "sniper";
	public static final String SNIPER_PASSWORD = "sniper";
	public static final String SNIPER_XMPP_ID = "sniper@localhost/auction";
	private static final SniperState JOINING = SniperState.JOINING;
	private AuctionSniperDriver driver;
	
	public void startBiddingIn(final FakeAuctionServer... auctions) {
		startSniper();
		for (FakeAuctionServer auction : auctions) {
			final String itemId = auction.getItemId();
			driver.startBiddingFor(itemId, Integer.MAX_VALUE);
			driver.showSniperStatus(auction.getItemId(), 0, 0, textFor(JOINING));
		}
	}

	public void startBiddingWithStopPrice(FakeAuctionServer auction, int stopPrice) {
		startSniper();
		final String itemId = auction.getItemId();
		driver.startBiddingFor(itemId, stopPrice);
		driver.showSniperStatus(auction.getItemId(), 0, 0, textFor(JOINING));
	}
	
	public void stop(){
		if (driver != null){
			driver.dispose();
		}
	} 

	public void hasShownSniperIsBidding(FakeAuctionServer auction, int lastPrice, int lastBid) throws InterruptedException {
		driver.showSniperStatus(auction.getItemId(), lastPrice, lastBid, textFor(SniperState.BIDDING));
	}

	public void hasShownSniperIsWinning(FakeAuctionServer auction, int winningBid) {
		driver.showSniperStatus(auction.getItemId(), winningBid, winningBid, textFor(SniperState.WINNING));		
	}

	public void hasShownSniperIsLosing(FakeAuctionServer auction, int lastPrice, int lastBid) {
		driver.showSniperStatus(auction.getItemId(), lastPrice, lastBid, textFor(SniperState.LOSING));
	}

	public void showsSniperHasWonAuction(FakeAuctionServer auction, int lastPrice) {
		driver.showSniperStatus(auction.getItemId(), lastPrice, lastPrice, textFor(SniperState.WON));		
	}

	public void showsSniperHasLostAuction() {
		driver.showSniperStatus(Main.STATUS_LOST);
	}

	public void showsSniperHasLostAuction(FakeAuctionServer auction, int lastPrice, int lastBid) {
		driver.showSniperStatus(auction.getItemId(), lastPrice, lastBid, textFor(SniperState.LOST));
	}

	public void showsSniperHasFailed(FakeAuctionServer auction) {
		driver.showSniperStatus(auction.getItemId(), 0, 0, Main.STATUS_FAILED);
	}

	public void reportsInvalidMessage(FakeAuctionServer auction, String brokenMessage) {
		
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

	private static String[] arguments() {
		String[] arguments = new String[3];
		arguments[0] = XMPP_HOSTNAME;
		arguments[1] = SNIPER_ID;
		arguments[2] = SNIPER_PASSWORD;
		
		return arguments;		
	}
}
