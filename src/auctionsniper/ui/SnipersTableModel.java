package auctionsniper.ui;

import javax.swing.table.AbstractTableModel;

import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;

public class SnipersTableModel extends AbstractTableModel {
	private final static SniperSnapshot STARTING_UP = new SniperSnapshot("", 0, 0, SniperState.JOINING);
	private SniperSnapshot snapshot = STARTING_UP;
	private String statusText = MainWindow.STATUS_JOINING;
	private static String[] STATUS_TEXT = { 
		MainWindow.STATUS_JOINING, 
		MainWindow.STATUS_BIDDING, 
		MainWindow.STATUS_WINNING,
		MainWindow.STATUS_LOST,
		MainWindow.STATUS_WON };
		
	public int getColumnCount() { return Column.values().length; }
	
	public int getRowCount() { return 1; }	
	
	public Object getValueAt(int rowIndex, int columnIndex) { 
		switch (Column.at(columnIndex)){
		case ITEM_IDENTIFIER:
			return snapshot.itemId;
		case LAST_PRICE:
			return snapshot.lastPrice;
		case LAST_BID:
			return snapshot.lastBid;
		case SNIPER_STATE:
			return textFor(this.snapshot.state);
		default:
			throw new IllegalArgumentException("No column at " + columnIndex);
		}
	}
	
	public static String textFor(SniperState state) {
		return STATUS_TEXT[state.ordinal()];
	}
	
	public void sniperStatusChanged(SniperSnapshot newSnapshot) {
		this.snapshot = newSnapshot;
		fireTableRowsUpdated(0,0);
	}
}