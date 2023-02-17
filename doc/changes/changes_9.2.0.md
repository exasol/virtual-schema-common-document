# Common Virtual Schema for Document Data 9.2.0, released 2023-02-17

Code name: Dependency Upgrade

## Summary

Updated dependencies, especially to account for breaking change in `edml-java` renaming method `com.exasol.adapter.document.edml.Fields.getFields()` to `getFieldsMap()`.

Removed usages of `org.json` in tests and fixed IDE warnings.

Suppressed message about vulnerability in transitive dependency `fr.turri:aXMLRPC` via `com.exasol:exasol-test-setup-abstraction-java:jar`. CVE-2020-36641 is reported to be fixed in aXMLRPC 1.12.1, while ETAJ uses version 1.13.0, so this is a false positive.


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

* Updated `com.exasol:error-code-crawler-maven-plugin:1.2.1` to `1.2.2`
* Updated `com.exasol:project-keeper-maven-plugin:2.9.1` to `2.9.3`
* Updated `org.apache.maven.plugins:maven-dependency-plugin:3.3.0` to `3.5.0`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.0.0-M7` to `3.0.0-M8`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.0.0-M7` to `3.0.0-M8`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.13.0` to `2.14.2`
