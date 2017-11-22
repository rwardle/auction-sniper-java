package test.auctionsniper.support

import auctionsniper.xmpp.XMPPAuctionHouse.LOG_FILE_NAME
import org.apache.commons.io.FileUtils
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import java.io.File
import java.nio.charset.Charset
import java.util.logging.LogManager

class AuctionLogDriver {

    private val logFile = File(LOG_FILE_NAME)

    fun clearLog() {
        logFile.delete()
        LogManager.getLogManager().reset()
    }

    fun hasEntry(matcher: Matcher<String>) {
        assertThat(FileUtils.readFileToString(logFile, Charset.defaultCharset()), matcher)
    }
}
