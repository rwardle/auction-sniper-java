package test.auctionsniper.tasks

import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.Task
import test.auctionsniper.abilities.RunTheApplication

open class StartBidding(private val work: (ability: RunTheApplication) -> Unit) : Task {

    override fun <T : Actor> performAs(actor: T) {
        work.invoke(RunTheApplication.asActor(actor))
    }

    companion object {
        fun onItems(vararg itemIds: String): StartBidding =
            StartBidding({ ability: RunTheApplication -> ability.startBiddingOnItems(*itemIds) })

        fun withStopPrice(itemId: String, stopPrice: Int): StartBidding =
            StartBidding({ ability: RunTheApplication -> ability.startBiddingWithStopPrice(itemId, stopPrice) })
    }
}
