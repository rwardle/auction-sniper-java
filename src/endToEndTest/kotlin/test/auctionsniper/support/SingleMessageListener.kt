package test.auctionsniper.support

import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasProperty
import org.jivesoftware.smack.chat.Chat
import org.jivesoftware.smack.chat.ChatMessageListener
import org.jivesoftware.smack.packet.Message
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit

class SingleMessageListener : ChatMessageListener {

    private val messages = ArrayBlockingQueue<Message>(1)

    override fun processMessage(chat: Chat, message: Message) {
        messages.add(message)
    }

    fun receivesAMessage(messageMatcher: Matcher<in String>) {
        val message = messages.poll(5, TimeUnit.SECONDS)
        assertThat(message, hasProperty("body", messageMatcher))
    }
}
