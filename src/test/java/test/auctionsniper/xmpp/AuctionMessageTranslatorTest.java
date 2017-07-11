package test.auctionsniper.xmpp;

import auctionsniper.AuctionEventListener;
import auctionsniper.xmpp.AuctionMessageTranslator;
import auctionsniper.xmpp.XMPPFailureReporter;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;
import org.junit.Test;

import static auctionsniper.AuctionEventListener.PriceSource.FromOtherBidder;
import static auctionsniper.AuctionEventListener.PriceSource.FromSniper;
import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.lang.String.format;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static test.auctionsniper.xmpp.MessageMaker.Message;
import static test.auctionsniper.xmpp.MessageMaker.body;

public class AuctionMessageTranslatorTest {

    public static final String PRICE_COMMAND_FORMAT = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: %d; Increment: %d; Bidder: %s;";
    public static final String CLOSE_COMMAND_FORMAT = "SOLVersion: 1.1; Event: CLOSE;";

    private static final Chat UNUSED_CHAT = null;
    private static final String SNIPER_ID = "sniperId";

    private final AuctionEventListener listener = mock(AuctionEventListener.class);
    private final XMPPFailureReporter failureReporter = mock(XMPPFailureReporter.class);
    private final AuctionMessageTranslator translator = new AuctionMessageTranslator(SNIPER_ID, listener, failureReporter);

    @Test
    public void notifiesAuctionClosedWhenMessageReceived() {
        Message closeMessage = make(a(Message, with(body, CLOSE_COMMAND_FORMAT)));

        translator.processMessage(UNUSED_CHAT, closeMessage);

        verify(listener).auctionClosed();
    }

    @Test
    public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromOtherBidder() {
        String messageText = format(PRICE_COMMAND_FORMAT, 192, 7, "Someone else");
        Message priceMessage = make(a(Message, with(body, messageText)));

        translator.processMessage(UNUSED_CHAT, priceMessage);

        verify(listener).currentPrice(192, 7, FromOtherBidder);
    }

    @Test
    public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromSniper() {
        String messageText = format(PRICE_COMMAND_FORMAT, 234, 5, SNIPER_ID);
        Message priceMessage = make(a(Message, with(body, messageText)));

        translator.processMessage(UNUSED_CHAT, priceMessage);

        verify(listener).currentPrice(234, 5, FromSniper);
    }

    @Test
    public void notifiesAuctionFailedWhenBadMessageReceived() {
        String messageText = "a bad message";
        Message badMessage = make(a(Message, with(body, messageText)));

        translator.processMessage(UNUSED_CHAT, badMessage);

        expectFailureWithMessage(messageText);
    }

    @Test
    public void notifiesAuctionFailedWhenEventTypeMissing() {
        String messageText = "SOLVersion: 1.1; CurrentPrice: 234; Increment: 5; Bidder: " + SNIPER_ID + ";";
        Message badMessage = make(a(Message, with(body, messageText)));

        translator.processMessage(UNUSED_CHAT, badMessage);

        expectFailureWithMessage(messageText);
    }

    @Test
    public void notifiesAuctionFailedWhenCurrentPriceMissing() {
        String messageText = "SOLVersion: 1.1; Event: PRICE; Increment: 5; Bidder: " + SNIPER_ID + ";";
        Message badMessage = make(a(Message, with(body, messageText)));

        translator.processMessage(UNUSED_CHAT, badMessage);

        expectFailureWithMessage(messageText);
    }

    @Test
    public void notifiesAuctionFailedWhenIncrementMissing() {
        String messageText = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 234; Bidder: " + SNIPER_ID + ";";
        Message badMessage = make(a(Message, with(body, messageText)));

        translator.processMessage(UNUSED_CHAT, badMessage);

        verify(listener).auctionFailed();
    }

    @Test
    public void notifiesAuctionFailedWhenBidderMissing() {
        String messageText = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 234; Increment: 5;";
        Message badMessage = make(a(Message, with(body, messageText)));

        translator.processMessage(UNUSED_CHAT, badMessage);

        verify(listener).auctionFailed();
    }

    private void expectFailureWithMessage(String badMessage) {
        verify(listener).auctionFailed();
        verify(failureReporter).cannotTranslateMessage(eq(SNIPER_ID), eq(badMessage), any(Exception.class));
    }
}
