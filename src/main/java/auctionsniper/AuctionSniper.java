package auctionsniper;

import org.jmock.example.announcer.Announcer;

public class AuctionSniper implements AuctionEventListener {

    private final Announcer<SniperListener> listeners = Announcer.to(SniperListener.class);
    private final Item item;
    private final Auction auction;
    private SniperSnapshot snapshot;

    public AuctionSniper(Item item, Auction auction) {
        this.item = item;
        this.auction = auction;
        snapshot = SniperSnapshot.joining(item.identifier());
    }

    @Override
    public void auctionClosed() {
        snapshot = snapshot.closed();
        notifyChange();
    }

    @Override
    public void auctionFailed() {
        snapshot = snapshot.failed();
        notifyChange();
    }

    @Override
    public void currentPrice(int price, int increment, PriceSource priceSource) {
        switch (priceSource) {
            case FromSniper:
                snapshot = snapshot.winning(price);
                break;
            case FromOtherBidder:
                int bid = price + increment;
                if (item.allowsBid(bid)) {
                    auction.bid(bid);
                    snapshot = snapshot.bidding(price, bid);
                } else {
                    snapshot = snapshot.losing(price);
                }
                break;
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
