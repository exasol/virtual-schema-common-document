# Common Virtual Schema for document data 6.1.0, released 2021-09-28

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
* #96: Add integration test for inline mapping definitions

## Refactoring

* #91: Refactored to use Java representation for EDML for reading schema mapping
* #98: Refactored propertyToXxxReaderClasses to use Lombok
* #110: Replaced SQL statement builder literals by native data types
* #112: Refactored PropertyToVarcharColumnValueExtractor

## Bug Fixes

* #101: Fix integration test setup

## Documentation

* #103: Documented data type conversions

## Dependency Updates

### Compile Dependency Updates

* Added `com.exasol:maven-project-version-getter:1.0.0`
* Added `com.fasterxml.jackson.core:jackson-databind:2.12.5`

### Test Dependency Updates

* Added `com.exasol:exasol-test-setup-abstraction-java:0.2.0`
* Added `com.exasol:hamcrest-resultset-matcher:1.5.0`
* Added `com.exasol:test-db-builder-java:3.2.1`
* Added `com.exasol:udf-debugging-java:0.4.0`
* Added `junit:junit:4.13.2`
* Added `org.apache.maven.shared:maven-verifier:1.7.2`
* Added `org.jacoco:org.jacoco.agent:0.8.7`
* Updated `org.junit.jupiter:junit-jupiter-engine:5.7.2` to `5.8.1`
* Updated `org.junit.jupiter:junit-jupiter-params:5.7.2` to `5.8.1`
* Updated `org.mockito:mockito-junit-jupiter:3.11.2` to `3.12.4`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:0.5.1` to `0.6.0`
* Updated `com.exasol:project-keeper-maven-plugin:0.10.0` to `1.2.0`
* Added `org.projectlombok:lombok-maven-plugin:1.18.20.0`
