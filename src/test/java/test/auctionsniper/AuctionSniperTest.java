package test.auctionsniper;

import auctionsniper.Auction;
import auctionsniper.AuctionSniper;
import auctionsniper.SniperListener;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class AuctionSniperTest {

    private final Auction auction = mock(Auction.class);
    private final SniperListener sniperListener = mock(SniperListener.class);
    private final AuctionSniper sniper = new AuctionSniper(auction, sniperListener);

    @Test
    public void reportsLostWhenAuctionCloses() {
        sniper.auctionClosed();

        verify(sniperListener, atLeastOnce()).sniperLost();
    }

    @Test
    public void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        int price = 1001;
        int increment = 25;

        sniper.currentPrice(price, increment);

        verify(auction).bid(price + increment);
        verify(sniperListener, atLeastOnce()).sniperBidding();
    }
}
