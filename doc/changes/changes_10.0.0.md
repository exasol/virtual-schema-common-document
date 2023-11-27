# Common Virtual Schema for Document Data 10.0.0, released 2023-??-??

Code name: Adapt to Exasol 8

## Summary

This release adds support for Exasol 8 by removing support for data type `TIMESTAMP WITH LOCAL TIME ZONE`. This type caused problems with the stricter type checks enabled by default in Exasol, causing pushdown queries for document based virtual schemas to fail with the following error:

```
Data type mismatch in column number 5 (1-indexed).Expected TIMESTAMP(3) WITH LOCAL TIME ZONE, but got TIMESTAMP(3).
```

We fixed this error by removing support `TIMESTAMP WITH LOCAL TIME ZONE` completely. This is a breaking change, so we updated the version to 10.0.0.

The release also improves logging for easier debugging:
* Log column types when creating a virtual table
* Log column types when rendering the pushdown query
* Log pushdown SQL query at log level `FINE`

The release also refactors the code to remove the dependency on Lombok.

## Features

* #178: Removed support for `TIMESTAMP WITH LOCAL TIME ZONE`
* #174: Adapted to Exasol 8 by casting to `TIMESTAMP WITH LOCAL TIME ZONE` where necessary

## Refactoring

* #170: Removed Lombok

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:edml-java:1.2.0` to `2.0.0`
* Updated `com.exasol:virtual-schema-common-java:17.0.0` to `17.0.1`

### Test Dependency Updates

* Updated `com.exasol:exasol-test-setup-abstraction-java:2.0.4` to `2.1.0`
* Updated `com.exasol:hamcrest-resultset-matcher:1.6.1` to `1.6.3`
* Updated `com.exasol:test-db-builder-java:3.5.1` to `3.5.2`
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
