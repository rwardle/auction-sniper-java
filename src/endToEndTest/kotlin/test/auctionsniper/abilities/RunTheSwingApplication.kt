package test.auctionsniper.abilities

import auctionsniper.Main
import auctionsniper.SniperState
import auctionsniper.ui.SnipersTableModel.textFor
import org.hamcrest.CoreMatchers
import test.auctionsniper.support.AuctionLogDriver
import test.auctionsniper.support.AuctionSniperDriver
import test.auctionsniper.support.Constants.SNIPER_ID
import test.auctionsniper.support.Constants.SNIPER_PASSWORD

open class RunTheSwingApplication(private val hostname: String) : IRunTheApplication {

    private val logDriver = AuctionLogDriver()
    private lateinit var driver: AuctionSniperDriver

    companion object {
        fun withServer(hostname: String): RunTheSwingApplication = RunTheSwingApplication(hostname)
    }

    override fun stop() {
        driver.dispose()
    }

    override fun startBiddingOnItems(vararg itemIds: String) {
        startSniper()
        for (itemId in itemIds) {
            driver.startBiddingFor(itemId, Integer.MAX_VALUE)
            driver.showsSniperStatus(itemId, 0, 0, textFor(SniperState.JOINING))
        }
    }

    override fun startBiddingWithStopPrice(itemId: String, stopPrice: Int) {
        startSniper()
        driver.startBiddingFor(itemId, stopPrice)
        driver.showsSniperStatus(itemId, 0, 0, textFor(SniperState.JOINING))
    }

    override fun showsSniperHasLostItem(itemId: String, lastPrice: Int, lastBid: Int) {
        driver.showsSniperStatus(itemId, lastPrice, lastBid, textFor(SniperState.LOST))
    }

    override fun hasShownSniperIsBidding(itemId: String, lastPrice: Int, lastBid: Int) {
        driver.showsSniperStatus(itemId, lastPrice, lastBid, textFor(SniperState.BIDDING))
    }

    override fun hasShownSniperIsWinning(itemId: String, winningBid: Int) {
        driver.showsSniperStatus(itemId, winningBid, winningBid, textFor(SniperState.WINNING))
    }

    override fun showsSniperHasWonAuction(itemId: String, lastPrice: Int) {
        driver.showsSniperStatus(itemId, lastPrice, lastPrice, textFor(SniperState.WON))
    }

    override fun hasShownSniperIsLosing(itemId: String, lastPrice: Int, lastBid: Int) {
        driver.showsSniperStatus(itemId, lastPrice, lastBid, textFor(SniperState.LOSING))
    }

    override fun showsSniperHasFailed(itemId: String) {
        driver.showsSniperStatus(itemId, 0, 0, textFor(SniperState.FAILED))
    }

    override fun reportsInvalidMessage(message: String) {
        logDriver.hasEntry(CoreMatchers.containsString(message))
    }

    private fun startSniper() {
        logDriver.clearLog()

        val thread = object : Thread() {
            override fun run() {
                try {
                    val args = arrayOf(hostname, SNIPER_ID, SNIPER_PASSWORD)
                    Main.main(*args)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        thread.isDaemon = true
        thread.start()

        driver = AuctionSniperDriver(1000)
    }
}
