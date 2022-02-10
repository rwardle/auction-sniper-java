package test.auctionsniper.support

import net.serenitybdd.screenplay.Ability
import net.serenitybdd.screenplay.Actor

class Scenario(
    private val auctionAbilityFactory: () -> List<Ability>,
    private val sniperAbilityFactory: () -> List<Ability>
) {

    fun newAuction(name: String): Actor {
        val auction = Actor.named(name)
        auctionAbilityFactory.invoke().map { ability -> auction.can(ability) }
        return auction
    }

    fun newSniper(name: String): Actor {
        val sniper = Actor.named(name)
        sniperAbilityFactory.invoke().map { ability -> sniper.can(ability) }
        return sniper
    }
}
