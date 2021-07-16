# Virtual Schema Common Document 4.0.0, released 2021-07-16

Code name: Performance Optimizations

## Summary

In this release we refactored the interface to improve performance and make implementing new dialects easier.

## Features:

* #69: Added document node factory that wraps java objects

## Bug Fixes:

* #72: Added parallel UDFs count calculation using memory limitation

## Refactoring

* #61: Refactored DocumentNode structure to be dialect independent
* #63: Removed DataLoader interface
* #65: Decoupled loading and emitting to improve performance
* #75: Refactored to scope Akka to this project
* #77: Removed Akka since it did not lead to the expected performance gain
* #79: Update error-code-crawler

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:error-reporting-java:0.2.0` to `0.4.0`
* Updated `com.exasol:sql-statement-builder:4.3.0` to `4.4.1`
* Updated `com.github.everit-org.json-schema:org.everit.json.schema:1.12.1` to `1.12.3`
* Updated `org.slf4j:slf4j-jdk14:1.7.30` to `1.7.31`

### Test Dependency Updates

* Updated `org.junit.jupiter:junit-jupiter-engine:5.7.0` to `5.7.2`
* Updated `org.junit.jupiter:junit-jupiter-params:5.7.0` to `5.7.2`
* Removed `org.mockito:mockito-core:3.6.0`
* Added `org.mockito:mockito-junit-jupiter:3.11.1`

### Plugin Dependency Updates

* Added `com.exasol:error-code-crawler-maven-plugin:0.5.0`
* Updated `com.exasol:project-keeper-maven-plugin:0.3.0` to `0.10.0`
* Added `io.github.zlika:reproducible-build-maven-plugin:0.13`
* Removed `org.apache.maven.plugins:maven-assembly-plugin:3.3.0`
