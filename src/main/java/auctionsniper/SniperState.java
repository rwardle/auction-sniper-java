package auctionsniper;

import java.util.Objects;

public class SniperState {

    public final String itemId;
    public final int lastPrice;
    public final int lastBid;

    public SniperState(String itemId, int lastPrice, int lastBid) {
        this.itemId = itemId;
        this.lastPrice = lastPrice;
        this.lastBid = lastBid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SniperState that = (SniperState) o;
        return lastPrice == that.lastPrice &&
                lastBid == that.lastBid &&
                Objects.equals(itemId, that.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, lastPrice, lastBid);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SniperState{");
        sb.append("itemId='").append(itemId).append('\'');
        sb.append(", lastPrice=").append(lastPrice);
        sb.append(", lastBid=").append(lastBid);
        sb.append('}');
        return sb.toString();
    }
}
