# Common Virtual Schema for document data 6.1.0, released 2021-XX-XX

Code name: New Mapping Types

## Summary

In this release we added the following new mapping types:

* `toDoubleMapping`

## Features

* #88: Added Java representation for the EDML
* #93: Added support for inline mapping definitions
* #95: Added integration tests
* #57: Added `toDoubleMapping` mapping type

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
