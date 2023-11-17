# Common Virtual Schema for Document Data 9.4.4, released 2023-??-??

Code name: Adapt to Exasol 8

## Summary

This release adds support for Exasol 8 and improves logging for easier debugging:
* Log column types when creating a virtual table
* Log column types when rendering the pushdown query
* Log pushdown SQL query at log level `FINE`

The release also refactors the code to remove the dependency on Lombok.

## Features

* #174: Adapted to Exasol 8

## Refactoring

* #170: Removed Lombok

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:edml-java:1.2.0` to `1.2.1`

### Test Dependency Updates

* Updated `com.exasol:exasol-test-setup-abstraction-java:2.0.4` to `2.1.0`
* Updated `com.exasol:hamcrest-resultset-matcher:1.6.1` to `1.6.2`
* Added `com.jparams:to-string-verifier:1.4.8`
* Updated `commons-io:commons-io:2.14.0` to `2.15.0`
* Added `nl.jqno.equalsverifier:equalsverifier:3.15.3`
* Updated `org.jacoco:org.jacoco.agent:0.8.10` to `0.8.11`
* Updated `org.junit.jupiter:junit-jupiter-params:5.10.0` to `5.10.1`
* Updated `org.mockito:mockito-junit-jupiter:5.6.0` to `5.7.0`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:2.9.13` to `2.9.16`
* Updated `org.apache.maven.plugins:maven-dependency-plugin:3.6.0` to `3.6.1`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.1.2` to `3.2.2`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.6.0` to `3.6.2`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.1.2` to `3.2.2`
* Removed `org.projectlombok:lombok-maven-plugin:1.18.20.0`
