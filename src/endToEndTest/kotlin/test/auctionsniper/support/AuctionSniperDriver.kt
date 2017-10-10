package test.auctionsniper.support

import auctionsniper.ui.MainWindow.*
import org.assertj.swing.core.BasicRobot
import org.assertj.swing.data.TableCell.row
import org.assertj.swing.finder.WindowFinder.findFrame
import org.assertj.swing.fixture.FrameFixture
import org.assertj.swing.fixture.JButtonFixture
import org.assertj.swing.fixture.JTableFixture
import org.assertj.swing.fixture.JTextComponentFixture
import org.assertj.swing.timing.Condition
import org.assertj.swing.timing.Pause

class AuctionSniperDriver(private val timeout: Long) {

    private val mainFrame: FrameFixture = findFrame(MAIN_WINDOW_NAME)
        .withTimeout(timeout)
        .using(BasicRobot.robotWithCurrentAwtHierarchy())

    fun startBiddingFor(itemId: String, stopPrice: Int) {
        itemIdField().setText(itemId)
        stopPriceField().setText(java.lang.String.valueOf(stopPrice))
        bidButton().click()
    }

    private fun itemIdField(): JTextComponentFixture {
        val textBox = mainFrame.textBox(NEW_ITEM_ID_FIELD_NAME)
        textBox.focus()
        return textBox
    }

    private fun stopPriceField(): JTextComponentFixture {
        val textBox = mainFrame.textBox(STOP_PRICE_FIELD_NAME)
        textBox.focus()
        return textBox
    }

    private fun bidButton(): JButtonFixture {
        return mainFrame.button(JOIN_BUTTON_NAME)
    }

    fun showsSniperStatus(itemId: String, lastPrice: Int, lastBid: Int, statusText: String) {
        val table = mainFrame.table(SNIPERS_TABLE_NAME)
        val row = table.cell(itemId).row()

        val conditions = arrayOf(
            tableCellWithText(table, row, "Item", itemId),
            tableCellWithText(table, row, "Last Price", lastPrice.toString()),
            tableCellWithText(table, row, "Last Bid", lastBid.toString()),
            tableCellWithText(table, row, "State", statusText)
        )
        Pause.pause(conditions, timeout)
    }

    private fun tableCellWithText(table: JTableFixture, rowIndex: Int, columnName: String, text: String): Condition {
        val description = String.format(
            "Snipers table column '%s' text in row '%d' to be '%s'", columnName, rowIndex, text)

        return object : Condition(description) {
            var failedComparisonValue: String? = null

            override fun test(): Boolean {
                val value = table.valueAt(row(rowIndex).column(table.columnIndexFor(columnName)))
                return if (text == value) {
                    true
                } else {
                    failedComparisonValue = value
                    false
                }
            }

            override fun descriptionAddendum(): String {
                return if (failedComparisonValue == null) {
                    super.descriptionAddendum()
                } else {
                    String.format(", actual value was '%s'", failedComparisonValue)
                }
            }
        }
    }

    fun dispose() {
        mainFrame.cleanUp()
    }
}
