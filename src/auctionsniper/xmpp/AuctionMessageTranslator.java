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
		try{
			translate(message.getBody());	
		} catch (Exception parseException) {
			listener.auctionFailed();
		}		
	}

	private void translate(String body) throws MissingValueException {
		AuctionEvent event = AuctionEvent.from(body);		

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
		private final HashMap<String, String> values = new HashMap<String, String>();
		
		public String type() throws MissingValueException {
			return get("Event");
		}
		
		public PriceSource isFrom(String sniperId) throws MissingValueException {
			return sniperId.equals(bidder()) ? PriceSource.FromSniper : PriceSource.FromOtherBidder;
		}

		private String bidder() throws MissingValueException {
			return get("Bidder");
		}

		public int currentPrice() throws NumberFormatException, MissingValueException {
			return getInt("CurrentPrice");
		}
		
		public int increment() throws NumberFormatException, MissingValueException {
			return getInt("Increment");
		}		
		
		static AuctionEvent from(String body) {
			AuctionEvent event = new AuctionEvent();
			for (String element : fieldsIn(body)){
				event.addField(element);
			}
			return event;
		}

		private static String[] fieldsIn(String body) {
			return body.split(";");
		}

		private void addField(String element) {
			String[] pair = element.split(":");
			values.put(pair[0].trim(), pair[1].trim());
		}

		private int getInt(String fieldName) throws NumberFormatException, MissingValueException {
			return Integer.parseInt(get(fieldName));
		}

		private String get(String name) throws MissingValueException {
			String value = values.get(name);
			if (null == value) {
				throw new MissingValueException(name);
			}
			return value;			
		}
	}
}