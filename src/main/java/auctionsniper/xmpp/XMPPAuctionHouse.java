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

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class XMPPAuctionHouse implements AuctionHouse {

    public static final String XMPP_DOMAIN = "localhost";
    public static final String AUCTION_RESOURCE = "Auction";
    public static final String LOG_FILE_NAME = "auction-sniper.log";
    private static final String LOGGER_NAME = "auctionsniper";

    private final XMPPTCPConnection connection;
    private final LoggingXMPPFailureReporter failureReporter;

    private XMPPAuctionHouse(XMPPTCPConnection connection) throws XMPPAuctionException {
        this.connection = connection;
        failureReporter = new LoggingXMPPFailureReporter(makeLogger());
    }

    private Logger makeLogger() throws XMPPAuctionException {
        Logger logger = Logger.getLogger(LOGGER_NAME);
        logger.setUseParentHandlers(false);
        logger.addHandler(simpleFileHandler());
        return logger;
    }

    private FileHandler simpleFileHandler() throws XMPPAuctionException {
        try {
            FileHandler fileHandler = new FileHandler(LOG_FILE_NAME);
            fileHandler.setFormatter(new SimpleFormatter());
            return fileHandler;
        } catch (Exception e) {
            throw new XMPPAuctionException("Could not create logger FileHandler " + getFullPath(LOG_FILE_NAME), e);
        }
    }

    private String getFullPath(String logFileName) {
        return new File(logFileName).getPath();
    }

    @Override
    public Auction auctionFor(Item item) {
        return new XMPPAuction(connection, item.identifier(), failureReporter);
    }

    @Override
    public void disconnect() {
        this.connection.disconnect();
    }

    public static XMPPAuctionHouse connect(String hostname, String username, String password)
        throws IOException, InterruptedException, XMPPException, SmackException, XMPPAuctionException {
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
