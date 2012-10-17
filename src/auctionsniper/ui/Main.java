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
import auctionsniper.AuctionMessageTranslator;
import auctionsniper.AuctionSniper;
import auctionsniper.Dummy;
import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;


public class Main {
	private final SnipersTableModel snipers = new SnipersTableModel();
	private MainWindow ui;
	private List<Chat> notToBeGCd = new ArrayList<Chat>();
	private static final int ARG_HOSTNAME = 0;
	private static final int ARG_USERNAME = 1;
	private static final int ARG_PASSWORD = 2;
	private static final int ARG_ITEM_ID = 3;
	private static final String AUCTION_RESOURCE = "auction";
	private static final String ITEM_ID_AS_LOGIN = "auction-%s";
	private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;
	public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";
	public static final String JOIN_COMMAND_FORMAT = "SOL Version 1.1; Command: Join";
	public static final String STATUS_LOST = "Lost"; 
	public static final String STATUS_BIDDING = "Bidding";
	public static final String STATUS_JOINING = "Joining";
	public static final String SNIPER_STATUS_NAME = "sniper status";
	public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
	public static final String STATUS_WON = "Won";
	public static final String STATUS_WINNING = "Winning";
	
	public Main() throws Exception {
		startUserInterface();
	}

	public static void main(String...args) throws Exception {
		Main main = new Main();
		XMPPConnection connection = connection(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]);
		main.disconnectWhenUICloses(connection);
		main.addUserRequestListenerFor(connection);
	}

	private void startUserInterface() throws InterruptedException, InvocationTargetException {
		SwingUtilities.invokeAndWait(new Runnable(){
			public void run() {
				ui = new MainWindow(snipers);
			}
		});		
	}

	private void addUserRequestListenerFor(final XMPPConnection connection) {
		ui.addUserRequestListener(new UserRequestListener() {
			public void joinAuction(String itemId) {
				snipers.addSniper(SniperSnapshot.joining(itemId));
				
				final Chat chat = connection.getChatManager()
						.createChat(auctionId(itemId, connection), null);
				notToBeGCd.add(chat);		
				
				Auction auction = new XMPPAuction(chat);
				
				chat.addMessageListener(
						new AuctionMessageTranslator(
								connection.getUser(), 
								new AuctionSniper(
										auction, 
										new SwingThreadSniperListener(snipers), 
										itemId)));
				
				auction.join();
			}			
		});			
	}

	private void disconnectWhenUICloses(final XMPPConnection connection) {
		ui.addWindowListener(new WindowAdapter(){
			@Override public void windowClosed(WindowEvent e){
				connection.disconnect();
			}
		});		
	}

	private static String auctionId(String itemId, XMPPConnection connection) {
		return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
	}

	private static XMPPConnection connection(String hostname, String username,
			String password) throws XMPPException {
		XMPPConnection connection = new XMPPConnection(hostname);
		connection.connect();
		connection.login(username, password, AUCTION_RESOURCE);
		return connection;
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
	
	public class XMPPAuction implements Auction {
		private final Chat chat;	
		
		public XMPPAuction(Chat chat) {
			this.chat = chat;
		}
		
		public void bid(int amount) {
			sendMessage(String.format(BID_COMMAND_FORMAT, amount));
		}
		
		public void join()
		{
			sendMessage(JOIN_COMMAND_FORMAT);
		}

		private void sendMessage(final String message) {
			try {			
				chat.sendMessage(message);
			} catch (XMPPException e) {
				e.printStackTrace();
			}
		}
	}
}
 