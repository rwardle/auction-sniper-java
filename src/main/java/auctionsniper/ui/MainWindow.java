package auctionsniper.ui;

import org.jmock.example.announcer.Announcer;

import javax.swing.*;
import java.awt.*;

import static auctionsniper.AppConstants.*;

public class MainWindow extends JFrame {

    private final Announcer<UserRequestListener> userRequests = Announcer.to(UserRequestListener.class);

    public MainWindow(SnipersTableModel snipers) {
        super(APPLICATION_TITLE);
        setName(MAIN_WINDOW_NAME);
        fillContentPane(makeSnipersTable(snipers), makeControls());
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
        itemIdField.setColumns(25);
        itemIdField.setName(NEW_ITEM_ID_FIELD_NAME);

        JButton joinAuctionButton = new JButton("Join Auction");
        joinAuctionButton.setName(JOIN_BUTTON_NAME);
        joinAuctionButton.addActionListener(e -> userRequests.announce().joinAuction(itemIdField.getText()));

        JPanel controls = new JPanel(new FlowLayout());
        controls.add(itemIdField);
        controls.add(joinAuctionButton);

        return controls;
    }

    private JTable makeSnipersTable(SnipersTableModel snipers) {
        JTable snipersTable = new JTable(snipers);
        snipersTable.setName(SNIPERS_TABLE_NAME);
        return snipersTable;
    }

}
