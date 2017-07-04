package auctionsniper

import org.apache.commons.io.FileUtils
import org.hamcrest.Matcher

import java.nio.charset.Charset
import java.util.logging.LogManager

import static auctionsniper.xmpp.XMPPAuctionHouse.LOG_FILE_NAME
import static org.hamcrest.MatcherAssert.assertThat

class AuctionLogDriver {

    File logFile = new File(LOG_FILE_NAME)

    def clearLog() {
        logFile.delete()
        LogManager.getLogManager().reset()
    }

    void hasEntry(Matcher<String> matcher) {
        assertThat(FileUtils.readFileToString(logFile, Charset.defaultCharset()), matcher)
    }
}
