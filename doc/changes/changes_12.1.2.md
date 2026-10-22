# Common Virtual Schema for Document Data 12.1.2, released 2026-10-20

Code name: Update udf-debugging-java to 0.6.22

## Summary

This release updates udf-debugging-java to 0.6.22 to fix vulnerabilities in transitive dependencies. Since this is a test dependency, users do not need to apply this update it only affects building and testing.

For more details, please see https://github.com/exasol/udf-debugging-java/releases/tag/0.6.21 and https://github.com/exasol/udf-debugging-java/releases/tag/0.6.22

## Security

* Update udf-debugging-java to 0.6.22 (PR# 223)

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:virtual-schema-common-java:18.0.4` to `18.1.0`

### Test Dependency Updates

* Updated `com.exasol:hamcrest-resultset-matcher:1.7.3` to `1.7.4`
* Updated `com.exasol:test-db-builder-java:4.0.2` to `4.0.3`
* Updated `com.exasol:udf-debugging-java:0.6.20` to `0.6.22`
* Updated `nl.jqno.equalsverifier:equalsverifier:3.19.4` to `4.5.2`
* Updated `org.mockito:mockito-junit-jupiter:5.23.0` to `5.24.0`
* Updated `org.slf4j:slf4j-jdk14:2.0.19` to `2.0.20`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:2.1.1` to `2.1.2`
* Updated `com.exasol:project-keeper-maven-plugin:5.7.5` to `5.7.7`
* Updated `org.apache.maven.plugins:maven-artifact-plugin:3.6.1` to `3.7.0`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.15.0` to `3.16.0`
* Updated `org.apache.maven.plugins:maven-deploy-plugin:3.1.4` to `3.2.0`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.5.6` to `3.6.0`
* Updated `org.apache.maven.plugins:maven-install-plugin:3.1.4` to `3.2.0`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.5.6` to `3.6.0`
* Updated `org.codehaus.mojo:build-helper-maven-plugin:3.6.1` to `3.6.2`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.21.0` to `2.23.0`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:5.7.0.6970` to `5.8.0.7211`
* Updated `org.sonatype.central:central-publishing-maven-plugin:0.11.0` to `0.12.0`
