package test.auctionsniper.abilities

import net.serenitybdd.screenplay.Ability
import net.serenitybdd.screenplay.Actor
import test.auctionsniper.exceptions.ActorCannotRunAnAuctionException
import test.auctionsniper.support.FakeAuctionServer

open class RunAnAuction(hostname: String) : Ability {

    private val auctionServer = FakeAuctionServer(hostname)

    companion object {
        fun withServer(hostname: String): RunAnAuction = RunAnAuction(hostname)

        fun asActor(actor: Actor): RunAnAuction {
            if (actor.abilityTo(RunAnAuction::class.java) == null) {
                throw ActorCannotRunAnAuctionException(actor.name)
            }

            return actor.abilityTo(RunAnAuction::class.java)
        }
    }

    fun stop() {
        auctionServer.stop()
    }

    fun startSellingItem(itemId: String) {
        auctionServer.startSellingItem(itemId)
    }

    fun hasReceivedJoinRequestFrom(sniperId: String) {
        auctionServer.hasReceivedJoinRequestFrom(sniperId)
    }

    fun hasReceivedBid(bid: Int, sniperId: String) {
        auctionServer.hasReceivedBid(bid, sniperId)
    }

    fun announceClosed() {
        auctionServer.announceClosed()
    }

    fun reportPrice(price: Int, increment: Int, bidder: String) {
        auctionServer.reportPrice(price, increment, bidder)
    }

    fun itemId(): String = auctionServer.itemId()

    fun sendInvalidMessageContaining(message: String) {
        auctionServer.sendInvalidMessageContaining(message)
    }
}
