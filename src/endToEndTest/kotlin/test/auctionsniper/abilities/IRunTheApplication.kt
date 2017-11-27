package test.auctionsniper.abilities

import net.serenitybdd.screenplay.Ability

interface IRunTheApplication : Ability {

    fun startBiddingOnItems(vararg itemIds: String)
    fun startBiddingWithStopPrice(itemId: String, stopPrice: Int)
    fun showsSniperHasLostItem(itemId: String, lastPrice: Int, lastBid: Int)
    fun hasShownSniperIsBidding(itemId: String, lastPrice: Int, lastBid: Int)
    fun hasShownSniperIsWinning(itemId: String, winningBid: Int)
    fun showsSniperHasWonAuction(itemId: String, lastPrice: Int)
    fun hasShownSniperIsLosing(itemId: String, lastPrice: Int, lastBid: Int)
    fun showsSniperHasFailed(itemId: String)
    fun reportsInvalidMessage(message: String)
    fun stop()
}
