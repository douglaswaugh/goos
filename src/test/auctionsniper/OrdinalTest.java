package test.auctionsniper;

import org.junit.Test;

import com.objogate.exception.Defect;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class OrdinalTest {
	public enum OrdinalEnum 
	{ 
		JOINING {
			@Override 
			public OrdinalEnum whenClosed() { return SOMETHING; }
		}, 
		BIDDING {
			@Override 
			public OrdinalEnum whenClosed() { return SOMETHING; }
		}, 
		SOMETHING;		
		
		public OrdinalEnum whenClosed() {
			throw new Defect("Something has gone wrong with your programming");
		}
	};
	
	@Test
	public void ThisIsJustATest(){
		
		OrdinalEnum testOrdinal = OrdinalEnum.BIDDING;
		
		assertThat(testOrdinal.ordinal(), equalTo(1));
	}
}