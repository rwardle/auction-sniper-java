package auctionsniper

import org.assertj.swing.core.BasicRobot
import org.assertj.swing.fixture.FrameFixture
import org.assertj.swing.timing.Condition
import org.assertj.swing.timing.Pause

import static auctionsniper.AppConstants.MAIN_WINDOW_NAME
import static auctionsniper.AppConstants.SNIPERS_TABLE_NAME
import static org.assertj.swing.data.TableCell.row
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

    void showsSniperStatus(String itemId, int lastPrice, int lastBid, String statusText) {
        def conditions = [
                columnWithText("Item", itemId),
                columnWithText("Last Price", lastPrice as String),
                columnWithText("Last Bid", lastBid as String),
                columnWithText("State", statusText)
        ] as Condition[]
        Pause.pause(conditions, timeout)
    }

    private Condition columnWithText(String columnName, String text) {
        def description = String.format("Snipers table column '%s' text to be '%s'", columnName, text)
        return new Condition(description) {
            String failedComparisonValue

            @Override
            boolean test() {
                def table = mainFrame.table(SNIPERS_TABLE_NAME)
                def value = table.valueAt(row(0).column(table.columnIndexFor(columnName)))
                if (text == value) {
                    return true
                } else {
                    failedComparisonValue = value
                    return false
                }
            }

            @Override
            String descriptionAddendum() {
                return failedComparisonValue ? String.format(", actual value was '%s'", failedComparisonValue) : EMPTY_TEXT
            }
        }
    }

    void dispose() {
        mainFrame.cleanUp()
    }
}
