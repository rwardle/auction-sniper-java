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
        Thread thread = new Thread("Test Application") {
            @Override
            void run() {
                try {
                    def args = [hostname, SNIPER_ID, SNIPER_PASSWORD] + auctions.collect { it.itemId }
                    Main.main(args as String[])
                } catch (e) {
                    e.printStackTrace()
                }
            }
        }

        thread.setDaemon(true)
        thread.start()

        driver = new AuctionSniperDriver(1000)
        for (FakeAuctionServer auction : auctions) {
            driver.showsSniperStatus(auction.getItemId(), 0, 0, textFor(SniperState.JOINING))
        }
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
