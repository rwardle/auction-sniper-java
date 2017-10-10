package test.auctionsniper.features

import net.serenitybdd.junit.runners.SerenityRunner
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.GivenWhenThen.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import test.auctionsniper.abilities.RunAnAuction
import test.auctionsniper.abilities.RunTheApplication
import test.auctionsniper.questions.MessageReceived
import test.auctionsniper.questions.ReportsInvalidMessage
import test.auctionsniper.questions.SniperStatus
import test.auctionsniper.support.Constants.SNIPER_XMPP_ID
import test.auctionsniper.support.Constants.XMPP_HOSTNAME_ENV
import test.auctionsniper.tasks.*

@RunWith(SerenityRunner::class)
class AuctionSniperStory {

    private val itemId = "item-54321"
    private val itemId2 = "item-65432"

    private val sniper = Actor.named("Sniper")
    private val auction = Actor.named("Auction")
    private val auction2 = Actor.named("Auction2")

    @Before
    fun setUp() {
        val hostname = System.getenv(XMPP_HOSTNAME_ENV) ?: "localhost"

        auction.can(RunAnAuction.withServer(hostname))
        auction2.can(RunAnAuction.withServer(hostname))
        sniper.can(RunTheApplication.withServer(hostname))
    }

    @After
    fun tearDown() {
        RunAnAuction.asActor(auction).stop()
        RunAnAuction.asActor(auction2).stop()
        RunTheApplication.asActor(sniper).stop()
    }

    @Test
    fun `sniper joins auction until auction closes`() {
        givenThat(auction).wasAbleTo(StartSelling.item(itemId))
        andThat(sniper).wasAbleTo(StartBidding.onItems(itemId))
        then(auction).should(seeThat<Boolean>(MessageReceived.join(SNIPER_XMPP_ID)))

        `when`(auction).attemptsTo(AnnounceClosed())
        then(sniper).should(seeThat<Boolean>(SniperStatus.lost(itemId, 0, 0)))
    }

    @Test
    fun `sniper makes a higher bid but loses`() {
        givenThat(auction).wasAbleTo(StartSelling.item(itemId))
        andThat(sniper).wasAbleTo(StartBidding.onItems(itemId))
        then(auction).should(seeThat<Boolean>(MessageReceived.join(SNIPER_XMPP_ID)))

        sniperReceivesPriceAndBids(auction, 1000, 98, "other bidder")

        `when`(auction).attemptsTo(AnnounceClosed())
        then(sniper).should(seeThat<Boolean>(SniperStatus.lost(itemId, 1000, 1098)))
    }

    @Test
    fun `sniper wins an auction by bidding higher`() {
        givenThat(auction).wasAbleTo(StartSelling.item(itemId))
        andThat(sniper).wasAbleTo(StartBidding.onItems(itemId))
        then(auction).should(seeThat<Boolean>(MessageReceived.join(SNIPER_XMPP_ID)))

        sniperReceivesPriceAndBids(auction, 1000, 98, "other bidder")

        `when`(auction).attemptsTo(ReportPrice(1098, 97, SNIPER_XMPP_ID))
        then(sniper).should(seeThat<Boolean>(SniperStatus.winning(itemId, 1098)))

        `when`(auction).attemptsTo(AnnounceClosed())
        then(sniper).should(seeThat<Boolean>(SniperStatus.won(itemId, 1098)))
    }

