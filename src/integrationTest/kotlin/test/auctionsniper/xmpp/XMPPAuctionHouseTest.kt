package test.auctionsniper.xmpp

import auctionsniper.AuctionEventListener
import auctionsniper.Item
import auctionsniper.xmpp.XMPPAuctionHouse
import org.junit.After
import org.junit.Before
import org.junit.Test
import test.auctionsniper.abilities.RunAnAuction
import test.auctionsniper.support.Constants.SNIPER_ID
import test.auctionsniper.support.Constants.SNIPER_PASSWORD
import test.auctionsniper.support.Constants.SNIPER_XMPP_ID
import test.auctionsniper.support.Constants.XMPP_HOSTNAME_ENV
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue

class XMPPAuctionHouseTest {

    private lateinit var auctionServer: RunAnAuction
    private lateinit var auctionHouse: XMPPAuctionHouse

    @Before
    fun setup() {
        val hostname = System.getenv(XMPP_HOSTNAME_ENV) ?: "localhost"

        auctionServer = RunAnAuction.withServer(hostname)
        auctionServer.startSellingItem("item-54321")

        auctionHouse = XMPPAuctionHouse.connect(hostname, SNIPER_ID, SNIPER_PASSWORD)
    }

    @After
    fun cleanup() {
        auctionHouse.disconnect()
        auctionServer.stop()
    }

    @Test
    fun `receives events from auction server after joining`() {
        val auctionWasClosed = CountDownLatch(1)
        val auction = auctionHouse.auctionFor(Item.create(auctionServer.itemId(), 789))
        auction.addAuctionEventListener(auctionClosedListener(auctionWasClosed))
        auction.join()
        auctionServer.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)

        auctionServer.announceClosed()

        assertTrue(auctionWasClosed.await(2, TimeUnit.SECONDS))
    }

    private fun auctionClosedListener(auctionWasClosed: CountDownLatch): AuctionEventListener {
        return object : AuctionEventListener {
            override fun auctionClosed() {
                auctionWasClosed.countDown()
            }

            override fun auctionFailed() {
                // no-op
            }

            override fun currentPrice(price: Int, increment: Int, priceSource: AuctionEventListener.PriceSource?) {
                // no-op
            }
        }
    }
}
