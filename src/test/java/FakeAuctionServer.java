import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class FakeAuctionServer {

    public static final String HOSTNAME = "localhost";

    private final SingleMessageListener messageListener = new SingleMessageListener();
    private final String itemId;
    private final XMPPConnection connection;
    private Chat currentChat;

    public FakeAuctionServer(String itemId) {
        this.itemId = itemId;
        this.connection = new XMPPConnection(HOSTNAME);
    }

    public void startSellingItem() {
        connection.connect();
        connection.login();
        connection.getChatManager().addChatListener(
                new ChatManagerListener() {
                    public void chatCreated(Chat chat, boolean createdLocally) {
                        currentChat = chat;
                        chat.addMessageListener();
                    }
                });
    }

    public void hasReceivedJoinRequestFromSniper() throws Exception {
        messageListener.receivesAMessage();
    }

    public void announceClosed() {
        currentChat.sendMessage(new Message());
    }

    public void stop() {
        connection.disconnect();
    }

    public String getItemId() {
        return itemId;
    }

    private static class XMPPConnection {
        public XMPPConnection(String hostname) {
        }

        public void connect() {

        }

        public void login() {

        }

        public ChatManager getChatManager() {
            return new ChatManager();
        }

        public void disconnect() {

        }
    }

    private static class ChatManager {
        public void addChatListener(ChatManagerListener chatManagerListener) {
        }
    }

    private static class ChatManagerListener {
    }

    private static class Chat {
        public void addMessageListener() {

        }

        public void sendMessage(Message message) {

        }
    }

    private static class SingleMessageListener {

        private final ArrayBlockingQueue<Message> messages = new ArrayBlockingQueue<>(1);

        public void receivesAMessage() throws InterruptedException {
            assertThat("Message", messages.poll(5, TimeUnit.SECONDS), is(notNullValue()));
        }
    }

    private static class Message {
    }
}
