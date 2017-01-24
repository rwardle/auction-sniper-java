package auctionsniper

import static auctionsniper.ui.SnipersTableModel.textFor

class ApplicationRunner {

    final String SNIPER_ID = "sniper"
    final String SNIPER_PASSWORD = "sniper"

    String hostname
    AuctionSniperDriver driver
    String itemId

    ApplicationRunner(String hostname) {
        this.hostname = hostname
    }

    void startBiddingIn(FakeAuctionServer auction) {
        itemId = auction.getItemId()

        Thread thread = new Thread("Test Application") {
            @Override
            void run() {
                try {
                    Main.main(hostname, SNIPER_ID, SNIPER_PASSWORD, auction.getItemId())
                } catch (e) {
                    e.printStackTrace()
                }
            }
        }

        thread.setDaemon(true)
        thread.start()

        driver = new AuctionSniperDriver(1000)
        driver.showsSniperStatus("", 0, 0, textFor(SniperState.JOINING))
    }

    void hasShownSniperIsBidding(int lastPrice, int lastBid) {
        driver.showsSniperStatus(itemId, lastPrice, lastBid, textFor(SniperState.BIDDING))
    }

    void hasShownSniperIsWinning(int winningBid) {
        driver.showsSniperStatus(itemId, winningBid, winningBid, textFor(SniperState.WINNING))
    }

    void showsSniperHasLostAuction(int lastPrice, int lastBid) {
        driver.showsSniperStatus(itemId, lastPrice, lastBid, textFor(SniperState.LOST))
    }

    void showsSniperHasWonAuction(int lastPrice) {
        driver.showsSniperStatus(itemId, lastPrice, lastPrice, textFor(SniperState.WON))
    }

    void stop() {
        driver?.dispose()
    }
}
