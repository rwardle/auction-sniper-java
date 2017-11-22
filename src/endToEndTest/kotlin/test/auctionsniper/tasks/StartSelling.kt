package test.auctionsniper.tasks

import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.Task
import test.auctionsniper.abilities.RunAnAuction

open class StartSelling(private val itemId: String) : Task {

    override fun <T : Actor> performAs(actor: T) {
        RunAnAuction.asActor(actor).startSellingItem(itemId)
    }

    companion object {
        fun item(itemId: String): StartSelling = StartSelling(itemId)
    }
}
