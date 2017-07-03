package auctionsniper.xmpp;

import auctionsniper.Auction;
import auctionsniper.AuctionHouse;
import auctionsniper.Item;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;

import java.io.IOException;

public class XMPPAuctionHouse implements AuctionHouse {

    public static final String XMPP_DOMAIN = "localhost";
    public static final String AUCTION_RESOURCE = "Auction";

    private final XMPPTCPConnection connection;

    private XMPPAuctionHouse(XMPPTCPConnection connection) {
        this.connection = connection;
    }

    @Override
    public Auction auctionFor(Item item) {
        return new XMPPAuction(connection, item.identifier());
    }

    @Override
    public void disconnect() {
        this.connection.disconnect();
    }

    public static XMPPAuctionHouse connect(String hostname, String username, String password)
        throws IOException, InterruptedException, XMPPException, SmackException {
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
            .setHost(hostname)
            .setXmppDomain(JidCreate.from(XMPP_DOMAIN).asDomainBareJid())
            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
//          .setDebuggerEnabled(true)
            .build();

        XMPPTCPConnection connection = new XMPPTCPConnection(config);
        connection.connect();
        connection.login(username, password, Resourcepart.from(AUCTION_RESOURCE));

        return new XMPPAuctionHouse(connection);
    }
}
