package test.auctionsniper.abilities

import auctionsniper.*
import auctionsniper.xmpp.XMPPAuctionHouse
import org.awaitility.Awaitility.await
import org.hamcrest.CoreMatchers
import org.jmock.example.announcer.Announcer
import test.auctionsniper.support.AuctionLogDriver
import test.auctionsniper.support.Constants
import java.util.concurrent.TimeUnit.SECONDS
import kotlin.test.assertEquals

open class RunTheDomainApplication(private val hostname: String) : IRunTheApplication {

    private val logDriver = AuctionLogDriver()
    private val userRequests = Announcer.to(UserRequestListener::class.java)
    private val sniperSnapshots = HashMap<String, SniperSnapshot>()
    private lateinit var auctionHouse: XMPPAuctionHouse

    companion object {
        fun withServer(hostname: String): RunTheDomainApplication = RunTheDomainApplication(hostname)
    }

    override fun startBiddingOnItems(vararg itemIds: String) {
        startSniper()
        for (itemId in itemIds) {
            userRequests.announce().joinAuction(Item.create(itemId, Integer.MAX_VALUE))
            assertSniperStatus(itemId, 0, 0, SniperState.JOINING)
        }
    }

    private fun assertSniperStatus(itemId: String, lastPrice: Int, lastBid: Int, state: SniperState) {
        await().atMost(1, SECONDS).untilAsserted { assertEquals(state, sniperSnapshots[itemId]?.state()) }
        assertEquals(lastPrice, sniperSnapshots[itemId]?.lastPrice())
        assertEquals(lastBid, sniperSnapshots[itemId]?.lastBid())
    }

    override fun startBiddingWithStopPrice(itemId: String, stopPrice: Int) {
        startSniper()
        userRequests.announce().joinAuction(Item.create(itemId, stopPrice))
        assertSniperStatus(itemId, 0, 0, SniperState.JOINING)
    }

    override fun showsSniperHasLostItem(itemId: String, lastPrice: Int, lastBid: Int) {
        assertSniperStatus(itemId, lastPrice, lastBid, SniperState.LOST)
    }

    override fun hasShownSniperIsBidding(itemId: String, lastPrice: Int, lastBid: Int) {
        assertSniperStatus(itemId, lastPrice, lastBid, SniperState.BIDDING)
    }

    override fun hasShownSniperIsWinning(itemId: String, winningBid: Int) {
        assertSniperStatus(itemId, winningBid, winningBid, SniperState.WINNING)
    }

    override fun showsSniperHasWonAuction(itemId: String, lastPrice: Int) {
        assertSniperStatus(itemId, lastPrice, lastPrice, SniperState.WON)
    }

    override fun hasShownSniperIsLosing(itemId: String, lastPrice: Int, lastBid: Int) {
        assertSniperStatus(itemId, lastPrice, lastBid, SniperState.LOSING)
    }

    override fun showsSniperHasFailed(itemId: String) {
        assertSniperStatus(itemId, 0, 0, SniperState.FAILED)
    }

    override fun reportsInvalidMessage(message: String) {
        logDriver.hasEntry(CoreMatchers.containsString(message))
    }

    override fun stop() {
        auctionHouse.disconnect()
    }

    private fun startSniper() {
        logDriver.clearLog()

        auctionHouse = XMPPAuctionHouse.connect(hostname, Constants.SNIPER_ID, Constants.SNIPER_PASSWORD)

        val sniperPortfolio = SniperPortfolio()
        sniperPortfolio.addPortfolioListener { auctionSniper ->
            sniperSnapshots[auctionSniper.snapshot.itemId()] = auctionSniper.snapshot
            auctionSniper.addSniperListener { snapshot ->
                sniperSnapshots[snapshot.itemId()] = snapshot
            }
        }

        val sniperLauncher = SniperLauncher(auctionHouse, sniperPortfolio)
        userRequests.addListener(sniperLauncher)
    }
}
