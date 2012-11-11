package auctionsniper;

import auctionsniper.ui.Item;

public class AuctionSniper implements AuctionEventListener{

	private SniperListener sniperListener;
	private Auction auction;
	private SniperSnapshot snapshot;
	private Item item;	
	private final Announcer<SniperListener> listeners = Announcer.to(SniperListener.class);

	public AuctionSniper(Item item, Auction auction) {
		this.auction = auction;
		this.item = item;
		this.snapshot = SniperSnapshot.joining(item.identifier);
	}
	
	public SniperSnapshot getSnapshot(){
		return snapshot;
	}
	
	public void addSniperListener(SniperListener sniperListener) {
		this.listeners.addListener(sniperListener);
	}
	
	public void auctionClosed() {
		snapshot = snapshot.closed();			
		notifyChange();
	}
	
	public void currentPrice(int price, int increment, PriceSource priceSource){
		switch(priceSource) {
		case FromSniper:
			snapshot = snapshot.winning(price);
			break;
		case FromOtherBidder:
			int bid = price + increment;
			if (item.allowsBid(bid)) {
				auction.bid(bid);
				snapshot = snapshot.bidding(price, bid);
			} else {
				snapshot = snapshot.losing(price);
			}			
			break;
		}
		
		notifyChange();
	}
	
	public void auctionFailed() {
		snapshot = snapshot.failed();
		notifyChange();
	}
	
	private void notifyChange(){
		listeners.announce().sniperStateChanged(snapshot);
	}
}