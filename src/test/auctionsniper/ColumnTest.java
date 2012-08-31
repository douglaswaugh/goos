package test.auctionsniper;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import auctionsniper.ui.Column;

public class ColumnTest {
	SniperSnapshot snapshot = new SniperSnapshot("auction-98765", 100, 110, SniperState.JOINING);
	
	@Test
	public void shouldReturnItemId(){
		assertEquals((String)Column.ITEM_IDENTIFIER.valueIn(snapshot), "auction-98765");
	}
	
	@Test
	public void shouldReturnLastPrice(){
		assertEquals(Column.LAST_PRICE.valueIn(snapshot), 100);
	}
	
	@Test
	public void shouldReturnLastBid(){
		assertEquals(Column.LAST_BID.valueIn(snapshot), 110);
	}
	
	@Test
	public void shouldReturnState(){
		assertEquals(Column.SNIPER_STATE.valueIn(snapshot), "Joining");
	}
}
