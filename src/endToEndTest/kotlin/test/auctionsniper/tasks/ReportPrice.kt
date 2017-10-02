package test.endtoend.auctionsniper.tasks

import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.Task
import test.endtoend.auctionsniper.abilities.RunAnAuction

open class ReportPrice(private val price: Int, private val increment: Int, private val bidder: String) : Task {

    override fun <T : Actor> performAs(actor: T) {
        RunAnAuction.asActor(actor).reportPrice(price, increment, bidder)
    }
}
