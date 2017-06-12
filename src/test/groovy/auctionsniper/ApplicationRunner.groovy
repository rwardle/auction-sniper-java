package auctionsniper

import static auctionsniper.ui.SnipersTableModel.textFor

class ApplicationRunner {

    final String SNIPER_ID = "sniper"
    final String SNIPER_PASSWORD = "sniper"

    String hostname
    AuctionSniperDriver driver

    ApplicationRunner(String hostname) {
        this.hostname = hostname
    }

    void startBiddingIn(FakeAuctionServer... auctions) {
        startSniper()
        for (FakeAuctionServer auction : auctions) {
            def itemId = auction.getItemId()
            driver.startBiddingFor(itemId)
            driver.showsSniperStatus(itemId, 0, 0, textFor(SniperState.JOINING))
        }
    }

    private void startSniper() {
        Thread thread = new Thread("Test Application") {
            @Override
            void run() {
                try {
                    def args = [hostname, SNIPER_ID, SNIPER_PASSWORD]
                    Main.main(args as String[])
                } catch (e) {
                    e.printStackTrace()
                }
            }
        }

        thread.setDaemon(true)
        thread.start()

        driver = new AuctionSniperDriver(1000)
    }

    void hasShownSniperIsBidding(FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, textFor(SniperState.BIDDING))
    }

    void hasShownSniperIsWinning(FakeAuctionServer auction, int winningBid) {
        driver.showsSniperStatus(auction.getItemId(), winningBid, winningBid, textFor(SniperState.WINNING))
    }

    void showsSniperHasLostAuction(FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, textFor(SniperState.LOST))
    }

    void showsSniperHasWonAuction(FakeAuctionServer auction, int lastPrice) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastPrice, textFor(SniperState.WON))
    }

    void stop() {
        driver?.dispose()
    }
}
