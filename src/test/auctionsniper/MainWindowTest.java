package test.auctionsniper;

import org.junit.After;
import org.junit.Test;

import com.objogate.wl.swing.probe.ValueMatcherProbe;

import auctionsniper.ui.AuctionSniperDriver;
import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SniperPortfolio;
import auctionsniper.ui.SnipersTableModel;
import auctionsniper.ui.UserRequestListener;
import static org.hamcrest.Matchers.*;

public class MainWindowTest {
	private final SniperPortfolio portfolio = new SniperPortfolio();
	private final MainWindow mainWindow = new MainWindow(portfolio);
	private final AuctionSniperDriver driver = new AuctionSniperDriver(100);
	
	@Test
	public void makeUserRequestsWhenJoinButtonClicked() {
		final ValueMatcherProbe<String> buttonProbe = new ValueMatcherProbe<String>(equalTo("an item-id"), "join request");
		mainWindow.addUserRequestListener(new UserRequestListener() {
			public void joinAuction(String itemId) {
				buttonProbe.setReceivedValue(itemId);
			}
		});
		
		driver.startBiddingFor("an item-id");
		driver.check(buttonProbe);
	}
	
	@After
	public void disposeOfDriver(){
		if (driver != null) {
			driver.dispose();
		}			
	}
}
