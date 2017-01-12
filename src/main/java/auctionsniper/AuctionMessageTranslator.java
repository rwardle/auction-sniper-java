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
        Map<String, String> event = unpackEventFrom(message);

        switch(event.get("Event")) {
            case "CLOSE":
                listener.auctionClosed();
                break;
            case "PRICE":
                listener.currentPrice(
                        Integer.parseInt(event.get("CurrentPrice")),
                        Integer.parseInt(event.get("Increment")));
                break;
        }
    }

    private Map<String, String> unpackEventFrom(Message message) {
        return Stream.of(message.getBody().split(";"))
                .map(e -> e.split(":"))
                .collect(toMap(p -> p[0].trim(), p -> p[1].trim()));
    }
}
