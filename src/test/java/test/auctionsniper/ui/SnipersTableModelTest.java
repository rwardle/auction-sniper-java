package test.auctionsniper.ui;

import auctionsniper.Defect;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import auctionsniper.ui.Column;
import auctionsniper.ui.SnipersTableModel;
import org.hamcrest.Description;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Before;
import org.junit.Test;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static test.auctionsniper.CustomMatchers.with;

public class SnipersTableModelTest {

    private TableModelListener listener = mock(TableModelListener.class);
    private final SnipersTableModel model = new SnipersTableModel();

    @Before
    public void attachModelListener() {
        model.addTableModelListener(listener);
    }

    @Test
    public void hasEnoughColumns() {
        assertThat(model.getColumnCount(), equalTo(Column.values().length));
    }

    @Test
    public void setsUpColumnHeadings() {
        for (Column column : Column.values()) {
            assertEquals(column.name, model.getColumnName(column.ordinal()));
        }
    }

    @Test
    public void setsSniperValuesInColumns() {
        SniperSnapshot joining = SniperSnapshot.joining("item id");
        SniperSnapshot bidding = joining.bidding(555, 666);

        model.addSniper(joining);
        model.sniperStateChanged(bidding);

        assertRowMatchesSnapshot(0, bidding);
        verify(listener).tableChanged(with(aChangeInRow(0)));
    }

    @Test
    public void textForJoining() {
        assertThat("Joining", equalTo(SnipersTableModel.textFor(SniperState.JOINING)));
    }

    @Test
    public void textForBidding() {
        assertThat("Bidding", equalTo(SnipersTableModel.textFor(SniperState.BIDDING)));
    }

    @Test
    public void textForWinning() {
        assertThat("Winning", equalTo(SnipersTableModel.textFor(SniperState.WINNING)));
    }

    @Test
    public void textForLost() {
        assertThat("Lost", equalTo(SnipersTableModel.textFor(SniperState.LOST)));
    }

    @Test
    public void textForWon() {
        assertThat("Won", equalTo(SnipersTableModel.textFor(SniperState.WON)));
    }

    @Test
    public void notifiersListenersWhenAddingASniper() {
        SniperSnapshot joining = SniperSnapshot.joining("item123");

        assertEquals(0, model.getRowCount());
        model.addSniper(joining);

        assertEquals(1, model.getRowCount());
        assertRowMatchesSnapshot(0, joining);
        verify(listener).tableChanged(with(anInsertionAtRow(0)));
    }

    @Test
    public void holdsSnipersInAdditionOrder() {
        model.addSniper(SniperSnapshot.joining("item 0"));
        model.addSniper(SniperSnapshot.joining("item 1"));

        assertThat(cellValue(0, Column.ITEM_IDENTIFIER), equalTo("item 0"));
        assertThat(cellValue(1, Column.ITEM_IDENTIFIER), equalTo("item 1"));
    }

    @Test
    public void updatesCorrectRowForSniper() {
        SniperSnapshot joining = SniperSnapshot.joining("item 1");
        SniperSnapshot bidding = joining.bidding(555, 666);

        model.addSniper(SniperSnapshot.joining("item 0"));
        model.addSniper(joining);
        model.sniperStateChanged(bidding);

        assertRowMatchesSnapshot(1, bidding);
    }

    @Test(expected = Defect.class)
    public void throwsDefectIfNoExistingSniperForAnUpdate() {
        model.sniperStateChanged(SniperSnapshot.joining("item 0").bidding(555, 666));
    }

    private void assertRowMatchesSnapshot(int rowIndex, SniperSnapshot snapshot) {
        assertThat(cellValue(rowIndex, Column.ITEM_IDENTIFIER), equalTo(snapshot.itemId));
        assertThat(cellValue(rowIndex, Column.LAST_PRICE), equalTo(snapshot.lastPrice));
        assertThat(cellValue(rowIndex, Column.LAST_BID), equalTo(snapshot.lastBid));
        assertThat(cellValue(rowIndex, Column.SNIPER_STATE), equalTo(SnipersTableModel.textFor(snapshot.state)));
    }

    private Object cellValue(int rowIndex, Column column) {
        return model.getValueAt(rowIndex, column.ordinal());
    }

    private static Matcher<TableModelEvent> anEventInRow(Integer rowIndex) {
        return new TypeSafeDiagnosingMatcher<TableModelEvent>() {
            @Override
            public void describeTo(Description description) {
                description.appendText(format("a first row of <%s> and a last row of <%s>", rowIndex, rowIndex));
            }

            @Override
            protected boolean matchesSafely(TableModelEvent item, Description mismatchDescription) {
                return item.getFirstRow() == rowIndex && item.getLastRow() == rowIndex;
            }
        };
    }

    private static Matcher<TableModelEvent> anEventWithType(Integer eventType) {
        return new FeatureMatcher<TableModelEvent, Integer>(
                equalTo(eventType), "a type of", "was ") {
            @Override
            protected Integer featureValueOf(TableModelEvent actual) {
                return actual.getType();
            }
        };
    }

    private static Matcher<TableModelEvent> anInsertionAtRow(Integer rowIndex) {
        return both(anEventWithType(TableModelEvent.INSERT)).and(anEventInRow(rowIndex));
    }

    private static Matcher<TableModelEvent> aChangeInRow(Integer rowIndex) {
        return both(anEventWithType(TableModelEvent.UPDATE)).and(anEventInRow(rowIndex));
    }
}
