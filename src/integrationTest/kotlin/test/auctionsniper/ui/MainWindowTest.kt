package test.auctionsniper.ui

import auctionsniper.Item
import auctionsniper.SniperPortfolio
import auctionsniper.ui.MainWindow
import org.assertj.swing.timing.Condition
import org.assertj.swing.timing.Pause
import org.junit.After
import org.junit.Test
import test.endtoend.auctionsniper.support.AuctionSniperDriver

class MainWindowTest {

    private val timeout = 1000L
    private val mainWindow = MainWindow(SniperPortfolio())
    private val driver = AuctionSniperDriver(timeout)

    @After
    fun cleanup() {
        driver.dispose()
    }

    @Test
    fun `makes user request when join button clicked`() {
        var receivedValue: Item? = null
        mainWindow.addUserRequestListener({ item: Item -> receivedValue = item })

        driver.startBiddingFor("an item-id", 789)

        Pause.pause(object : Condition("join request for 'an item-id'") {

            override fun test(): Boolean {
                return receivedValue == Item.create("an item-id", 789)
            }

            override fun descriptionAddendum(): String {
                return String.format(", receivedValue: '%s'", receivedValue)
            }
        }, timeout)
    }
}
