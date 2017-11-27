package test.auctionsniper.support

import net.serenitybdd.screenplay.Ability
import test.auctionsniper.abilities.RunAnAuction
import test.auctionsniper.abilities.RunTheApplication
import test.auctionsniper.abilities.RunTheDomainApplication
import test.auctionsniper.abilities.RunTheSwingApplication

class Production {

    companion object {
        private val productionType = System.getenv(Constants.PRODUCTION_TYPE_ENV) ?: "swing"
        private val hostname = System.getenv(Constants.XMPP_HOSTNAME_ENV) ?: "localhost"

        fun newScenario(): Scenario {
            val auctionAbilityFactory = { listOf<Ability>(RunAnAuction.withServer(hostname)) }

            val sniperAbilityFactory: () -> List<Ability> = when (productionType) {
                "swing" -> {
                    { listOf(RunTheApplication(RunTheSwingApplication.withServer(hostname))) }
                }
                "domain" -> {
                    { listOf(RunTheApplication(RunTheDomainApplication.withServer(hostname))) }
                }
                else -> throw IllegalArgumentException("Unknown production type $productionType")
            }

            return Scenario(auctionAbilityFactory, sniperAbilityFactory)
        }
    }
}
