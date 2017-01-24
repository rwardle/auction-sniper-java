package test.auctionsniper.ui;

import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import auctionsniper.ui.Column;
import org.junit.Test;

import static auctionsniper.ui.Column.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ColumnTest {

    private final SniperSnapshot snapshot = new SniperSnapshot("itemId", 100, 112, SniperState.LOST);

    @Test
    public void itemIdentifierAtIndex0() {
        assertThat(ITEM_IDENTIFIER, equalTo(Column.at(0)));
    }

    @Test
    public void lastPriceAtIndex1() {
        assertThat(LAST_PRICE, equalTo(Column.at(1)));
    }

    @Test
    public void lastBidAtIndex2() {
        assertThat(LAST_BID, equalTo(Column.at(2)));
    }

    @Test
    public void sniperStateAtIndex3() {
        assertThat(SNIPER_STATE, equalTo(Column.at(3)));
    }

    @Test
    public void itemIdentifierValueInSnapshot() {
        assertThat("itemId", equalTo(ITEM_IDENTIFIER.valueIn(snapshot)));
    }

    @Test
    public void lastPriceValueInSnapshot() {
        assertThat(100, equalTo(LAST_PRICE.valueIn(snapshot)));
    }

    @Test
    public void lastBidValueInSnapshot() {
        assertThat(112, equalTo(LAST_BID.valueIn(snapshot)));
    }

    @Test
    public void stateValueInSnapshot() {
        assertThat("Lost", equalTo(SNIPER_STATE.valueIn(snapshot)));
    }
}
