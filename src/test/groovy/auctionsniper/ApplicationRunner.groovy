package auctionsniper

import static auctionsniper.AppConstants.*

class ApplicationRunner {

    final String SNIPER_ID = "sniper"
    final String SNIPER_PASSWORD = "sniper"

    String hostname
    AuctionSniperDriver driver

    ApplicationRunner(String hostname) {
        this.hostname = hostname
    }

    void startBiddingIn(FakeAuctionServer auction) {
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
        driver.showsSniperStatus(STATUS_JOINING)
    }

    void hasShownSniperIsBidding() {
        driver.showsSniperStatus(STATUS_BIDDING)
    }

    void showsSniperHasLostAuction() {
        driver.showsSniperStatus(STATUS_LOST)
    }

    void stop() {
        driver?.dispose()
    }
}
