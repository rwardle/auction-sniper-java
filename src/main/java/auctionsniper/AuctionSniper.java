package auctionsniper;

import org.jmock.example.announcer.Announcer;

public class AuctionSniper implements AuctionEventListener {

    private final Announcer<SniperListener> listeners = Announcer.to(SniperListener.class);
    private final Auction auction;
    private SniperSnapshot snapshot;
    private boolean isWinning = false;

    public AuctionSniper(String itemId, Auction auction) {
        this.auction = auction;
        snapshot = SniperSnapshot.joining(itemId);
    }

    @Override
    public void auctionClosed() {
        snapshot = snapshot.closed();
        notifyChange();
    }

    @Override
    public void currentPrice(int price, int increment, PriceSource priceSource) {
        isWinning = priceSource == PriceSource.FromSniper;
        if (isWinning) {
            snapshot = snapshot.winning(price);
        } else {
            int bid = price + increment;
            auction.bid(bid);
            snapshot = snapshot.bidding(price, bid);
        }
        notifyChange();
    }

    public SniperSnapshot getSnapshot() {
        return snapshot;
    }

    public void addSniperListener(SniperListener sniperListener) {
        listeners.addListener(sniperListener);
    }

    private void notifyChange() {
        listeners.announce().sniperStateChanged(snapshot);
    }
}
