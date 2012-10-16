package auctionsniper.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.objogate.exception.Defect;

import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;

public class SnipersTableModel extends AbstractTableModel implements SniperListener {
	private List<SniperSnapshot> snapshots = new ArrayList<SniperSnapshot>();
	private static String[] STATUS_TEXT = { "Joining", "Bidding", "Winning", "Lost", "Won"};
		
	public int getColumnCount() { return Column.values().length; }
	
	public int getRowCount() { return snapshots.size(); }	
	
	public Object getValueAt(int rowIndex, int columnIndex) {
		return Column.at(columnIndex).valueIn(snapshots.get(rowIndex));
	}
	
	public static String textFor(SniperState state) {
		return STATUS_TEXT[state.ordinal()];
	}

	@Override
	public void sniperStateChanged(SniperSnapshot updatedSnapshot) {
		int row = rowMatching(updatedSnapshot);
		
		snapshots.set(row, updatedSnapshot);
		fireTableRowsUpdated(row,row);	
	}
	
	private int rowMatching(SniperSnapshot updatedSnapshot) {
		for (SniperSnapshot snapshot : snapshots){
			if (snapshot.isSameItemAs(updatedSnapshot)) {
				return snapshots.indexOf(snapshot);				
			}
		}	
		throw new Defect("Cannot find match for " + updatedSnapshot);
	}

	@Override
	public String getColumnName(int column){
		return Column.at(column).name;
	}

	public void addSniper(SniperSnapshot joining) {
		this.snapshots.add(joining);
		fireTableRowsInserted(0, 0);
	}
	
}