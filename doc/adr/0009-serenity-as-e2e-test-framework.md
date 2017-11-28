# 9. Serenity as e2e test framework

Date: 2017-11-28

## Status

Accepted

Supercedes [5. Spock as e2e test tool](0005-spock-as-e2e-test-tool.md)

## Context

We want to write end-to-end acceptance tests for the project using the 
[Screenplay](http://serenity-bdd.info/docs/articles/screenplay-tutorial.html) pattern. 

## Decision

We will use [Serenity](http://serenity-bdd.info/) and run the tests using [JUnit](http://junit.org/).

## Consequences

End-to-end tests will be written in [Kotlin](https://kotlinlang.org/) which will 
provide better type-safety and IDE integration than Groovy.
