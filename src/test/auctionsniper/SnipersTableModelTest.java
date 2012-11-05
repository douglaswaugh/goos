package test.auctionsniper;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import auctionsniper.AuctionSniper;
import auctionsniper.SniperPortfolio;
import auctionsniper.SniperSnapshot;
import auctionsniper.ui.Column;
import auctionsniper.ui.Item;
import auctionsniper.ui.SnipersTableModel;

import com.objogate.exception.Defect;

@RunWith(JMock.class)
public class SnipersTableModelTest {
	private final Mockery context = new Mockery();
	private final TableModelListener listener = context.mock(TableModelListener.class);
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
		AuctionSniper joining = new AuctionSniper(new Item("item id", Integer.MAX_VALUE), null);
		SniperSnapshot bidding = joining.getSnapshot().bidding(555, 666);
		context.checking(new Expectations(){{
			allowing(listener).tableChanged(with(anyInsertionEvent()));
			one(listener).tableChanged(with(aChangeInRow(0)));
		}});		
		
		model.sniperAdded(joining);
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
		AuctionSniper joining = new AuctionSniper(new Item("item123", Integer.MAX_VALUE), null);
		context.checking(new Expectations() { {
			oneOf(listener).tableChanged(with(anInsertionAtRow(0)));
		}});	
		
		assertEquals(0, model.getRowCount());
		
		model.sniperAdded(joining);
		
		assertEquals(1, model.getRowCount());
		assertRowMatchesSnapshot(0, joining.getSnapshot());
	}
	
	@Test
	public void holdsSniperInAdditionOrder() {
		AuctionSniper joining = new AuctionSniper(new Item("item 0", Integer.MAX_VALUE), null);
		AuctionSniper joining2 = new AuctionSniper(new Item("item 1", Integer.MAX_VALUE), null);
		context.checking(new Expectations() {{
			ignoring(listener);
		}});
		
		model.sniperAdded(joining);
		model.sniperAdded(joining2);
		
		assertEquals("item 0", cellValue(0, Column.ITEM_IDENTIFIER));
		assertEquals("item 1", cellValue(1, Column.ITEM_IDENTIFIER));
	}
	
	@Test
	public void updatesCorrectRowForSniper() {	
		AuctionSniper joining = new AuctionSniper(new Item("item 0", Integer.MAX_VALUE), null);
		AuctionSniper joining2 = new AuctionSniper(new Item("item 1", Integer.MAX_VALUE), null);
		context.checking(new Expectations(){{
			ignoring(listener);
		}});
		
		model.sniperAdded(joining);
		model.sniperAdded(joining2);
		SniperSnapshot bidding = joining2.getSnapshot().bidding(555, 666);
		model.sniperStateChanged(bidding);
		
		assertRowMatchesSnapshot(1, bidding);
	}
	
	@Test (expected=Defect.class)
	public void throwsDefectIfNoExistingSniperForAnUpdate() {
		AuctionSniper joining = new AuctionSniper(new Item("item 0", Integer.MAX_VALUE), null);
		AuctionSniper joining2 = new AuctionSniper(new Item("item 1", Integer.MAX_VALUE), null);
		context.checking(new Expectations() {{
			ignoring(listener);
		}});
		
		model.sniperAdded(joining);
		model.sniperStateChanged(joining2.getSnapshot());
	}
	
	private String cellValue(int row, Column column) {
		return (String)model.getValueAt(row, column.ordinal());
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