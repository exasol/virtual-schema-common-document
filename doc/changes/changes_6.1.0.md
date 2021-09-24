# Common Virtual Schema for document data 6.1.0, released 2021-XX-XX

Code name: New Mapping Types

## Summary

In this release we added the following new mapping types:

* `toDoubleMapping`
* `toBoolMapping`
* `toDateMapping`
* `toTimestampMapping`

In order to use the new features, please update you EDML definitions to version `1.3.0` (no breaking changes).

## Features

* #88: Added Java representation for the EDML
* #93: Added support for inline mapping definitions
* #95: Added integration tests
* #57: Added `toDoubleMapping` mapping type
* #104: Added `toBoolMapping` mapping type
* #106: Added support for date, time and timestamp input values
* #58: Added `toDate` and `toTimestamp` mapping types

## Refactoring

* #91: Refactored to use Java representation for EDML for reading schema mapping
* #98: Refactored propertyToXxxReaderClasses to use Lombok

# Bug Fixes

* #101: Fix integration test setup

## Dependency Updates

### Compile Dependency Updates

* Added `com.fasterxml.jackson.core:jackson-databind:2.11.1`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:0.5.1` to `0.6.0`
* Added `org.projectlombok:lombok-maven-plugin:1.18.20.0`
