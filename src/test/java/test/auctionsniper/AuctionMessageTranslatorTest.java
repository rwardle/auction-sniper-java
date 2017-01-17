package test.auctionsniper;

import auctionsniper.AuctionEventListener;
import auctionsniper.AuctionMessageTranslator;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;
import org.junit.Test;

import static auctionsniper.AppConstants.CLOSE_COMMAND_FORMAT;
import static auctionsniper.AppConstants.PRICE_COMMAND_FORMAT;
import static auctionsniper.AuctionEventListener.PriceSource.FromOtherBidder;
import static auctionsniper.AuctionEventListener.PriceSource.FromSniper;
import static java.lang.String.format;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AuctionMessageTranslatorTest {

    private static final Chat UNUSED_CHAT = null;
    private static final String SNIPER_ID = "sniperId";

    private final AuctionEventListener listener = mock(AuctionEventListener.class);
    private final AuctionMessageTranslator translator = new AuctionMessageTranslator(SNIPER_ID, listener);

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
}
