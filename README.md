# Auction Sniper

The worked example from [Growing Object-Oriented Software Guided by Tests][goos]
implemented in Java with acceptance tests written in Kotlin.

[goos]: http://www.growing-object-oriented-software.com

## Prerequisites

- JDK 8.
- Docker.
- Docker Compose.

## Build

This project configures a local build environment based on Docker using [Batect][].

To see a list of all tasks in the project run: `./batect --list-tasks`.

[batect]: https://batect.dev

## Documentation

[Architecture Decision Records](doc/adr) capture the important architectural
decisions made in the project along with their context and consequences.

## Acceptance tests

Acceptance tests are written as BDD scenarios using [Serenity and the Screenplay pattern][serenity].
They can be run end-to-end against the GUI or can be run directly against the domain model.

[serenity]: http://serenity-bdd.info/docs/articles/screenplay-tutorial.html

## Openfire

Communication between the participants in the auction is via an [Openfire][] server.
A [Docker Compose file][dc_file] is provided for running an [Openfire docker image][of_image].

To start the Openfire server run: `docker-compose up`.

[openfire]: http://www.igniterealtime.org/projects/openfire
[dc_file]: docker-compose.yml
[of_image]: https://github.com/rwardle/openfire-goos

## Run

To start the application run: `./gradlew run --args="localhost sniper sniper"`.

This command starts the application and connects to an Openfire server running on `localhost`,
connecting as the `sniper` user preconfigured in the Openfire Docker image.
