package spec.auctionsniper.ui

import auctionsniper.AuctionSniperDriver
import auctionsniper.ui.MainWindow
import auctionsniper.ui.SnipersTableModel
import org.assertj.swing.timing.Condition
import org.assertj.swing.timing.Pause
import spock.lang.Specification

class MainWindowSpec extends Specification {

    long timeout = 1000
    SnipersTableModel tableModel = new SnipersTableModel()
    MainWindow mainWindow = new MainWindow(tableModel)
    AuctionSniperDriver driver = new AuctionSniperDriver(timeout)

    def cleanup() {
        driver.dispose()
    }

    def "makes user request when join button clicked"() {
        setup:
        def receivedValue = null
        mainWindow.addUserRequestListener({ itemId -> receivedValue = itemId })

        when:
        driver.startBiddingFor("an item-id")

        then:
        Pause.pause(new Condition("join request for 'an item-id'") {
            @Override
            boolean test() {
                return receivedValue == "an item-id"
            }

            @Override
            protected String descriptionAddendum() {
                return String.format(", receivedValue: '%s'", receivedValue)
            }
        }, timeout)
    }
}
