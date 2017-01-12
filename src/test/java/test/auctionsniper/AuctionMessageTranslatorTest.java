package test.auctionsniper;

import auctionsniper.AuctionEventListener;
import auctionsniper.AuctionMessageTranslator;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;
import org.junit.Test;

import static auctionsniper.AppConstants.CLOSE_COMMAND_FORMAT;
import static auctionsniper.AppConstants.PRICE_COMMAND_FORMAT;
import static java.lang.String.format;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AuctionMessageTranslatorTest {

    private static final Chat UNUSED_CHAT = null;

    private final AuctionEventListener listener = mock(AuctionEventListener.class);
    private final AuctionMessageTranslator translator = new AuctionMessageTranslator(listener);

    @Test
    public void notifiesAuctionClosedWhenMessageReceived() {
        Message message = new Message();
        message.setBody(CLOSE_COMMAND_FORMAT);

        translator.processMessage(UNUSED_CHAT, message);

        verify(listener).auctionClosed();
    }

    @Test
    public void notifiesBidDetailsWhenCurrentPriceMessageReceived() {
        Message message = new Message();
        message.setBody(format(PRICE_COMMAND_FORMAT, 192, 7, "Someone else"));

        translator.processMessage(UNUSED_CHAT, message);

        verify(listener).currentPrice(192, 7);
    }
}
