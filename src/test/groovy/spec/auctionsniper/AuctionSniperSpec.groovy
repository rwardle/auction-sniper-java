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
        sniperJoinsAuction()

        when:
        auction.announceClosed()

        then:
        application.showsSniperHasLostAuction(0, 0)
    }

    def "sniper makes a higher bid but loses"() {
        given:
        sniperJoinsAuction()
        sniperReceivesPriceAndBids(1000, 98, "other bidder")

        when:
        auction.announceClosed()

        then:
        application.showsSniperHasLostAuction(1000, 1098)
    }

    def "sniper wins an auction by bidding higher"() {
        given:
        sniperJoinsAuction()
        sniperReceivesPriceAndBids(1000, 98, "other bidder")

        when:
        auction.reportPrice(1098, 97, SNIPER_XMPP_ID)

        then:
        application.hasShownSniperIsWinning(1098)

        when:
        auction.announceClosed()

        then:
        application.showsSniperHasWonAuction(1098)
    }

    def sniperJoinsAuction() {
        auction.startSellingItem()
        application.startBiddingIn(auction)
        auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)
    }

    def sniperReceivesPriceAndBids(int price, int increment, String bidder) {
        auction.reportPrice(price, increment, bidder)

        def bid = price + increment
        application.hasShownSniperIsBidding(price, bid)
        auction.hasReceivedBid(bid, SNIPER_XMPP_ID)
    }
}
