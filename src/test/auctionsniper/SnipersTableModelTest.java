package test.auctionsniper;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;

import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import auctionsniper.ui.Column;
import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;

@RunWith(JMock.class)
public class SnipersTableModelTest {
	private final Mockery context = new Mockery();
	private TableModelListener listener = context.mock(TableModelListener.class);
	private final SnipersTableModel model = new SnipersTableModel();	
	
	@Before 
	public void attachModelListener() {
		model.addTableModelListener(listener);
	}
	
	@Test
	public void hasEnoughColumns() {
		assertThat(model.getColumnCount(), equalTo(Column.values().length));
	}
	
	@Test
	public void setsSniperValuesInColumns(){
		SniperSnapshot joining = SniperSnapshot.joining("item id");
		SniperSnapshot bidding = joining.bidding(555, 666);
		context.checking(new Expectations(){{
			allowing(listener).tableChanged(with(anyInsertionEvent()));
			one(listener).tableChanged(with(aChangeInRow(0)));
		}});		
		
		model.addSniper(joining);
		model.sniperStateChanged(bidding);
		
		assertRowMatchesSnapshot(0, bidding);
	};

	@Test
	public void setsUpColumnHeadings(){
		for (Column column: Column.values()){
			assertEquals(column.name, model.getColumnName(column.ordinal()));
		}
	}
	
	@Test
	public void notifiesListenersWhenAddingASniper() {
		SniperSnapshot joining  = SniperSnapshot.joining("item123");
		context.checking(new Expectations() { {
			oneOf(listener).tableChanged(with(anInsertionAtRow(0)));
		}});	
		
		assertEquals(0, model.getRowCount());
		
		model.addSniper(joining);
		
		assertEquals(1, model.getRowCount());
		assertRowMatchesSnapshot(0, joining);
	}

	private void assertRowMatchesSnapshot(int rowIndex, SniperSnapshot snapshot) {
		assertColumnEquals(rowIndex, Column.ITEM_IDENTIFIER, snapshot.itemId);
		assertColumnEquals(rowIndex, Column.LAST_PRICE, snapshot.lastPrice);
		assertColumnEquals(rowIndex, Column.LAST_BID, snapshot.lastBid);
		assertColumnEquals(rowIndex, Column.SNIPER_STATE, SnipersTableModel.textFor(snapshot.state));	
	}

	private void assertColumnEquals(int rowIndex, Column column, Object expected) {
		final int columnIndex = column.ordinal();
		assertEquals(expected, model.getValueAt(rowIndex, columnIndex));
	}

	private Matcher<TableModelEvent> aChangeInRow(int row) {
		return samePropertyValuesAs(new TableModelEvent(model, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
	}
	
	private Matcher<TableModelEvent> anInsertionAtRow(int rowIndex) {
		return samePropertyValuesAs(new TableModelEvent(model, rowIndex, rowIndex, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
	}
	
	private Matcher<TableModelEvent> anyInsertionEvent() {
		return hasProperty("type", equalTo(TableModelEvent.INSERT));
	}
}