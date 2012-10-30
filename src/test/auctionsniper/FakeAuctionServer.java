package test.auctionsniper;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import static org.hamcrest.Matchers.equalTo;
import org.hamcrest.Matcher;

import auctionsniper.Main;
import auctionsniper.xmpp.XMPPAuction;
import static org.junit.Assert.assertThat;

public class FakeAuctionServer {
	public static final String ITEM_ID_AS_LOGIN = "auction-%s";
	public static final String AUCTION_RESOURCE = "auction";
	public static final String XMPP_HOSTNAME = "localhost";
	public static final String AUCTION_PASSWORD = "auction";

	private final String itemId;
	private final XMPPConnection connection;
	private Chat currentChat;
	private final SingleMessageListener messageListener = new SingleMessageListener();
	
	public FakeAuctionServer(String itemId) {
		this.itemId = itemId;
		this.connection = new XMPPConnection(XMPP_HOSTNAME);
	}

	public String getItemId() {
		return itemId;
	}

	public void startSellingItem() throws XMPPException {
		connection.connect();
		connection.login(format(ITEM_ID_AS_LOGIN, itemId), AUCTION_PASSWORD, AUCTION_RESOURCE);
		connection.getChatManager().addChatListener(
				new ChatManagerListener(){
					public void chatCreated(Chat chat, boolean createdLocally){
						currentChat = chat;
						chat.addMessageListener(messageListener);
					}					
				});
	}

	public void announceClosed() throws XMPPException {
		currentChat.sendMessage("SOLVersion: 1.1; Event: CLOSE;");		
	}

	public void reportPrice(int price, int increment, String bidder) throws XMPPException {
		currentChat.sendMessage(
			String.format(
				"SOLVeresion: 1.1; Event: PRICE; CurrentPrice: %d; Increment: %d; Bidder: %s;", 
				price, increment, bidder));
	}

	public void hasReceivedJoinRequestFromSniper(String sniperId) throws InterruptedException {
		receivesAMessageMatching(sniperId, equalTo(XMPPAuction.JOIN_COMMAND_FORMAT));
	}

	public void hasReceivedBid(int bid, String sniperId) throws InterruptedException {
		receivesAMessageMatching(sniperId, equalTo(String.format(
				XMPPAuction.BID_COMMAND_FORMAT, bid)));
	}

	public void stop() {
		connection.disconnect();		
	}

	private String format(String target, String arg) {
		return String.format(target, arg);
	}

	private void receivesAMessageMatching(String sniperId, Matcher<? super String> matcher)
			throws InterruptedException {
		messageListener.receivesAMessage(matcher);
		assertThat(currentChat.getParticipant(), equalTo(sniperId));
	}
}