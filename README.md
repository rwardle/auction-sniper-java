# Auction Sniper

The worked example from
[Growing Object-Oriented Software Guided by Tests](http://www.growing-object-oriented-software.com)
implemented in Java with acceptance tests written in Kotlin.

## Prerequisites

- JDK 8.
- Docker.
- Docker Compose.

## Build

This project configures a local build environment based on Docker using [Batect](https://batect.dev). \
Run `./batect --list-tasks` for a list of all tasks in the project.

## Documentation

[Architecture Decision Records](doc/adr) capture the important architectural decisions made in the project
along with their context and consequences.

## Acceptance tests

Acceptance tests are written as BDD scenarios using
[Serenity and the Screenplay pattern](http://serenity-bdd.info/docs/articles/screenplay-tutorial.html).
They can be run end-to-end against the GUI or can be run directly against the domain model.

## Openfire

Communication between the participants in the auction is via an
[Openfire](http://www.igniterealtime.org/projects/openfire) server.
Docker compose configuration is provided for running an
[Openfire docker image](https://github.com/rwardle/openfire-goos).

To start the Openfire server run: `docker-compose up`.

## Run

To start the application run: `./gradlew run --args="localhost sniper sniper"`.

This command starts the application and connects to an Openfire server running on `localhost`,
connecting as the `sniper` user preconfigured in the Openfire Docker image.
