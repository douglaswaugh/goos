package test.auctionsniper;
import static com.objogate.wl.swing.matcher.IterableComponentsMatcher.matching;
import static com.objogate.wl.swing.matcher.JLabelTextMatcher.withLabelText;
import static org.hamcrest.Matchers.equalTo;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.table.JTableHeader;

import org.hamcrest.Matcher;

import auctionsniper.Main;
import auctionsniper.SniperState;
import auctionsniper.ui.MainWindow;

import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JButtonDriver;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JTableDriver;
import com.objogate.wl.swing.driver.JTableHeaderDriver;
import com.objogate.wl.swing.driver.JTextFieldDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;

public class AuctionSniperDriver extends JFrameDriver{
	
	public AuctionSniperDriver(int timeoutMillis){ 
		super(new GesturePerformer(),
				JFrameDriver.topLevelFrame(
						named(Main.MAIN_WINDOW_NAME), 
						showingOnScreen()), 
						new AWTEventQueueProber(timeoutMillis, 100));
	}
	
	public void showSniperStatus(String itemId, int lastPrice, int lastBid, String status){
		JTableDriver table = new JTableDriver(this);
		
		table.hasRow(
			matching(
				withLabelText(itemId)
				,withLabelText(String.valueOf(lastPrice)) 
				,withLabelText(String.valueOf(lastBid))
				,withLabelText(status)
			)			
		);
	}
	
	public void showSniperStatus(String statusText){	
		new JTableDriver(this).hasCell(withLabelText((Matcher)equalTo(statusText)));
	}
	
	public void hasColumnTitles() {
		JTableHeaderDriver headers = new JTableHeaderDriver(this, JTableHeader.class);		
		headers.hasHeaders(matching(withLabelText("Item"), withLabelText("Last Price"), 
				withLabelText("Last Bid"), withLabelText("State")));
	}

	public void startBiddingFor(String itemId) {
		textField(MainWindow.NEW_ITEM_ID_NAME).replaceAllText(itemId);
		bidButton().click();
	}	

	public void startBiddingFor(String itemId, int stopPrice) {
		textField(MainWindow.NEW_ITEM_ID_NAME).replaceAllText(itemId);
		textField(MainWindow.NEW_ITEM_STOP_PRICE_NAME).replaceAllText(String.valueOf(stopPrice));
		bidButton().click();
	}
	
	private JTextFieldDriver textField(String newItemIdName) {
		JTextFieldDriver textField = new JTextFieldDriver(this, JTextField.class, named(newItemIdName));
		textField.focusWithMouse();
		return textField;
	}
	
	private JButtonDriver bidButton() {
		return new JButtonDriver(this, JButton.class, named(MainWindow.JOIN_BUTTON_NAME));
	}
}