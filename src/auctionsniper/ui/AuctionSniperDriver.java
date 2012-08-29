package auctionsniper.ui;
import javax.swing.JComponent;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import static org.hamcrest.Matchers.equalTo;
import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JTableDriver;
import static com.objogate.wl.swing.matcher.JLabelTextMatcher.withLabelText;
import static com.objogate.wl.swing.matcher.IterableComponentsMatcher.matching;
import com.objogate.wl.swing.gesture.GesturePerformer;

public class AuctionSniperDriver extends JFrameDriver{
	
	public AuctionSniperDriver(int timeoutMillis){ 
		super(new GesturePerformer(),
				JFrameDriver.topLevelFrame(
						named(Main.MAIN_WINDOW_NAME), 
						showingOnScreen()), 
						new AWTEventQueueProber(timeoutMillis, 100));
	}
	
	public void showSniperStatus(String itemId, int lastPrice, int lastBid, String statusText){
		JTableDriver table = new JTableDriver(this);
		
		table.hasRow(
			matching(
				withLabelText(itemId)
				,withLabelText(String.valueOf(lastPrice)) 
				,withLabelText(String.valueOf(lastBid))
				,withLabelText(statusText)
			)			
		);
	}
	
	public void showSniperStatus(String statusText){	
		new JTableDriver(this).hasCell(withLabelText((Matcher)equalTo(statusText)));
	}
}