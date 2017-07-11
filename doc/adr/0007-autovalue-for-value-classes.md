# 7. AutoValue for value classes

Date: 2017-07-13

## Status

Accepted

## Context

We want a way of generating value classes.

This will save us writing equals, hashCode and toString methods for these classes.

## Decision

We will use [AutoValue](https://github.com/google/auto/tree/master/value).

## Consequences

Developers must enable annotation processing in their IDEs.
