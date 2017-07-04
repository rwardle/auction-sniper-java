package test.auctionsniper.xmpp;

import auctionsniper.AuctionEventListener;
import auctionsniper.xmpp.AuctionMessageTranslator;
import auctionsniper.xmpp.XMPPFailureReporter;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;
import org.junit.Test;

import static auctionsniper.AuctionEventListener.PriceSource.FromOtherBidder;
import static auctionsniper.AuctionEventListener.PriceSource.FromSniper;
import static java.lang.String.format;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
        Message message = new Message();
        message.setBody(CLOSE_COMMAND_FORMAT);

        translator.processMessage(UNUSED_CHAT, message);

        verify(listener).auctionClosed();
    }

    @Test
    public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromOtherBidder() {
        Message message = new Message();
        message.setBody(format(PRICE_COMMAND_FORMAT, 192, 7, "Someone else"));

        translator.processMessage(UNUSED_CHAT, message);

        verify(listener).currentPrice(192, 7, FromOtherBidder);
    }

    @Test
    public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromSniper() {
        Message message = new Message();
        message.setBody(format(PRICE_COMMAND_FORMAT, 234, 5, SNIPER_ID));

        translator.processMessage(UNUSED_CHAT, message);

        verify(listener).currentPrice(234, 5, FromSniper);
    }

    @Test
    public void notifiesAuctionFailedWhenBadMessageReceived() {
        String badMessage = "a bad message";

        translator.processMessage(UNUSED_CHAT, message(badMessage));

        expectFailureWithMessage(badMessage);
    }

    @Test
    public void notifiesAuctionFailedWhenEventTypeMissing() {
        String badMessage = "SOLVersion: 1.1; CurrentPrice: 234; Increment: 5; Bidder: " + SNIPER_ID + ";";

        translator.processMessage(UNUSED_CHAT, message(badMessage));

        expectFailureWithMessage(badMessage);
    }

    @Test
    public void notifiesAuctionFailedWhenCurrentPriceMissing() {
        String badMessage = "SOLVersion: 1.1; Event: PRICE; Increment: 5; Bidder: " + SNIPER_ID + ";";

        translator.processMessage(UNUSED_CHAT, message(badMessage));

        expectFailureWithMessage(badMessage);
    }

    @Test
    public void notifiesAuctionFailedWhenIncrementMissing() {
        String badMessage = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 234; Bidder: " + SNIPER_ID + ";";

        translator.processMessage(UNUSED_CHAT, message(badMessage));

        verify(listener).auctionFailed();
    }

    @Test
    public void notifiesAuctionFailedWhenBidderMissing() {
        String badMessage = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 234; Increment: 5;";

        translator.processMessage(UNUSED_CHAT, message(badMessage));

        verify(listener).auctionFailed();
    }

    private void expectFailureWithMessage(String badMessage) {
        verify(listener).auctionFailed();
        verify(failureReporter).cannotTranslateMessage(eq(SNIPER_ID), eq(badMessage), any(Exception.class));
    }

    private static Message message(String body) {
        Message message = new Message();
        message.setBody(body);
        return message;
    }
}
