# Common Virtual Schema for document data 9.1.2, released 2022-12-19

Code name: Dependency Upgrade

## Summary

Updated dependencies to use artifacts from maven as repository maven.exasol.com has been discontinued.

## Changes

* #153: Updated dependencies

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:error-reporting-java:0.4.1` to `1.0.0`
* Updated `com.exasol:maven-project-version-getter:1.1.0` to `1.2.0`
* Updated `com.exasol:sql-statement-builder:4.5.1` to `4.5.2`
* Updated `com.exasol:virtual-schema-common-java:15.3.2` to `16.2.0`
* Updated `org.logicng:logicng:2.2.1` to `2.4.1`
* Updated `org.slf4j:slf4j-jdk14:1.7.36` to `2.0.6`

### Test Dependency Updates

* Updated `com.exasol:exasol-test-setup-abstraction-java:0.3.2` to `1.1.0`
* Updated `com.exasol:hamcrest-resultset-matcher:1.5.1` to `1.5.2`
* Updated `com.exasol:test-db-builder-java:3.3.3` to `3.4.1`
* Updated `com.exasol:udf-debugging-java:0.6.4` to `0.6.5`
* Updated `org.junit.jupiter:junit-jupiter-engine:5.8.2` to `5.9.1`
* Updated `org.junit.jupiter:junit-jupiter-params:5.8.2` to `5.9.1`
* Updated `org.mockito:mockito-junit-jupiter:4.6.1` to `4.10.0`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:1.1.1` to `1.2.1`
* Updated `com.exasol:project-keeper-maven-plugin:2.4.6` to `2.9.2`
* Updated `io.github.zlika:reproducible-build-maven-plugin:0.15` to `0.16`
* Updated `org.apache.maven.plugins:maven-deploy-plugin:3.0.0-M1` to `3.0.0`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.0.0` to `3.1.0`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.0.0-M5` to `3.0.0-M7`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.4.0` to `3.4.1`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.0.0-M5` to `3.0.0-M7`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.2.7` to `1.3.0`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.10.0` to `2.13.0`
