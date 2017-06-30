package auctionsniper

import org.hamcrest.Matcher
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.chat.Chat
import org.jivesoftware.smack.chat.ChatManager
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart

import static auctionsniper.xmpp.XMPPAuction.*
import static auctionsniper.xmpp.XMPPAuctionHouse.AUCTION_RESOURCE
import static auctionsniper.xmpp.XMPPAuctionHouse.XMPP_DOMAIN
import static java.lang.String.format
import static org.hamcrest.CoreMatchers.equalTo
import static org.junit.Assert.assertThat
import static test.auctionsniper.xmpp.AuctionMessageTranslatorTest.CLOSE_COMMAND_FORMAT
import static test.auctionsniper.xmpp.AuctionMessageTranslatorTest.PRICE_COMMAND_FORMAT

class FakeAuctionServer {

    static final String AUCTION_PASSWORD = "auction"

    def messageListener = new SingleMessageListener()

    private String itemId
    private XMPPTCPConnection connection
    private Chat currentChat

    FakeAuctionServer(String hostname, String itemId) {
        this.itemId = itemId

        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
            .setHost(hostname)
            .setXmppDomain(JidCreate.from(XMPP_DOMAIN).asDomainBareJid())
            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
//          .setDebuggerEnabled(true)
            .build()
        connection = new XMPPTCPConnection(config)
    }

    void startSellingItem() {
        connection.connect()
        connection.login(format(ITEM_ID_AS_LOGIN, itemId), AUCTION_PASSWORD, Resourcepart.from(AUCTION_RESOURCE))
        def listener = { chat, createdLocally ->
            currentChat = chat
            chat.addMessageListener(messageListener)
        }
        ChatManager.getInstanceFor(connection).addChatListener(listener)
    }

    void hasReceivedJoinRequestFrom(String sniperId) {
        receivesAMessageMatching(sniperId, equalTo(JOIN_COMMAND_FORMAT))
    }

    private void receivesAMessageMatching(String sniperId, Matcher<? super String> messageMatcher) {
        messageListener.receivesAMessage(messageMatcher)
        assertThat(currentChat.getParticipant().asUnescapedString(), equalTo(sniperId))
    }

    void reportPrice(int price, int increment, String bidder) {
        currentChat.sendMessage(format(PRICE_COMMAND_FORMAT, price, increment, bidder))
    }

    void hasReceivedBid(int bid, String sniperId) {
        receivesAMessageMatching(sniperId, equalTo(format(BID_COMMAND_FORMAT, bid)))
    }

    void announceClosed() {
        currentChat.sendMessage(CLOSE_COMMAND_FORMAT)
    }

    void stop() {
        connection.disconnect()
    }

    String getItemId() {
        return itemId
    }
}
