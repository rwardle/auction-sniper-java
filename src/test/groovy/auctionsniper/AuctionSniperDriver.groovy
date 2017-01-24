package auctionsniper

import org.assertj.swing.core.BasicRobot
import org.assertj.swing.data.TableCell
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
                tableCellWithText(row(0).column(0), itemId),
                tableCellWithText(row(0).column(1), lastPrice as String),
                tableCellWithText(row(0).column(2), lastBid as String),
                tableCellWithText(row(0).column(3), statusText)
        ] as Condition[]
        Pause.pause(conditions, timeout)
    }

    private Condition tableCellWithText(TableCell tableCell, String text) {
        def description = String.format(
                "Snipers table cell [%d, %d] text to be '%s'",
                tableCell.row, tableCell.column, text)
        return new Condition(description) {
            String failedComparisonValue

            @Override
            boolean test() {
                def value = mainFrame.table(SNIPERS_TABLE_NAME).valueAt(tableCell)
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
