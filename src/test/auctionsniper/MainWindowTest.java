package test.auctionsniper;

import org.junit.After;
import org.junit.Test;

import com.objogate.wl.swing.probe.ValueMatcherProbe;

import auctionsniper.SniperPortfolio;
import auctionsniper.UserRequestListener;
import auctionsniper.ui.Item;
import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;
import static org.hamcrest.Matchers.*;

public class MainWindowTest {
	private final SniperPortfolio portfolio = new SniperPortfolio();
	private final MainWindow mainWindow = new MainWindow(portfolio);
	private final AuctionSniperDriver driver = new AuctionSniperDriver(100);
	
	@Test
	public void makeUserRequestsWhenJoinButtonClicked() {
		final ValueMatcherProbe<Item> itemProbe = new ValueMatcherProbe<Item>(equalTo(new Item("an item-id", 789)), "join request");
		mainWindow.addUserRequestListener(new UserRequestListener() {
			public void joinAuction(Item item) {
				itemProbe.setReceivedValue(item);
			}
		});
		
		driver.startBiddingFor("an item-id", 789);
		driver.check(itemProbe);
	}
	
	@After
	public void disposeOfDriver(){
		if (driver != null) {
			driver.dispose();
		}			
	}
}
