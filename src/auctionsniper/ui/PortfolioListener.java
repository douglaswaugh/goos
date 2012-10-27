package auctionsniper.ui;

import java.util.EventListener;

import auctionsniper.AuctionSniper;

public interface PortfolioListener extends EventListener {
	void sniperAdded(AuctionSniper sniper);
}
