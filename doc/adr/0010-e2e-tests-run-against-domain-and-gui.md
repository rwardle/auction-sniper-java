# 10. e2e tests run against domain and gui

Date: 2017-11-28

## Status

Accepted

## Context

We want to run the same set of end-to-end tests at different layers of the 
system e.g. against the domain model or via the GUI.

## Decision

We will extend the Screenplay pattern so that the abilities given to the actors 
(e.g. run a Swing application, browse the web, etc) can be configured at runtime.

## Consequences

Developers must ensure that the end-to-end tests run successfully against all 
layers of the system that are being targeted.
