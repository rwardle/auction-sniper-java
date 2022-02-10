package test.auctionsniper.abilities

import net.serenitybdd.screenplay.Actor
import test.auctionsniper.exceptions.ActorCannotRunTheApplicationException

open class RunTheApplication(delegate: IRunTheApplication) : IRunTheApplication by delegate {

    companion object {
        fun asActor(actor: Actor): RunTheApplication {
            if (actor.abilityTo(RunTheApplication::class.java) == null) {
                throw ActorCannotRunTheApplicationException(actor.name)
            }

            return actor.abilityTo(RunTheApplication::class.java)
        }
    }
}
