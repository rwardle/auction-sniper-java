package test.auctionsniper.questions

import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.Question
import test.auctionsniper.abilities.RunTheApplication

class ReportsInvalidMessage(private val message: String) : Question<Boolean> {

    override fun answeredBy(actor: Actor): Boolean {
        RunTheApplication.asActor(actor).reportsInvalidMessage(message)
        return true
    }
}
