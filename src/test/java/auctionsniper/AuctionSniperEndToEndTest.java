package auctionsniper;

import org.junit.After;
import org.junit.Test;
import org.jxmpp.stringprep.XmppStringprepException;

public class AuctionSniperEndToEndTest {

    private static final String SNIPER_XMPP_ID = "sniper@localhost/Auction";

    private final FakeAuctionServer auction;
    private final ApplicationRunner application;

    public AuctionSniperEndToEndTest() throws XmppStringprepException {
        String hostname = System.getenv("XMPP_HOSTNAME");
        auction = new FakeAuctionServer(hostname, "item-54321");
        application = new ApplicationRunner(hostname);
    }

    @Test
    public void sniperJoinsAuctionUntilAuctionCloses() throws Exception {
        auction.startSellingItem();

        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID);

        auction.announceClosed();
        application.showsSniperHasLostAuction();
    }

    @Test
    public void sniperMakesAHigherBidButLoses() throws Exception {
        auction.startSellingItem();

        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID);

        auction.reportPrice(1000, 98, "other bidder");
        application.hasShownSniperIsBidding();

        auction.hasReceivedBid(1098, SNIPER_XMPP_ID);

        auction.announceClosed();
        application.showsSniperHasLostAuction();
    }

    @After
    public void stopAuction() {
        auction.stop();
    }

    @After
    public void stopApplication() {
        application.stop();
    }
}
