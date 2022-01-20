# Common Virtual Schema for document data 8.0.0, released 2022-01-20

Code name: Unified connection definition

## Summary:

This release added common implementation parts for switching to unified connection definition specified in: https://github.com/exasol/connection-parameter-specification/.

**This release has breaking API changes:**

* The dialects must now implement `UdfEntryPoint` and call `GenericUdfCallHandler`.
* The dialect Java API changed

## Features

* #123: Support unified connection definition

## Dependency Updates

### Test Dependency Updates

* Updated `com.exasol:exasol-test-setup-abstraction-java:0.2.1` to `0.2.2`
* Updated `com.exasol:test-db-builder-java:3.2.1` to `3.2.2`
* Updated `com.exasol:udf-debugging-java:0.4.1` to `0.5.0`
* Added `commons-io:commons-io:2.11.0`
