package test.auctionsniper.questions

import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.Question
import test.auctionsniper.abilities.RunTheApplication

class SniperStatus(private val questionCall: (ability: RunTheApplication) -> Unit) : Question<Boolean> {

    override fun answeredBy(actor: Actor): Boolean {
        questionCall.invoke(RunTheApplication.asActor(actor))
        return true
    }

    companion object {
        fun lost(itemId: String, lastPrice: Int, lastBid: Int): SniperStatus {
            return SniperStatus(
                    { ability: RunTheApplication -> ability.showsSniperHasLostItem(itemId, lastPrice, lastBid) }
            )
        }

        fun bidding(itemId: String, lastPrice: Int, lastBid: Int): SniperStatus {
            return SniperStatus(
                    { ability: RunTheApplication -> ability.hasShownSniperIsBidding(itemId, lastPrice, lastBid) }
            )
        }

        fun winning(itemId: String, winningBid: Int): SniperStatus {
            return SniperStatus(
                    { ability: RunTheApplication -> ability.hasShownSniperIsWinning(itemId, winningBid) }
            )
        }

        fun won(itemId: String, lastPrice: Int): SniperStatus {
            return SniperStatus(
                    { ability: RunTheApplication -> ability.showsSniperHasWonAuction(itemId, lastPrice) }
            )
        }

        fun losing(itemId: String, lastPrice: Int, lastBid: Int): SniperStatus {
            return SniperStatus(
                    { ability: RunTheApplication -> ability.hasShownSniperIsLosing(itemId, lastPrice, lastBid) }
            )
        }

        fun failed(itemId: String): SniperStatus {
            return SniperStatus(
                    { ability: RunTheApplication -> ability.showsSniperHasFailed(itemId) }
            )
        }
    }
}
