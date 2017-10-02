package test.endtoend.auctionsniper.tasks

import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.Task
import test.endtoend.auctionsniper.abilities.RunAnAuction

open class AnnounceClosed : Task {

    override fun <T : Actor> performAs(actor: T) {
        RunAnAuction.asActor(actor).announceClosed()
    }
}
