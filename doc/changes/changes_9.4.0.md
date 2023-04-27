# Common Virtual Schema for Document Data 9.4.0, released 2023-04-27

Code name: Detect additional configuration

## Summary

This release allows virtual schemas using VSD as basis to define `additionalConfiguration`, `description` and `destinationTable` in addition to `mapping` during schema auto-inference. This will e.g. allow the auto-inference for CSV files to detect if a CSV file contains a header or not.

To migrate a virtual schema to this new version you need to update your implementation of `SchemaFetcher` to return `Optional<InferredMappingDefinition>` instead of `Optional<MappingDefinition>` in method `fetchSchema()`.

This release also improves the wording of error code [F-VSD-5](https://exasol.github.io/error-catalog/error-codes/f-vsd-5.html) to better explain the background and mitigation.

## Features

* #159: Improved LIKE error message
* #161: Allowed virtual schemas to define more properties during auto-inference

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:sql-statement-builder:4.5.2` to `4.5.3`
* Removed `org.slf4j:slf4j-jdk14:2.0.6`

### Test Dependency Updates

* Updated `com.exasol:exasol-test-setup-abstraction-java:2.0.0` to `2.0.1`
* Removed `com.exasol:exasol-testcontainers:6.5.1`
* Updated `com.exasol:hamcrest-resultset-matcher:1.5.2` to `1.6.0`
* Updated `org.jacoco:org.jacoco.agent:0.8.8` to `0.8.9`
* Removed `org.junit.jupiter:junit-jupiter-engine:5.9.2`
* Updated `org.junit.jupiter:junit-jupiter-params:5.9.2` to `5.9.3`
* Updated `org.mockito:mockito-junit-jupiter:5.2.0` to `5.3.1`
* Added `org.slf4j:slf4j-jdk14:2.0.7`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:1.2.2` to `1.2.3`
* Updated `com.exasol:project-keeper-maven-plugin:2.9.3` to `2.9.7`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.10.1` to `3.11.0`
* Updated `org.apache.maven.plugins:maven-deploy-plugin:3.0.0` to `3.1.1`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.1.0` to `3.3.0`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.0.0-M8` to `3.0.0`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.4.1` to `3.5.0`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.0.0-M8` to `3.0.0`
* Added `org.basepom.maven:duplicate-finder-maven-plugin:1.5.1`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.3.0` to `1.4.1`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.14.2` to `2.15.0`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.8` to `0.8.9`
