package auctionsniper.ui;

import java.util.ArrayList;

import auctionsniper.AuctionSniper;
import auctionsniper.SniperCollector;
import auctionsniper.SniperListener;

public class SniperPortfolio implements SniperCollector{
	private final ArrayList<AuctionSniper> snipers = new ArrayList<AuctionSniper>();
	private final Announcer<SniperListener> sniperListeners = Announcer.to(SniperListener.class);
	
	@Override
	public void addSniper(AuctionSniper sniper) {
		snipers.add(sniper);
		sniperListeners.announce().sniperStateChanged(sniper.getSnapshot());
	}

	public void addPortfolioListener(SniperListener listener) {
		sniperListeners.addListener(listener);
	}
}