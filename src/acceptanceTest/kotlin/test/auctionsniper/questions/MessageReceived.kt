package test.auctionsniper.questions

import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.Question
import test.auctionsniper.abilities.RunAnAuction

class MessageReceived(private val questionCall: (ability: RunAnAuction) -> Unit) : Question<Boolean> {

    override fun answeredBy(actor: Actor): Boolean {
        questionCall.invoke(RunAnAuction.asActor(actor))
        return true
    }

    companion object {
        fun join(sniperId: String): MessageReceived {
            return MessageReceived { ability: RunAnAuction -> ability.hasReceivedJoinRequestFrom(sniperId) }
        }

        fun bid(bid: Int, sniperId: String): MessageReceived {
            return MessageReceived { ability: RunAnAuction -> ability.hasReceivedBid(bid, sniperId) }
        }
    }
}
