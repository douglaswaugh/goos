package auctionsniper;

import java.util.EventListener;

import auctionsniper.ui.Item;

public interface UserRequestListener extends EventListener {
	void joinAuction(Item item);
}
