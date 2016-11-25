package auctionsniper.ui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

import static auctionsniper.AppConstants.*;

public class MainWindow extends JFrame {
    private final JLabel sniperStatus = createLabel(STATUS_JOINING);

    public MainWindow() {
        super("Auction Sniper");
        setName(MAIN_WINDOW_NAME);
        add(sniperStatus);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private static JLabel createLabel(String initialText) {
        JLabel result = new JLabel(initialText);
        result.setName(SNIPER_STATUS_NAME);
        result.setBorder(new LineBorder(Color.black));
        return result;
    }
}
