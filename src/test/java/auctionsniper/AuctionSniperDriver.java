package auctionsniper;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.fixture.FrameFixture;

import static org.assertj.swing.finder.WindowFinder.findFrame;

public class AuctionSniperDriver {

    private final FrameFixture mainFrame;

    public AuctionSniperDriver(long timeout) {
        mainFrame = findFrame(AppConstants.MAIN_WINDOW_NAME).withTimeout(timeout).using(BasicRobot.robotWithCurrentAwtHierarchy());
    }

    public void showsSniperStatus(String statusText) {
        mainFrame.label(AppConstants.SNIPER_STATUS_NAME).requireText(statusText);
    }

    public void dispose() {
        mainFrame.cleanUp();
    }
}
