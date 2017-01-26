package spec.auctionsniper

import auctionsniper.ApplicationRunner
import auctionsniper.FakeAuctionServer
import spock.lang.Specification

class AuctionSniperSpec extends Specification {

    static final SNIPER_XMPP_ID = "sniper@localhost/Auction"

    FakeAuctionServer auction
    FakeAuctionServer auction2
    ApplicationRunner application

    def setup() {
        def hostname = System.getenv("XMPP_HOSTNAME")
        auction = new FakeAuctionServer(hostname, "item-54321")
        auction2 = new FakeAuctionServer(hostname, "item-65432")
        application = new ApplicationRunner(hostname)
    }

    def cleanup() {
        auction.stop()
        auction2.stop()
        application.stop()
    }

    def "sniper joins auction until auction closes"() {
        given:
        auction.startSellingItem()

        and:
        application.startBiddingIn(auction)
        auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)

        when:
        auction.announceClosed()

        then:
        application.showsSniperHasLostAuction(auction, 0, 0)
    }

    def "sniper makes a higher bid but loses"() {
        given:
        auction.startSellingItem()

        and:
        application.startBiddingIn(auction)
        auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)

        and:
        sniperReceivesPriceAndBids(auction, 1000, 98, "other bidder")

        when:
        auction.announceClosed()

        then:
        application.showsSniperHasLostAuction(auction, 1000, 1098)
    }

    def "sniper wins an auction by bidding higher"() {
        given:
        auction.startSellingItem()

        and:
        application.startBiddingIn(auction)
        auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)

        and:
        sniperReceivesPriceAndBids(auction, 1000, 98, "other bidder")

        when:
        auction.reportPrice(1098, 97, SNIPER_XMPP_ID)

        then:
        application.hasShownSniperIsWinning(auction, 1098)

        when:
        auction.announceClosed()

        then:
        application.showsSniperHasWonAuction(auction, 1098)
    }

    def "sniper bids for multiple items"() {
        given:
        auction.startSellingItem()
        auction2.startSellingItem()

        and:
        application.startBiddingIn(auction, auction2)
        auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)
        auction2.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID)

        and:
        sniperReceivesPriceAndBids(auction, 1000, 98, "other bidder")
        sniperReceivesPriceAndBids(auction2, 500, 21, "other bidder")

        when:
        auction.reportPrice(1098, 97, SNIPER_XMPP_ID)
        auction2.reportPrice(521, 20, SNIPER_XMPP_ID)

        then:
        application.hasShownSniperIsWinning(auction, 1098)
        application.hasShownSniperIsWinning(auction2, 521)

        when:
        auction.announceClosed()
        auction2.announceClosed()

        then:
        application.showsSniperHasWonAuction(auction, 1098)
        application.showsSniperHasWonAuction(auction2, 521)
    }

    def sniperReceivesPriceAndBids(FakeAuctionServer currentAuction, int price, int increment, String bidder) {
        currentAuction.reportPrice(price, increment, bidder)

        def bid = price + increment
        application.hasShownSniperIsBidding(currentAuction, price, bid)
        currentAuction.hasReceivedBid(bid, SNIPER_XMPP_ID)
    }
}
