# Common Virtual Schema for Document Data 12.0.0, released 2026-04-21

Code name: Anonymous feature tracking

## Summary

This release adds anonymous feature tracking using the [telemetry-java](https://github.com/exasol/telemetry-java) library. When you integrate this new version into another product, please observe the [required user documentation](https://github.com/exasol/telemetry-java/blob/main/doc/integration-guide.md#required-documentation).

## Breaking Changes

See breaking changes in [virtual-schema-common-java 18.0.0](https://github.com/exasol/virtual-schema-common-java/releases/tag/18.0.0).

## Features

* #210: Add anonymous feature tracking

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:edml-java:2.1.0` to `2.1.1`
* Updated `com.exasol:error-reporting-java:1.0.1` to `1.0.2`
* Updated `com.exasol:maven-project-version-getter:1.2.1` to `1.2.2`
* Updated `com.exasol:sql-statement-builder:4.6.0` to `4.6.1`
* Updated `com.exasol:virtual-schema-common-java:17.1.0` to `18.0.0`

### Test Dependency Updates

* Updated `com.exasol:hamcrest-resultset-matcher:1.7.1` to `1.7.2`
* Updated `com.exasol:test-db-builder-java:3.6.1` to `3.6.4`
* Updated `com.exasol:udf-debugging-java:0.6.17` to `0.6.18`
* Updated `commons-io:commons-io:2.19.0` to `2.21.0`
* Updated `nl.jqno.equalsverifier:equalsverifier:3.19` to `3.19.4`
* Updated `org.junit.jupiter:junit-jupiter-params:5.13.0` to `5.14.3`
* Updated `org.mockito:mockito-junit-jupiter:5.18.0` to `5.23.0`

### Plugin Dependency Updates

* Updated `org.apache.maven.plugins:maven-jar-plugin:3.4.1` to `3.5.0`
