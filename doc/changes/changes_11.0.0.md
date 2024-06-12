# Common Virtual Schema for Document Data 11.0.0, released 2024-06-12

Code name: Converter column names during automatic schema inference

## Summary

This release allows configuring the mapping of column names for the automatic schema inference. Before, the virtual schema always converted source column names to `UPPER_SNAKE_CASE` to create the Exasol column names. This is now configurable with EDML configuration option `autoInferenceColumnNames`. See the [EDML user guide](../user_guide/edml_user_guide.md#column-name-conversion) for details.

## Features

* #189: Added column name converter for automatic schema inference

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:edml-java:2.0.0` to `2.1.0`
* Updated `com.exasol:virtual-schema-common-java:17.0.1` to `17.1.0`
* Updated `org.logicng:logicng:2.4.1` to `2.5.0`

### Test Dependency Updates

* Updated `com.exasol:exasol-test-setup-abstraction-java:2.1.2` to `2.1.4`
* Updated `com.exasol:udf-debugging-java:0.6.12` to `0.6.13`
* Updated `org.jacoco:org.jacoco.agent:0.8.11` to `0.8.12`
* Updated `org.mockito:mockito-junit-jupiter:5.11.0` to `5.12.0`
* Updated `org.slf4j:slf4j-jdk14:2.0.12` to `2.0.13`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:2.0.2` to `2.0.3`
* Updated `com.exasol:project-keeper-maven-plugin:4.3.0` to `4.3.3`
* Updated `org.apache.maven.plugins:maven-deploy-plugin:3.1.1` to `3.1.2`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.4.1` to `3.5.0`
* Updated `org.apache.maven.plugins:maven-gpg-plugin:3.2.2` to `3.2.4`
* Updated `org.apache.maven.plugins:maven-jar-plugin:3.3.0` to `3.4.1`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.6.3` to `3.7.0`
* Updated `org.apache.maven.plugins:maven-toolchains-plugin:3.1.0` to `3.2.0`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:3.11.0.3922` to `4.0.0.4121`
* Updated `org.sonatype.plugins:nexus-staging-maven-plugin:1.6.13` to `1.7.0`