    @Test
    fun `sniper bids for multiple items`() {
        givenThat(auction).wasAbleTo(StartSelling.item(itemId))
        andThat(auction2).wasAbleTo(StartSelling.item(itemId2))
        andThat(sniper).wasAbleTo(StartBidding.onItems(itemId, itemId2))
        then(auction).should(seeThat<Boolean>(MessageReceived.join(SNIPER_XMPP_ID)))
        and(auction2).should(seeThat<Boolean>(MessageReceived.join(SNIPER_XMPP_ID)))

        sniperReceivesPriceAndBids(auction, 1000, 98, "other bidder")
        sniperReceivesPriceAndBids(auction2, 500, 21, "other bidder")

        `when`(auction).attemptsTo(ReportPrice(1098, 97, SNIPER_XMPP_ID))
        and(auction2).attemptsTo(ReportPrice(521, 20, SNIPER_XMPP_ID))

        then(sniper).should(seeThat<Boolean>(SniperStatus.winning(itemId, 1098)))
        and(sniper).should(seeThat<Boolean>(SniperStatus.winning(itemId2, 521)))

        `when`(auction).attemptsTo(AnnounceClosed())
        and(auction2).attemptsTo(AnnounceClosed())

        then(sniper).should(seeThat<Boolean>(SniperStatus.won(itemId, 1098)))
        and(sniper).should(seeThat<Boolean>(SniperStatus.won(itemId2, 521)))
    }

    @Test
    fun `sniper loses an auction when the price is too high`() {
        givenThat(auction).wasAbleTo(StartSelling.item(itemId))
        andThat(sniper).wasAbleTo(StartBidding.withStopPrice(itemId, 1100))
        then(auction).should(seeThat<Boolean>(MessageReceived.join(SNIPER_XMPP_ID)))

        sniperReceivesPriceAndBids(auction, 1000, 98, "other bidder")

        `when`(auction).attemptsTo(ReportPrice(1197, 10, "third party"))
        then(sniper).should(seeThat<Boolean>(SniperStatus.losing(itemId, 1197, 1098)))

        `when`(auction).attemptsTo(ReportPrice(1207, 10, "fourth party"))
        then(sniper).should(seeThat<Boolean>(SniperStatus.losing(itemId, 1207, 1098)))

        `when`(auction).attemptsTo(AnnounceClosed())
        then(sniper).should(seeThat<Boolean>(SniperStatus.lost(itemId, 1207, 1098)))
    }

    @Test
    fun `sniper reports invalid auction message and stop responding to events`() {
        givenThat(auction).wasAbleTo(StartSelling.item(itemId))
        andThat(auction2).wasAbleTo(StartSelling.item(itemId2))
        andThat(sniper).wasAbleTo(StartBidding.onItems(itemId, itemId2))
        then(auction).should(seeThat<Boolean>(MessageReceived.join(SNIPER_XMPP_ID)))

        sniperReceivesPriceAndBids(auction, 500, 20, "other bidder")

        val brokenMessage = "a broken message"
        `when`(auction).attemptsTo(SendInvalidMessage.containing(brokenMessage))
        then(sniper).should(seeThat<Boolean>(SniperStatus.failed(itemId)))

        `when`(auction).attemptsTo(ReportPrice(520, 21, "other bidder"))
        waitForAnotherAuctionEvent()
        then(sniper).should(seeThat<Boolean>(ReportsInvalidMessage(brokenMessage)))
        and(sniper).should(seeThat<Boolean>(SniperStatus.failed(itemId)))
    }

    private fun sniperReceivesPriceAndBids(auctionActor: Actor, price: Int, increment: Int, bidder: String) {
        `when`(auctionActor).attemptsTo(ReportPrice(price, increment, bidder))

        val bid = price + increment
        then(sniper).should(seeThat<Boolean>(
            SniperStatus.bidding(RunAnAuction.asActor(auctionActor).itemId(), price, bid)))
        and(auctionActor).should(seeThat<Boolean>(MessageReceived.bid(bid, SNIPER_XMPP_ID)))
    }

    private fun waitForAnotherAuctionEvent() {
        then(auction2).should(seeThat<Boolean>(MessageReceived.join(SNIPER_XMPP_ID)))

        `when`(auction2).attemptsTo(ReportPrice(600, 6, "other bidder"))
        then(sniper).should(seeThat<Boolean>(SniperStatus.bidding(itemId2, 600, 606)))
    }
}
