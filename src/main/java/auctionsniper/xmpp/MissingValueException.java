package auctionsniper.xmpp;

class MissingValueException extends Exception {

    MissingValueException(String fieldName) {
        super(fieldName);
    }
}
