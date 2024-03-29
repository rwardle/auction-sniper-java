package test.auctionsniper;

import auctionsniper.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;

import static auctionsniper.AuctionEventListener.PriceSource.FromOtherBidder;
import static auctionsniper.AuctionEventListener.PriceSource.FromSniper;
import static auctionsniper.SniperState.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

public class AuctionSniperTest {

    private static final String ITEM_ID = "itemId";

    private final Auction auction = mock(Auction.class);
    private final SniperListenerSpy sniperListener = spy(new SniperListenerSpy());
    private final AuctionSniper sniper = new AuctionSniper(Item.create(ITEM_ID, 1234), auction);

    @Before
    public void setup() {
        sniper.addSniperListener(sniperListener);
    }

    @Test
    public void reportsLostWhenAuctionClosesImmediately() {
        sniper.auctionClosed();

        verify(sniperListener, atLeastOnce()).sniperStateChanged(argThat(snapshot -> snapshot.state() == LOST));
    }

    @Test
    public void reportsLostIfAuctionClosesWhenBidding() {
        doAnswer(invocation -> {
            verifySniperIs(BIDDING, invocation);
            return invocation.callRealMethod();
        }).when(sniperListener).sniperStateChanged(argThat(snapshot -> snapshot.state() == LOST));

        sniper.currentPrice(123, 45, FromOtherBidder);
        sniper.auctionClosed();

        verify(sniperListener, atLeastOnce()).sniperStateChanged(argThat(snapshot -> snapshot.state() == LOST));
    }

    @Test
    public void reportsFailedIfAuctionFailsWhenBidding() {
        doAnswer(invocation -> {
            verifySniperIs(BIDDING, invocation);
            return invocation.callRealMethod();
        }).when(sniperListener).sniperStateChanged(argThat(snapshot -> snapshot.state() == FAILED));

        sniper.currentPrice(123, 45, FromOtherBidder);
        sniper.auctionFailed();

        verify(sniperListener, atLeastOnce()).sniperStateChanged(
                SniperSnapshot.create(ITEM_ID, 0, 0, FAILED));
    }

    @Test
    public void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        int price = 1001;
        int increment = 25;
        int bid = price + increment;

        sniper.currentPrice(price, increment, FromOtherBidder);

        verify(auction).bid(bid);
        verify(sniperListener, atLeastOnce()).sniperStateChanged(
                SniperSnapshot.create(ITEM_ID, price, bid, BIDDING));
    }

    @Test
    public void reportsIsWinningWhenCurrentPriceComesFromSniper() {
        doAnswer(invocation -> {
            verifySniperIs(BIDDING, invocation);
            return invocation.callRealMethod();
        }).when(sniperListener).sniperStateChanged(argThat(snapshot -> snapshot.state() == WINNING));

        sniper.currentPrice(123, 12, FromOtherBidder);
        sniper.currentPrice(135, 45, FromSniper);

        verify(sniperListener, atLeastOnce())
                .sniperStateChanged(SniperSnapshot.create(ITEM_ID, 135, 135, WINNING));
    }

    @Test
    public void reportsWonIfAuctionClosesWhenWinning() {
        doAnswer(invocation -> {
            verifySniperIs(WINNING, invocation);
            return invocation.callRealMethod();
        }).when(sniperListener).sniperStateChanged(argThat(snapshot -> snapshot.state() == WON));

        sniper.currentPrice(123, 45, FromSniper);
        sniper.auctionClosed();

        verify(sniperListener, atLeastOnce()).sniperStateChanged(argThat(snapshot -> snapshot.state() == WON));
    }

    @Test
    public void doesNotBidAndReportsLosingIfSubsequentPriceIsAboveStopPrice() {
        sniper.currentPrice(123, 45, FromOtherBidder);
        sniper.currentPrice(2345, 25, FromOtherBidder);

        int bid = 123 + 45;
        verify(auction).bid(bid);
        verify(sniperListener, atLeastOnce()).sniperStateChanged(SniperSnapshot.create(ITEM_ID, 2345, bid, LOSING));
    }

    @Test
    public void doesNotBidAndReportsLosingIfFirstPriceIsAboveStopPrice() {
        sniper.currentPrice(2345, 25, FromOtherBidder);

        verify(auction, never()).bid(anyInt());
        verify(sniperListener, atLeastOnce()).sniperStateChanged(SniperSnapshot.create(ITEM_ID, 2345, 0, LOSING));
    }

    @Test
    public void reportsLostIfAuctionClosesWhenLosing() {
        doAnswer(invocation -> {
            verifySniperIs(LOSING, invocation);
            return invocation.callRealMethod();
        }).when(sniperListener).sniperStateChanged(argThat(snapshot -> snapshot.state() == LOST));

        sniper.currentPrice(2345, 25, FromOtherBidder);
        sniper.auctionClosed();

        verify(sniperListener, atLeastOnce()).sniperStateChanged(argThat(snapshot -> snapshot.state() == LOST));
    }

    @Test
    public void continuesToBeLosingOnceStopPriceHasBeenReached() {
        sniper.currentPrice(2345, 25, FromOtherBidder);
        sniper.currentPrice(3456, 25, FromOtherBidder);

        verify(sniperListener, atLeastOnce()).sniperStateChanged(SniperSnapshot.create(ITEM_ID, 3456, 0, LOSING));
    }

    @Test
    public void doesNotBidAndReportsLosingIfPriceAfterWinningIsAboveStopPrice() {
        sniper.currentPrice(123, 12, FromOtherBidder);
        sniper.currentPrice(135, 45, FromSniper);
        sniper.currentPrice(2345, 25, FromOtherBidder);

        verify(auction).bid(135);
        verify(sniperListener, atLeastOnce())
                .sniperStateChanged(SniperSnapshot.create(ITEM_ID, 135, 135, WINNING));
        verify(sniperListener, atLeastOnce())
                .sniperStateChanged(SniperSnapshot.create(ITEM_ID, 2345, 135, LOSING));
    }

    private static void verifySniperIs(SniperState state, InvocationOnMock invocation) {
        SniperListenerSpy listener = (SniperListenerSpy) invocation.getMock();
        assertThat(listener.state, is(state));
    }

    private static class SniperListenerSpy implements SniperListener {

        SniperState state;

        @Override
        public void sniperStateChanged(SniperSnapshot snapshot) {
            state = snapshot.state();
        }
    }
}
