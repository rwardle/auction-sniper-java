package spec.auctionsniper.ui

import auctionsniper.AuctionSniperDriver
import auctionsniper.Item
import auctionsniper.SniperPortfolio
import auctionsniper.ui.MainWindow
import org.assertj.swing.timing.Condition
import org.assertj.swing.timing.Pause
import spock.lang.Specification

class MainWindowSpec extends Specification {

    long timeout = 1000
    MainWindow mainWindow = new MainWindow(new SniperPortfolio())
    AuctionSniperDriver driver = new AuctionSniperDriver(timeout)

    def cleanup() {
        driver.dispose()
    }

    def "makes user request when join button clicked"() {
        setup:
        def receivedValue = null
        mainWindow.addUserRequestListener({ item -> receivedValue = item })

        when:
        driver.startBiddingFor("an item-id", 789)

        then:
        Pause.pause(new Condition("join request for 'an item-id'") {
            @Override
            boolean test() {
                return receivedValue == Item.create("an item-id", 789);
            }

            @Override
            protected String descriptionAddendum() {
                return String.format(", receivedValue: '%s'", receivedValue)
            }
        }, timeout)
    }
}
