package auctionsniper.xmpp;

import java.util.HashMap;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import auctionsniper.AuctionEventListener;
import auctionsniper.AuctionEventListener.PriceSource;

public class AuctionMessageTranslator implements MessageListener {

	private AuctionEventListener listener;
	private final String sniperId;

	public AuctionMessageTranslator(String sniperId, AuctionEventListener auctionSniper) {
		this.sniperId = sniperId;
		this.listener = auctionSniper;		
	}

	@Override
	public void processMessage(Chat chat, Message message) {
		AuctionEvent event = AuctionEvent.from(message);		

		String type = event.type();		
		if ("CLOSE".equals(type)){
			listener.auctionClosed();			
		} else if ("PRICE".equals(type)){			
			listener.currentPrice(event.currentPrice(), 
								  event.increment(),
								  event.isFrom(sniperId));
		}
	}
	
	private static class AuctionEvent {
		private static final HashMap<String, String> fields = new HashMap<String, String>();
		
		public String type() {
			return get("Event");
		}
		
		public PriceSource isFrom(String sniperId) {
			return sniperId.equals(bidder()) ? PriceSource.FromSniper : PriceSource.FromOtherBidder;
		}

		private String bidder() {
			return get("Bidder");
		}

		public int currentPrice() {
			return getInt("CurrentPrice");
		}
		
		public int increment() {
			return getInt("Increment");
		}		
		
		static AuctionEvent from(Message message) {
			AuctionEvent event = new AuctionEvent();
			for (String element : fieldsIn(message)){
				addField(element);
			}
			return event;
		}

		private static String[] fieldsIn(Message message) {
			return message.getBody().split(";");
		}

		private static void addField(String element) {
			String[] pair = element.split(":");
			fields.put(pair[0].trim(), pair[1].trim());
		}

		private int getInt(String fieldName) {
			return Integer.parseInt(get(fieldName));
		}

		private String get(String fieldName) {
			return fields.get(fieldName);
		}
	}
}