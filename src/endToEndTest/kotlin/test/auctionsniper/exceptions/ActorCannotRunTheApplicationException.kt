package test.auctionsniper.exceptions

class ActorCannotRunTheApplicationException(actorName: String) : RuntimeException(
        "The actor $actorName does not have the ability to run the application")
