package auctionsniper

import org.assertj.swing.core.BasicRobot
import org.assertj.swing.fixture.FrameFixture
import org.assertj.swing.timing.Condition
import org.assertj.swing.timing.Pause

import static auctionsniper.AppConstants.MAIN_WINDOW_NAME
import static auctionsniper.AppConstants.SNIPER_STATUS_NAME
import static org.assertj.swing.finder.WindowFinder.findFrame

class AuctionSniperDriver {

    long timeout
    FrameFixture mainFrame

    AuctionSniperDriver(long timeout) {
        this.timeout = timeout
        mainFrame = findFrame(MAIN_WINDOW_NAME)
                .withTimeout(timeout)
                .using(BasicRobot.robotWithCurrentAwtHierarchy())
    }

    void showsSniperStatus(String statusText) {
        def description = String.format("Sniper Status label text to be '%s'", statusText)
        Pause.pause(new Condition(description) {
            @Override
            boolean test() {
                return statusText == mainFrame.label(SNIPER_STATUS_NAME).text()
            }
        }, timeout)
    }

    void dispose() {
        mainFrame.cleanUp()
    }
}
