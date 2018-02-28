package test.auctionsniper.support

import auctionsniper.xmpp.XMPPAuction
import auctionsniper.xmpp.XMPPAuctionHouse
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.chat.Chat
import org.jivesoftware.smack.chat.ChatManager
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart
import test.auctionsniper.support.Constants.AUCTION_PASSWORD
import test.auctionsniper.xmpp.AuctionMessageTranslatorTest

class FakeAuctionServer(hostname: String) {

    private val messageListener = SingleMessageListener()
    private val connection: XMPPTCPConnection
    private lateinit var itemId: String
    private lateinit var currentChat: Chat

    init {
        val config = XMPPTCPConnectionConfiguration.builder()
            .setHost(hostname)
            .setXmppDomain(JidCreate.from(XMPPAuctionHouse.XMPP_DOMAIN).asDomainBareJid())
            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
//          .setDebuggerEnabled(true)
            .build()
        connection = XMPPTCPConnection(config)
    }

    fun stop() {
        connection.disconnect()
    }

    fun startSellingItem(itemId: String) {
        this.itemId = itemId

        connection.connect()
        connection.login(
            java.lang.String.format(XMPPAuction.ITEM_ID_AS_LOGIN, itemId),
            AUCTION_PASSWORD,
            Resourcepart.from(XMPPAuctionHouse.AUCTION_RESOURCE)
        )
        val listener = { chat: Chat, _: Boolean ->
            currentChat = chat
            chat.addMessageListener(messageListener)
        }
        ChatManager.getInstanceFor(connection).addChatListener(listener)
    }

    fun hasReceivedJoinRequestFrom(sniperId: String) {
        receivesAMessageMatching(sniperId, CoreMatchers.equalTo(XMPPAuction.JOIN_COMMAND_FORMAT))
    }

    fun hasReceivedBid(bid: Int, sniperId: String) {
        receivesAMessageMatching(
            sniperId,
            CoreMatchers.equalTo(java.lang.String.format(XMPPAuction.BID_COMMAND_FORMAT, bid))
        )
    }

    fun announceClosed() {
        currentChat.sendMessage(AuctionMessageTranslatorTest.CLOSE_COMMAND_FORMAT)
    }

    fun reportPrice(price: Int, increment: Int, bidder: String) {
        currentChat.sendMessage(
            java.lang.String.format(
                AuctionMessageTranslatorTest.PRICE_COMMAND_FORMAT,
                price,
                increment,
                bidder
            )
        )
    }

    fun itemId(): String = itemId

    fun sendInvalidMessageContaining(message: String) {
        currentChat.sendMessage(message)
    }

    private fun receivesAMessageMatching(sniperId: String, messageMatcher: Matcher<in String>) {
        messageListener.receivesAMessage(messageMatcher)
        MatcherAssert.assertThat(currentChat.participant.asUnescapedString(), CoreMatchers.equalTo(sniperId))
    }
}
