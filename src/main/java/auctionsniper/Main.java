package auctionsniper;

import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static auctionsniper.AppConstants.*;
import static java.lang.String.format;

public class Main {

    private static final int ARGS_HOSTNAME = 0;
    private static final int ARGS_USERNAME = 1;
    private static final int ARGS_PASSWORD = 2;

    private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

    private final SnipersTableModel snipers = new SnipersTableModel();
    private MainWindow ui;

    @SuppressWarnings("unused")
    private List<Chat> notToBeGCd = new ArrayList<>();

    public Main() throws Exception {
        startUserInterface();
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(() -> ui = new MainWindow(snipers));
    }

    public static void main(String... args) throws Exception {
        Main main = new Main();
        XMPPTCPConnection connection = connection(args[ARGS_HOSTNAME], args[ARGS_USERNAME], args[ARGS_PASSWORD]);
        main.disconnectWhenUICloses(connection);

        Arrays.stream(args).skip(3).forEach(itemId -> main.joinAuction(connection, itemId));
    }

    private void joinAuction(XMPPTCPConnection connection, String itemId) {
        safelyAddItemToModel(itemId);

        Chat chat = ChatManager.getInstanceFor(connection).createChat(auctionId(itemId, connection));
        notToBeGCd.add(chat);

        Auction auction = new XMPPAuction(chat);
        chat.addMessageListener(
                new AuctionMessageTranslator(
                        connection.getUser().asUnescapedString(),
                        new AuctionSniper(itemId, auction, new SwingThreadSniperListener(snipers))));
        auction.join();
    }

    private void safelyAddItemToModel(String itemId) {
        SwingUtilities.invokeLater(() -> snipers.addSniper(SniperSnapshot.joining(itemId)));
    }

    private void disconnectWhenUICloses(XMPPTCPConnection connection) {
        ui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                connection.disconnect();
            }
        });
    }

    private static XMPPTCPConnection connection(String hostname, String username, String password) throws Exception {
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

    private static EntityJid auctionId(String itemId, XMPPConnection connection) {
        String jid = format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
        try {
            return JidCreate.from(jid).asEntityJidIfPossible();
        } catch (XmppStringprepException e) {
            throw new IllegalArgumentException(format("Invalid JID: %s", jid));
        }
    }

    private static class SwingThreadSniperListener implements SniperListener {

        private final SnipersTableModel snipers;

        SwingThreadSniperListener(SnipersTableModel snipers) {
            this.snipers = snipers;
        }

        @Override
        public void sniperStateChanged(SniperSnapshot sniperSnapshot) {
            SwingUtilities.invokeLater(() -> snipers.sniperStateChanged(sniperSnapshot));
        }
    }
}
