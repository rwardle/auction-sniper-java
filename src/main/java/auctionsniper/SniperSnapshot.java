package auctionsniper;

import java.util.Objects;

public class SniperSnapshot {

    public final String itemId;
    public final int lastPrice;
    public final int lastBid;
    public final SniperState state;

    public SniperSnapshot(String itemId, int lastPrice, int lastBid, SniperState state) {
        this.itemId = itemId;
        this.lastPrice = lastPrice;
        this.lastBid = lastBid;
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SniperSnapshot that = (SniperSnapshot) o;
        return lastPrice == that.lastPrice &&
                lastBid == that.lastBid &&
                Objects.equals(itemId, that.itemId) &&
                state == that.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, lastPrice, lastBid, state);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SniperSnapshot{");
        sb.append("itemId='").append(itemId).append('\'');
        sb.append(", lastPrice=").append(lastPrice);
        sb.append(", lastBid=").append(lastBid);
        sb.append(", state=").append(state);
        sb.append('}');
        return sb.toString();
    }

    public SniperSnapshot bidding(int newLastPrice, int newLastBid) {
        return new SniperSnapshot(itemId, newLastPrice, newLastBid, SniperState.BIDDING);
    }

    public SniperSnapshot winning(int newLastPrice) {
        return new SniperSnapshot(itemId, newLastPrice, lastBid, SniperState.WINNING);
    }

    public SniperSnapshot closed() {
        return new SniperSnapshot(itemId, lastPrice, lastBid, state.whenAuctionClosed());
    }

    public static SniperSnapshot joining(String itemId) {
        return new SniperSnapshot(itemId, 0, 0, SniperState.JOINING);
    }
}
