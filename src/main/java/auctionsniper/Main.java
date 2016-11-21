package auctionsniper;

import auctionsniper.ui.MainWindow;

import javax.swing.*;

public class Main {

    private MainWindow ui;

    public Main() throws Exception {
        startUserInterface();
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(() -> ui = new MainWindow());
    }

    public static void main(String... args) throws Exception {
        new Main();
    }
}
