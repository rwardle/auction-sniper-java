package auctionsniper;

import org.hamcrest.Matcher;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;

import static auctionsniper.AppConstants.*;
import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class FakeAuctionServer {

    private static final String AUCTION_PASSWORD = "auction";

    private final SingleMessageListener messageListener = new SingleMessageListener();
    private final String itemId;
    private final XMPPTCPConnection connection;

    private Chat currentChat;

    public FakeAuctionServer(String hostname, String itemId) throws XmppStringprepException {
        this.itemId = itemId;
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setHost(hostname)
                .setXmppDomain(JidCreate.from(XMPP_DOMAIN).asDomainBareJid())
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
//                .setDebuggerEnabled(true)
                .build();
        this.connection = new XMPPTCPConnection(config);
    }

    public void startSellingItem() throws IOException, XMPPException, SmackException, InterruptedException {
        connection.connect();
        connection.login(format(ITEM_ID_AS_LOGIN, itemId), AUCTION_PASSWORD, Resourcepart.from(AUCTION_RESOURCE));
        ChatManager.getInstanceFor(connection).addChatListener(
                (chat, createdLocally) -> {
                    currentChat = chat;
                    chat.addMessageListener(messageListener);
                });
    }

    public void hasReceivedJoinRequestFrom(String sniperId) throws InterruptedException {
        receivesAMessageMatching(sniperId, equalTo(JOIN_COMMAND_FORMAT));
    }

    private void receivesAMessageMatching(String sniperId, Matcher<? super String> messageMatcher) throws InterruptedException {
        messageListener.receivesAMessage(messageMatcher);
        assertThat(currentChat.getParticipant(), equalTo(sniperId));
    }

    public void reportPrice(int price, int increment, String bidder) throws SmackException, InterruptedException {
        currentChat.sendMessage(format(PRICE_COMMAND_FORMAT, price, increment, bidder));
    }

    public void hasReceivedBid(int bid, String sniperId) throws InterruptedException {
        receivesAMessageMatching(sniperId, equalTo(format(BID_COMMAND_FORMAT, bid)));
    }

    public void announceClosed() throws SmackException, InterruptedException {
        currentChat.sendMessage(CLOSE_COMMAND_FORMAT);
    }

    public void stop() {
        connection.disconnect();
    }

    public String getItemId() {
        return itemId;
    }
}
