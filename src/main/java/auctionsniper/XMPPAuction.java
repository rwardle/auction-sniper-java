package auctionsniper;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;

import static auctionsniper.AppConstants.BID_COMMAND_FORMAT;
import static auctionsniper.AppConstants.JOIN_COMMAND_FORMAT;
import static java.lang.String.format;

public class XMPPAuction implements Auction {

    private final Chat chat;

    public XMPPAuction(Chat chat) {
        this.chat = chat;
    }

    @Override
    public void bid(int amount) {
        try {
            chat.sendMessage(format(BID_COMMAND_FORMAT, amount));
        } catch (SmackException.NotConnectedException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void join() {
        try {
            chat.sendMessage(JOIN_COMMAND_FORMAT);
        } catch (SmackException.NotConnectedException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
