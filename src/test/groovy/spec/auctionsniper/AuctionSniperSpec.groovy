package spec.auctionsniper

import auctionsniper.ApplicationRunner
import auctionsniper.FakeAuctionServer
import spock.lang.Specification

class AuctionSniperSpec extends Specification {

    static final SNIPER_XMPP_ID = "sniper@localhost/Auction"

    FakeAuctionServer auction
    ApplicationRunner application

    def setup() {
        def hostname = System.getenv("XMPP_HOSTNAME")
        auction = new FakeAuctionServer(hostname, "item-54321")
        application = new ApplicationRunner(hostname)
    }

    def cleanup() {
        auction.stop()
        application.stop()
    }

    def "sniper joins auction until auction closes"() {
        given:
        auction.startSellingItem()

        when:
        application.startBiddingIn(auction)

        then:
        auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)

        when:
        auction.announceClosed()

        then:
        application.showsSniperHasLostAuction()
    }

    def "sniper makes a higher bid but loses"() {
        given:
        auction.startSellingItem()

        when:
        application.startBiddingIn(auction)

        then:
        auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)

        when:
        auction.reportPrice(1000, 98, "other bidder")

        then:
        application.hasShownSniperIsBidding()

        and:
        auction.hasReceivedBid(1098, SNIPER_XMPP_ID)

        when:
        auction.announceClosed()

        then:
        application.showsSniperHasLostAuction()
    }
}
