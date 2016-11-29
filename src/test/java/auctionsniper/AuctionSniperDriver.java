package auctionsniper;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.timing.Condition;
import org.assertj.swing.timing.Pause;

import static org.assertj.swing.finder.WindowFinder.findFrame;

public class AuctionSniperDriver {

    private final long timeout;
    private final FrameFixture mainFrame;

    public AuctionSniperDriver(long timeout) {
        this.timeout = timeout;
        mainFrame = findFrame(AppConstants.MAIN_WINDOW_NAME)
                .withTimeout(timeout)
                .using(BasicRobot.robotWithCurrentAwtHierarchy());
    }

    public void showsSniperStatus(final String statusText) {
        String description = String.format("Sniper Status label text to be '%s'", statusText);
        Pause.pause(new Condition(description) {
            @Override
            public boolean test() {
                return statusText.equals(mainFrame.label(AppConstants.SNIPER_STATUS_NAME).text());
            }
        }, timeout);
    }

    public void dispose() {
        mainFrame.cleanUp();
    }
}
