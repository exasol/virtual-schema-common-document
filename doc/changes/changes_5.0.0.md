# Virtual Schema Common Document 5.0.0, released 2021-08-10

Code name: Added interface for binary data

## Summary

In this release we added an interface for accessing binary data. This was required by the DynamoDB implementation.

## Features:

* #82: Added interface for binary data

## Refactoring

* #83: Moved `TransformingIterator` from `virtual-schema-common-document-files` to this repository

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:virtual-schema-common-java:13.0.0` to `15.2.0`
* Updated `com.github.everit-org.json-schema:org.everit.json.schema:1.12.3` to `1.13.0`
* Updated `org.logicng:logicng:2.0.2` to `2.1.0`
* Updated `org.slf4j:slf4j-jdk14:1.7.31` to `1.7.32`

### Test Dependency Updates

* Updated `org.mockito:mockito-junit-jupiter:3.11.1` to `3.11.2`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:0.5.0` to `0.5.1`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.5` to `0.8.7`
