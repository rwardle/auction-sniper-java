package auctionsniper;

import org.junit.After;
import org.junit.Test;
import org.jxmpp.stringprep.XmppStringprepException;

public class AuctionSniperEndToEndTest {

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
        auction.hasReceivedJoinRequestFromSniper();
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
