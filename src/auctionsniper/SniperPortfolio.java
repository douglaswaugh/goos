package auctionsniper;

import java.util.ArrayList;

import auctionsniper.ui.Announcer;

public class SniperPortfolio implements SniperCollector{
	private final ArrayList<AuctionSniper> snipers = new ArrayList<AuctionSniper>();
	private final Announcer<PortfolioListener> listeners = Announcer.to(PortfolioListener.class);
	
	@Override
	public void addSniper(AuctionSniper sniper) {
		snipers.add(sniper);
		listeners.announce().sniperAdded(sniper);
	}

	public void addPortfolioListener(PortfolioListener listener) {
		listeners.addListener(listener);
	}
}