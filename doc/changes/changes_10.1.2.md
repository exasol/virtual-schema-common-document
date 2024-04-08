# Common Virtual Schema for Document Data 10.1.2, released 2024-04-08

Code name: Fixed CVE-2024-29025 in io.netty:netty-codec-http:jar:4.1.100.Final:test

## Summary

This release fixes vulnerability CVE-2024-29025 in `io.netty:netty-codec-http:jar:4.1.100.Final:test`.

**Excluded vulnerabilities:** This release contains vulnerability CVE-2017-10355 in transitive compile dependency `xerces:xercesImpl`. This library is only used for connecting to internal service ExaOperations during tests.

## Security

* #184: Fixed CVE-2024-29025 in `io.netty:netty-codec-http:jar:4.1.100.Final:test`

## Dependency Updates

### Test Dependency Updates

* Updated `com.exasol:exasol-test-setup-abstraction-java:2.1.0` to `2.1.2`
* Updated `com.exasol:test-db-builder-java:3.5.3` to `3.5.4`
* Updated `commons-io:commons-io:2.15.1` to `2.16.0`
* Updated `nl.jqno.equalsverifier:equalsverifier:3.15.8` to `3.16.1`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:2.0.1` to `2.0.2`
* Updated `com.exasol:project-keeper-maven-plugin:4.2.0` to `4.3.0`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.12.1` to `3.13.0`
* Updated `org.apache.maven.plugins:maven-gpg-plugin:3.1.0` to `3.2.2`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.11` to `0.8.12`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:3.10.0.2594` to `3.11.0.3922`
