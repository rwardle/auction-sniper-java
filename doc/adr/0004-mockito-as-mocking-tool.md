# 4. Mockito as mocking tool

Date: 2017-07-13

## Status

Accepted

## Context

We want to use a mocking library to help with TDD.

## Decision

We will use [Mockito](http://site.mockito.org/).

## Consequences

Developers should be aware that Mockito uses a "loose" mocking style by default that differs from classic mocking frameworks e.g. EasyMock, JMock.
