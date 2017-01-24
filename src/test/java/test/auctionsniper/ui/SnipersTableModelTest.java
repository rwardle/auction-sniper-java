package test.auctionsniper.ui;

import auctionsniper.SniperState;
import auctionsniper.ui.SnipersTableModel;
import auctionsniper.ui.SnipersTableModel.Column;
import org.junit.Before;
import org.junit.Test;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import static auctionsniper.AppConstants.STATUS_BIDDING;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
    public void setsSniperValuesInColumns() {
        model.sniperStatusChanged(new SniperState("item id", 555, 666), STATUS_BIDDING);

        assertColumnEquals(Column.ITEM_IDENTIFIER, "item id");
        assertColumnEquals(Column.LAST_PRICE, 555);
        assertColumnEquals(Column.LAST_BID, 666);
        assertColumnEquals(Column.SNIPER_STATUS, STATUS_BIDDING);

        verify(listener).tableChanged(refEq(new TableModelEvent(model, 0)));
    }

    private void assertColumnEquals(Column column, Object expected) {
        int rowIndex = 0;
        int columnIndex = column.ordinal();
        assertEquals(expected, model.getValueAt(rowIndex, columnIndex));
    }
}
