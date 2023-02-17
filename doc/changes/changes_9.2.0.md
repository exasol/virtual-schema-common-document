# Common Virtual Schema for Document Data 9.2.0, released 2023-02-17

Code name: Dependency Upgrade

## Summary

Updated dependencies, especially to account for breaking change in `edml-java` renaming method `com.exasol.adapter.document.edml.Fields.getFields()` to `getFieldsMap()`.

## Refactorings

* #156: Updated dependencies

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:edml-java:1.1.2` to `1.1.3`
* Updated `com.exasol:error-reporting-java:1.0.0` to `1.0.1`

### Test Dependency Updates

* Updated `com.exasol:exasol-test-setup-abstraction-java:1.1.1` to `2.0.0`
* Updated `com.exasol:test-db-builder-java:3.4.1` to `3.4.2`
* Updated `com.exasol:udf-debugging-java:0.6.5` to `0.6.8`
* Updated `org.junit.jupiter:junit-jupiter-engine:5.9.1` to `5.9.2`
* Updated `org.junit.jupiter:junit-jupiter-params:5.9.1` to `5.9.2`
* Updated `org.mockito:mockito-junit-jupiter:4.10.0` to `5.1.1`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:2.9.1` to `2.9.3`
