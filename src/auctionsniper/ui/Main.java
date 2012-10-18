package auctionsniper.ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import auctionsniper.AuctionSniper;
import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;
import auctionsniper.xmpp.AuctionMessageTranslator;
import auctionsniper.xmpp.XMPPAuction;
import auctionsniper.xmpp.XMPPAuctionHouse;


public class Main {
	private final SnipersTableModel snipers = new SnipersTableModel();
	private MainWindow ui;
	private List<Auction> notToBeGCd = new ArrayList<Auction>();
	private static final int ARG_HOSTNAME = 0;
	private static final int ARG_USERNAME = 1;
	private static final int ARG_PASSWORD = 2;
	public static final String STATUS_LOST = "Lost"; 
	public static final String STATUS_BIDDING = "Bidding";
	public static final String STATUS_JOINING = "Joining";
	public static final String STATUS_WON = "Won";
	public static final String STATUS_WINNING = "Winning";
	public static final String SNIPER_STATUS_NAME = "sniper status";
	public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";	
	public Main() throws Exception {
		startUserInterface();
	}

	public static void main(String...args) throws Exception {
		Main main = new Main();
		XMPPAuctionHouse auctionHouse = XMPPAuctionHouse.connect(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]);
		main.disconnectWhenUICloses(auctionHouse);
		main.addUserRequestListenerFor(auctionHouse);
	}

	private void startUserInterface() throws InterruptedException, InvocationTargetException {
		SwingUtilities.invokeAndWait(new Runnable(){
			public void run() {
				ui = new MainWindow(snipers);
			}
		});		
	}

	private void addUserRequestListenerFor(final XMPPAuctionHouse auctionHouse) {
		ui.addUserRequestListener(new UserRequestListener() {
			public void joinAuction(String itemId) {
				snipers.addSniper(SniperSnapshot.joining(itemId));				
				Auction auction = auctionHouse.auctionFor(itemId);
				notToBeGCd.add(auction);	
				auction.addAuctionEventListener(new AuctionSniper(itemId, auction, new SwingThreadSniperListener(snipers)));
				auction.join();
			}			
		});		
	}

	private void disconnectWhenUICloses(final XMPPAuctionHouse auctionHouse) {
		ui.addWindowListener(new WindowAdapter() {
			@Override public void windowClosed(WindowEvent e){
				auctionHouse.disconnect();
			}
		});		
	}

	public class SwingThreadSniperListener implements SniperListener{

		private final SniperListener listener;
		
		public SwingThreadSniperListener(SniperListener listener) {
			this.listener = listener;
		}
		
		@Override
		public void sniperStateChanged(final SniperSnapshot state) {
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					listener.sniperStateChanged(state);
				}
			});
		}
	}
}
 