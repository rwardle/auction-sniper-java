package auctionsniper;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class SniperSnapshot {

    public static SniperSnapshot create(String itemId, int lastPrice, int lastBid, SniperState state) {
        return new AutoValue_SniperSnapshot(itemId, lastPrice, lastBid, state);
    }

    public static SniperSnapshot joining(String itemId) {
        return SniperSnapshot.create(itemId, 0, 0, SniperState.JOINING);
    }

    public abstract String itemId();

    public abstract int lastPrice();

    public abstract int lastBid();

    public abstract SniperState state();

    public final SniperSnapshot bidding(int newLastPrice, int newLastBid) {
        return SniperSnapshot.create(itemId(), newLastPrice, newLastBid, SniperState.BIDDING);
    }

    public final SniperSnapshot winning(int newLastPrice) {
        return SniperSnapshot.create(itemId(), newLastPrice, lastBid(), SniperState.WINNING);
    }

    public final SniperSnapshot losing(int newLastPrice) {
        return SniperSnapshot.create(itemId(), newLastPrice, lastBid(), SniperState.LOSING);
    }

    public final SniperSnapshot closed() {
        return SniperSnapshot.create(itemId(), lastPrice(), lastBid(), state().whenAuctionClosed());
    }

    public final SniperSnapshot failed() {
        return SniperSnapshot.create(itemId(), 0, 0, SniperState.FAILED);
    }

    public final boolean isForSameItemAs(SniperSnapshot snapshot) {
        return itemId().equals(snapshot.itemId());
    }
}
