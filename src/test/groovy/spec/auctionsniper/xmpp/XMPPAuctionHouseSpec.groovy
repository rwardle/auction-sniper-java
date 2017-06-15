package spec.auctionsniper.xmpp

import auctionsniper.Auction
import auctionsniper.AuctionEventListener
import auctionsniper.FakeAuctionServer
import auctionsniper.xmpp.XMPPAuctionHouse
import spock.lang.Specification

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

import static auctionsniper.ApplicationRunner.SNIPER_ID
import static auctionsniper.ApplicationRunner.SNIPER_PASSWORD
import static spec.auctionsniper.AuctionSniperSpec.SNIPER_XMPP_ID
import static spec.auctionsniper.AuctionSniperSpec.XMPP_HOSTNAME_ENV

class XMPPAuctionHouseSpec extends Specification {

    FakeAuctionServer auctionServer
    XMPPAuctionHouse auctionHouse

    def setup() {
        def hostname = System.getenv(XMPP_HOSTNAME_ENV)

        auctionServer = new FakeAuctionServer(hostname, "item-54321")
        auctionServer.startSellingItem()

        auctionHouse = XMPPAuctionHouse.connect(hostname, SNIPER_ID, SNIPER_PASSWORD)
    }

    def cleanup() {
        auctionHouse.disconnect()
        auctionServer.stop()
    }

    def "receives events from auction server after joining"() {
        setup:
        def auctionWasClosed = new CountDownLatch(1)
        Auction auction = auctionHouse.auctionFor(auctionServer.getItemId())
        auction.addAuctionEventListener(auctionClosedListener(auctionWasClosed))
        auction.join()
        auctionServer.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)

        when:
        auctionServer.announceClosed()

        then:
        auctionWasClosed.await(2, TimeUnit.SECONDS)
    }

    private static AuctionEventListener auctionClosedListener(CountDownLatch auctionWasClosed) {
        return new AuctionEventListener() {
            @Override
            void auctionClosed() {
                auctionWasClosed.countDown()
            }

            @Override
            void currentPrice(int price, int increment, AuctionEventListener.PriceSource priceSource) {
                // no-op
            }
        }
    }
}
