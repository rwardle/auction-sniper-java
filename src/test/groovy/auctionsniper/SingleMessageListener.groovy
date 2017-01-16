package auctionsniper

import org.hamcrest.Matcher
import org.jivesoftware.smack.chat.Chat
import org.jivesoftware.smack.chat.ChatMessageListener
import org.jivesoftware.smack.packet.Message

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit

import static org.hamcrest.CoreMatchers.is
import static org.hamcrest.CoreMatchers.notNullValue
import static org.junit.Assert.assertThat

class SingleMessageListener implements ChatMessageListener {

    def messages = new ArrayBlockingQueue<Message>(1)

    @Override
    void processMessage(Chat chat, Message message) {
        messages.add(message)
    }

    void receivesAMessage(Matcher<? super String> messageMatcher) {
        def message = messages.poll(5, TimeUnit.SECONDS)
        assertThat("Message", message, is(notNullValue()))
        assertThat(message.getBody(), messageMatcher)
    }
}
