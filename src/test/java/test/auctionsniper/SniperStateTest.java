package test.auctionsniper;

import auctionsniper.Defect;
import org.junit.Test;

import static auctionsniper.SniperState.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class SniperStateTest {

    @Test
    public void reportsLostWhenAuctionClosesInJoining() {
        assertThat(JOINING.whenAuctionClosed(), equalTo(LOST));
    }

    @Test
    public void reportsLostWhenAuctionClosesInBidding() {
        assertThat(BIDDING.whenAuctionClosed(), equalTo(LOST));
    }

    @Test
    public void reportsWonWhenAuctionClosesInWinning() {
        assertThat(WINNING.whenAuctionClosed(), equalTo(WON));
    }

    @Test(expected = Defect.class)
    public void throwsDefectWhenAuctionClosesInLost() {
        LOST.whenAuctionClosed();
    }

    @Test(expected = Defect.class)
    public void throwsDefectWhenAuctionClosesInWon() {
        WON.whenAuctionClosed();
    }
}
