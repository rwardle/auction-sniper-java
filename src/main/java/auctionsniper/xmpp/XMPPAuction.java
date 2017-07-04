package auctionsniper.xmpp;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jmock.example.announcer.Announcer;
import org.jxmpp.jid.EntityJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import static auctionsniper.xmpp.XMPPAuctionHouse.AUCTION_RESOURCE;
import static java.lang.String.format;

public class XMPPAuction implements Auction {

    public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
    public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";
    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

    private final Announcer<AuctionEventListener> auctionEventListeners = Announcer.to(AuctionEventListener.class);
    private final XMPPFailureReporter failureReporter;
    private final Chat chat;

    XMPPAuction(XMPPTCPConnection connection, String itemId, XMPPFailureReporter failureReporter) {
        this.failureReporter = failureReporter;
        AuctionMessageTranslator translator = translatorFor(connection);
        this.chat = ChatManager.getInstanceFor(connection).createChat(auctionId(itemId, connection), translator);
        addAuctionEventListener(chatDisconnectorFor(translator));
    }

    private AuctionEventListener chatDisconnectorFor(AuctionMessageTranslator translator) {
        return new AuctionEventListener() {
            @Override
            public void auctionFailed() {
                chat.removeMessageListener(translator);
            }

            @Override
            public void auctionClosed() {
                // no-op
            }

            @Override
            public void currentPrice(int price, int increment, PriceSource priceSource) {
                // no-op
            }
        };
    }

    private AuctionMessageTranslator translatorFor(XMPPTCPConnection connection) {
        return new AuctionMessageTranslator(connection.getUser().asUnescapedString(), auctionEventListeners.announce(),
            failureReporter);
    }

    @Override
    public void bid(int amount) {
        try {
            chat.sendMessage(format(BID_COMMAND_FORMAT, amount));
        } catch (SmackException.NotConnectedException | InterruptedException e) {
            // TODO report failure
            e.printStackTrace();
        }
    }

    @Override
    public void join() {
        try {
            chat.sendMessage(JOIN_COMMAND_FORMAT);
        } catch (SmackException.NotConnectedException | InterruptedException e) {
            // TODO report failure
            e.printStackTrace();
        }
    }

    @Override
    public void addAuctionEventListener(AuctionEventListener listener) {
        auctionEventListeners.addListener(listener);
    }

    private static EntityJid auctionId(String itemId, XMPPConnection connection) {
        String jid = format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
        try {
            return JidCreate.from(jid).asEntityJidIfPossible();
        } catch (XmppStringprepException e) {
            throw new IllegalArgumentException(format("Invalid JID: %s", jid));
        }
    }
}
