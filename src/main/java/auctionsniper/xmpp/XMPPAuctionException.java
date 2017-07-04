package auctionsniper.xmpp;

class XMPPAuctionException extends Exception {

    XMPPAuctionException(String message, Exception ex) {
        super(message, ex);
    }
}
