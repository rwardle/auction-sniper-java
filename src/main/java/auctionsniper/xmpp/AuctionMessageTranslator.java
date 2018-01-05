package auctionsniper.xmpp;

import auctionsniper.AuctionEventListener;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.Map;
import java.util.stream.Stream;

import static auctionsniper.AuctionEventListener.PriceSource.FromOtherBidder;
import static auctionsniper.AuctionEventListener.PriceSource.FromSniper;
import static java.util.stream.Collectors.toMap;

public class AuctionMessageTranslator implements ChatMessageListener {

    private static final String EVENT_TYPE_CLOSE = "CLOSE";
    private static final String EVENT_TYPE_PRICE = "PRICE";

    private final String sniperId;
    private final AuctionEventListener listener;
    private final XMPPFailureReporter failureReporter;

    public AuctionMessageTranslator(String sniperId, AuctionEventListener listener, XMPPFailureReporter failureReporter) {
        this.sniperId = sniperId;
        this.listener = listener;
        this.failureReporter = failureReporter;
    }

    public void processMessage(Chat chat, Message message) {
        String messageBody = message.getBody();
        try {
            translate(messageBody);
        } catch (Exception parseException) {
            failureReporter.cannotTranslateMessage(sniperId, messageBody, parseException);
            listener.auctionFailed();
        }
    }

    private void translate(String messageBody) throws MissingValueException {
        AuctionEvent event = AuctionEvent.from(messageBody);

        String eventType = event.type();
        if (EVENT_TYPE_CLOSE.equals(eventType)) {
            listener.auctionClosed();
        } else if (EVENT_TYPE_PRICE.equals(eventType)) {
            listener.currentPrice(event.currentPrice(), event.increment(), event.isFrom(sniperId));
        }
    }

    private static class AuctionEvent {

        private final Map<String, String> fields;

        private AuctionEvent(String messageBody) {
            fields = Stream.of(messageBody.split(";"))
                    .map(e -> e.split(":"))
                    .collect(toMap(p -> p[0].trim(), p -> p[1].trim()));
        }

        String type() throws MissingValueException {
            return get("Event");
        }

        int currentPrice() throws MissingValueException {
            return getInt("CurrentPrice");
        }

        int increment() throws MissingValueException {
            return getInt("Increment");
        }

        AuctionEventListener.PriceSource isFrom(String sniperId) throws MissingValueException {
            return sniperId.equals(bidder()) ? FromSniper : FromOtherBidder;
        }

        private String bidder() throws MissingValueException {
            return get("Bidder");
        }

        private String get(String fieldName) throws MissingValueException {
            String value = fields.get(fieldName);
            if (null == value) {
                throw new MissingValueException(fieldName);
            }
            return value;
        }

        private int getInt(String fieldName) throws MissingValueException {
            return Integer.parseInt(get(fieldName));
        }

        static AuctionEvent from(String messageBody) {
            return new AuctionEvent(messageBody);
        }
    }
}
