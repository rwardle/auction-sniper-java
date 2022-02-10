package test.auctionsniper.tasks

import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.Task
import test.auctionsniper.abilities.RunAnAuction

open class SendInvalidMessage(private val message: String) : Task {

    override fun <T : Actor> performAs(actor: T) {
        RunAnAuction.asActor(actor).sendInvalidMessageContaining(message)
    }

    companion object {
        fun containing(message: String): SendInvalidMessage = SendInvalidMessage(message)
    }
}
