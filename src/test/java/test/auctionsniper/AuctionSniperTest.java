package test.auctionsniper;

import auctionsniper.Auction;
import auctionsniper.AuctionSniper;
import auctionsniper.SniperListener;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;

import static auctionsniper.AuctionEventListener.PriceSource.FromOtherBidder;
import static auctionsniper.AuctionEventListener.PriceSource.FromSniper;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static test.auctionsniper.AuctionSniperTest.ListenerState.*;

public class AuctionSniperTest {

    private final Auction auction = mock(Auction.class);
    private final SniperListenerSpy sniperListener = spy(new SniperListenerSpy());
    private final AuctionSniper sniper = new AuctionSniper(auction, sniperListener);

    @Test
    public void reportsLostWhenAuctionClosesImmediately() {
        sniper.auctionClosed();

        verify(sniperListener, atLeastOnce()).sniperLost();
    }

    @Test
    public void reportsLostIfAuctionClosesWhenBidding() {
        doAnswer(invocation -> {
            verifySniperInState(Bidding, invocation);
            return invocation.callRealMethod();
        }).when(sniperListener).sniperLost();

        sniper.currentPrice(123, 45, FromOtherBidder);
        sniper.auctionClosed();

        verify(sniperListener, atLeastOnce()).sniperLost();
    }

    @Test
    public void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        int price = 1001;
        int increment = 25;

        sniper.currentPrice(price, increment, FromOtherBidder);

        verify(auction).bid(price + increment);
        verify(sniperListener, atLeastOnce()).sniperBidding();
    }

    @Test
    public void reportsIsWinningWhenCurrentPriceComesFromSniper() {
        sniper.currentPrice(123, 45, FromSniper);

        verifyZeroInteractions(auction);
        verify(sniperListener, atLeastOnce()).sniperWinning();
    }

    @Test
    public void reportsWonIfAuctionClosesWhenWinning() {
        doAnswer(invocation -> {
            verifySniperInState(Winning, invocation);
            return invocation.callRealMethod();
        }).when(sniperListener).sniperWon();

        sniper.currentPrice(123, 45, FromSniper);
        sniper.auctionClosed();

        verify(sniperListener, atLeastOnce()).sniperWon();
    }

    private static void verifySniperInState(ListenerState state, InvocationOnMock invocation) {
        SniperListenerSpy listener = (SniperListenerSpy) invocation.getMock();
        assertThat(listener.state, is(state));
    }

    enum ListenerState {
        Undefined, Lost, Bidding, Won, Winning
    }

    private static class SniperListenerSpy implements SniperListener {

        ListenerState state = Undefined;

        @Override
        public void sniperLost() {
            state = Lost;
        }

        @Override
        public void sniperBidding() {
            state = Bidding;
        }

        @Override
        public void sniperWinning() {
            state = Winning;
        }

        @Override
        public void sniperWon() {
            state = Won;
        }
    }
}
