package test.auctionsniper.exceptions

class ActorCannotRunAnAuctionException(actorName: String) : RuntimeException(
    "The actor $actorName does not have the ability to run an auction"
)
