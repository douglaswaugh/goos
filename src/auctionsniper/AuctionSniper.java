package auctionsniper;

public class AuctionSniper implements AuctionEventListener{

	private SniperListener sniperListener;
	private Auction auction;
	private Boolean isWinning = false;
	private SniperSnapshot snapshot;

	public AuctionSniper(Auction auction, SniperListener sniperListener, String itemId) {
		this.sniperListener = sniperListener;
		this.auction = auction;
		this.snapshot = SniperSnapshot.joining(itemId);
	}
	
	public void auctionClosed() {
		snapshot = snapshot.closed();			
		notifyChange();
	}
	
	public void currentPrice(int price, int increment, PriceSource priceSource){
		this.isWinning = priceSource == PriceSource.FromSniper;
		
		if (this.isWinning){			
			snapshot = snapshot.winning(price);
		} else {
			int bid = price + increment;
			auction.bid(bid);
			snapshot = snapshot.bidding(price, bid);
		}
		
		notifyChange();
	}
	
	private void notifyChange(){
		sniperListener.sniperStateChanged(snapshot);
	}
}