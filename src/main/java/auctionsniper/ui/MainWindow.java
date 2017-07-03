package auctionsniper.ui;

import auctionsniper.Item;
import auctionsniper.SniperPortfolio;
import auctionsniper.UserRequestListener;
import org.jmock.example.announcer.Announcer;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.function.Supplier;

public class MainWindow extends JFrame {

    public static final String APPLICATION_TITLE = "Auction Sniper";
    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    public static final String SNIPERS_TABLE_NAME = "snipers table";
    public static final String NEW_ITEM_ID_FIELD_NAME = "new item id field";
    public static final String STOP_PRICE_FIELD_NAME = "stop price field";
    public static final String JOIN_BUTTON_NAME = "join button";

    private final Announcer<UserRequestListener> userRequests = Announcer.to(UserRequestListener.class);

    public MainWindow(SniperPortfolio portfolio) {
        super(APPLICATION_TITLE);
        setName(MAIN_WINDOW_NAME);
        fillContentPane(makeSnipersTable(portfolio), makeControls());
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void addUserRequestListener(UserRequestListener listener) {
        userRequests.addListener(listener);
    }

    private void fillContentPane(JTable snipersTable, JPanel controlsPanel) {
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
        contentPane.add(controlsPanel, BorderLayout.NORTH);
    }

    private JPanel makeControls() {
        JTextField itemIdField = new JTextField();
        itemIdField.setColumns(20);
        itemIdField.setName(NEW_ITEM_ID_FIELD_NAME);

        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setGroupingUsed(false);
        JFormattedTextField stopPriceField = new JFormattedTextField(numberFormat);
        stopPriceField.setColumns(20);
        stopPriceField.setName(STOP_PRICE_FIELD_NAME);

        JButton joinAuctionButton = new JButton("Join Auction");
        joinAuctionButton.setName(JOIN_BUTTON_NAME);

        Supplier<String> itemId = itemIdField::getText;
        Supplier<Integer> stopPrice = () -> ((Number) stopPriceField.getValue()).intValue();
        joinAuctionButton.addActionListener(
            e -> userRequests.announce().joinAuction(new Item(itemId.get(), stopPrice.get())));

        JPanel controls = new JPanel(new FlowLayout());
        controls.add(new JLabel("Item:"));
        controls.add(itemIdField);
        controls.add(new JLabel("Stop price:"));
        controls.add(stopPriceField);
        controls.add(joinAuctionButton);

        return controls;
    }

    private JTable makeSnipersTable(SniperPortfolio portfolio) {
        SnipersTableModel model = new SnipersTableModel();
        portfolio.addPortfolioListener(model);
        JTable snipersTable = new JTable(model);
        snipersTable.setName(SNIPERS_TABLE_NAME);
        return snipersTable;
    }
}
