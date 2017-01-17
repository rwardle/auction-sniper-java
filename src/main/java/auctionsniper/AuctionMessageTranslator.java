package auctionsniper;

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

    public AuctionMessageTranslator(String sniperId, AuctionEventListener listener) {
        this.sniperId = sniperId;
        this.listener = listener;
    }

    public void processMessage(Chat chat, Message message) {
        AuctionEvent event = AuctionEvent.from(message.getBody());

        switch (event.type()) {
            case EVENT_TYPE_CLOSE:
                listener.auctionClosed();
                break;
            case EVENT_TYPE_PRICE:
                listener.currentPrice(event.currentPrice(), event.increment(), event.isFrom(sniperId));
                break;
        }
    }

    private static class AuctionEvent {

        private final Map<String, String> fields;

        private AuctionEvent(String messageBody) {
            fields = Stream.of(messageBody.split(";"))
                    .map(e -> e.split(":"))
                    .collect(toMap(p -> p[0].trim(), p -> p[1].trim()));
        }

        String type() {
            return get("Event");
        }

        int currentPrice() {
            return getInt("CurrentPrice");
        }

        int increment() {
            return getInt("Increment");
        }

        AuctionEventListener.PriceSource isFrom(String sniperId) {
            return sniperId.equals(bidder()) ? FromSniper : FromOtherBidder;
        }

        private String bidder() {
            return get("Bidder");
        }

        private String get(String fieldName) {
            return fields.get(fieldName);
        }

        private int getInt(String fieldName) {
            return Integer.parseInt(get(fieldName));
        }

        static AuctionEvent from(String messageBody) {
            return new AuctionEvent(messageBody);
        }
    }
}
