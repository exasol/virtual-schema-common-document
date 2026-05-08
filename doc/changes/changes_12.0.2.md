# Common Virtual Schema for Document Data 12.0.2, released 2026-05-08

Code name: Update telemetry endpoint

## Summary

This release updates the telemetry-java library to use the correct telemetry endpoint. The previous release 12.0.1 didn't actually contain the correct dependency version.

## Bugfixes

* #212: Upgrade telemetry-java to use correct telemetry endpoint

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:virtual-schema-common-java:18.0.0` to `18.0.1`

### Test Dependency Updates

* Updated `org.junit.jupiter:junit-jupiter-params:5.14.3` to `5.14.4`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:5.5.2` to `5.6.2`
