package auctionsniper;

import auctionsniper.ui.MainWindow;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.EntityJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static auctionsniper.AppConstants.*;
import static java.lang.String.format;

public class Main {

    private static final int ARGS_HOSTNAME = 0;
    private static final int ARGS_USERNAME = 1;
    private static final int ARGS_PASSWORD = 2;
    private static final int ARGS_ITEM_ID = 3;

    private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

    private MainWindow ui;

    @SuppressWarnings("unused")
    private Chat notToBeGCd;

    public Main() throws Exception {
        startUserInterface();
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(() -> ui = new MainWindow());
    }

    public static void main(String... args) throws Exception {
        Main main = new Main();
        main.joinAuction(
                connectTo(args[ARGS_HOSTNAME], args[ARGS_USERNAME], args[ARGS_PASSWORD]),
                args[ARGS_ITEM_ID]);
    }

    private void joinAuction(XMPPTCPConnection connection, String itemId) throws Exception {
        disconnectWhenUICloses(connection);

        Chat chat = ChatManager.getInstanceFor(connection).createChat(auctionId(itemId, connection));
        this.notToBeGCd = chat;

        Auction auction = new XMPPAuction(chat);
        chat.addMessageListener(
                new AuctionMessageTranslator(
                        connection.getUser().asUnescapedString(),
                        new AuctionSniper(auction, itemId, new SniperStateDisplayer())));
        auction.join();
    }

    private void disconnectWhenUICloses(XMPPTCPConnection connection) {
        ui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                connection.disconnect();
            }
        });
    }

    private static XMPPTCPConnection connectTo(String hostname, String username, String password) throws Exception {
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setHost(hostname)
                .setXmppDomain(JidCreate.from(XMPP_DOMAIN).asDomainBareJid())
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
//                .setDebuggerEnabled(true)
                .build();
        XMPPTCPConnection connection = new XMPPTCPConnection(config);
        connection.connect();
        connection.login(username, password, Resourcepart.from(AUCTION_RESOURCE));
        return connection;
    }

    private static EntityJid auctionId(String itemId, XMPPConnection connection) throws XmppStringprepException {
        return JidCreate.from(format(AUCTION_ID_FORMAT, itemId, connection.getServiceName())).asEntityJidIfPossible();
    }

    public class SniperStateDisplayer implements SniperListener {

        @Override
        public void sniperLost() {
            showStatus(STATUS_LOST);
        }

        @Override
        public void sniperBidding(SniperState sniperState) {
            SwingUtilities.invokeLater(() -> ui.sniperStatusChanged(sniperState, STATUS_BIDDING));
        }

        @Override
        public void sniperWinning() {
            showStatus(STATUS_WINNING);
        }

        @Override
        public void sniperWon() {
            showStatus(STATUS_WON);
        }

        private void showStatus(String status) {
            SwingUtilities.invokeLater(() -> ui.showStatusText(status));
        }
    }
}
