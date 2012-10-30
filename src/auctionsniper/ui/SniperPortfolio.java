package auctionsniper.ui;

import java.util.ArrayList;

import auctionsniper.AuctionSniper;
import auctionsniper.SniperCollector;

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