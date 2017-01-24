package auctionsniper.ui;

import auctionsniper.SniperState;

import javax.swing.table.AbstractTableModel;

import static auctionsniper.AppConstants.STATUS_JOINING;

public class SnipersTableModel extends AbstractTableModel {

    public enum Column {
        ITEM_IDENTIFIER, LAST_PRICE, LAST_BID, SNIPER_STATUS;

        public static Column at(int offset) {
            return values()[offset];
        }
    }

    private static final SniperState STARTING_UP = new SniperState("", 0, 0);

    private String statusText = STATUS_JOINING;
    private SniperState sniperState = STARTING_UP;

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (Column.at(columnIndex)) {
            case ITEM_IDENTIFIER:
                return sniperState.itemId;
            case LAST_PRICE:
                return sniperState.lastPrice;
            case LAST_BID:
                return sniperState.lastBid;
            case SNIPER_STATUS:
                return statusText;
            default:
                throw new IllegalArgumentException("No column at " + columnIndex);
        }
    }

    public void setStatusText(String newStatusText) {
        statusText = newStatusText;
        fireTableRowsUpdated(0, 0);
    }

    public void sniperStatusChanged(SniperState newSniperState, String newStatusText) {
        sniperState = newSniperState;
        statusText = newStatusText;
        fireTableRowsUpdated(0, 0);
    }
}
