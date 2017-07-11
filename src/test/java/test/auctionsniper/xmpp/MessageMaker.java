package test.auctionsniper.xmpp;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import org.jivesoftware.smack.packet.Message;

import static com.natpryce.makeiteasy.Property.newProperty;

class MessageMaker {

    static final Property<Message, String> body = newProperty();

    static final Instantiator<Message> Message = lookup -> {
        Message message = new Message();
        message.setBody(lookup.valueOf(body, "message body"));
        return message;
    };
}
