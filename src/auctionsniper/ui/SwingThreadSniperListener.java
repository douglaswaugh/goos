package auctionsniper.ui;

import javax.swing.SwingUtilities;

import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;

public class SwingThreadSniperListener implements SniperListener{

	private final SniperListener listener;
	
	public SwingThreadSniperListener(SniperListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void sniperStateChanged(final SniperSnapshot state) {
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				listener.sniperStateChanged(state);
			}
		});
	}
}
