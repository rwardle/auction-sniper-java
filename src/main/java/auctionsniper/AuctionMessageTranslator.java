package auctionsniper;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class AuctionMessageTranslator implements ChatMessageListener {

    private final AuctionEventListener listener;

    public AuctionMessageTranslator(AuctionEventListener listener) {
        this.listener = listener;
    }

    public void processMessage(Chat chat, Message message) {
        AuctionEvent event = AuctionEvent.from(message.getBody());

        switch (event.type()) {
            case "CLOSE":
                listener.auctionClosed();
                break;
            case "PRICE":
                listener.currentPrice(event.currentPrice(), event.increment());
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

        public String type() {
            return get("Event");
        }

        public int currentPrice() {
            return getInt("CurrentPrice");
        }

        public int increment() {
            return getInt("Increment");
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
