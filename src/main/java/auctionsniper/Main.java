package auctionsniper;

import auctionsniper.ui.MainWindow;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.EntityJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import javax.swing.*;

import static auctionsniper.AppConstants.*;

public class Main {

    private static final int ARGS_HOSTNAME = 0;
    private static final int ARGS_USERNAME = 1;
    private static final int ARGS_PASSWORD = 2;
    private static final int ARGS_ITEM_ID = 3;

    private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

    private MainWindow ui;

    public Main() throws Exception {
        startUserInterface();
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(() -> ui = new MainWindow());
    }

    public static void main(String... args) throws Exception {
        new Main();

        XMPPConnection connection = connectTo(args[ARGS_HOSTNAME], args[ARGS_USERNAME], args[ARGS_PASSWORD]);
        Chat chat = ChatManager.getInstanceFor(connection).createChat(
                auctionId(args[ARGS_ITEM_ID], connection),
                (aChat, aMessage) -> {
                    // TODO
                });
        chat.sendMessage(new Message());
    }

    private static XMPPConnection connectTo(String hostname, String username, String password) throws Exception {
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
        return JidCreate.from(String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName())).asEntityJidIfPossible();
    }
}
