# Common Virtual Schema for Document Data 11.0.2, released 2025-02-12

Code name: Fix vulnerabilities CVE-2025-25193 and CVE-2025-24970 in test dependencies

## Summary

This release fixes the following vulnerabilities in test dependencies:

* `io.netty:netty-common:jar:4.1.115.Final:test`: CVE-2025-25193
* `io.netty:netty-handler:jar:4.1.115.Final:test`: CVE-2025-24970

## Security

* #194: Fixed CVE-2025-25193 in `io.netty:netty-common:jar:4.1.115.Final:test`
* #195: Fixed CVE-2025-24970 in `io.netty:netty-handler:jar:4.1.115.Final:test`

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:maven-project-version-getter:1.2.0` to `1.2.1`

### Test Dependency Updates

* Updated `com.exasol:exasol-test-setup-abstraction-java:2.1.6` to `2.1.7`
* Updated `com.exasol:udf-debugging-java:0.6.13` to `0.6.14`
* Updated `commons-io:commons-io:2.17.0` to `2.18.0`
* Updated `nl.jqno.equalsverifier:equalsverifier:3.17.3` to `3.19`
* Updated `org.junit.jupiter:junit-jupiter-params:5.11.3` to `5.11.4`
* Updated `org.mockito:mockito-junit-jupiter:5.14.2` to `5.15.2`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:4.4.0` to `4.5.0`
* Updated `org.apache.maven.plugins:maven-dependency-plugin:3.8.0` to `3.8.1`
* Updated `org.apache.maven.plugins:maven-deploy-plugin:3.1.2` to `3.1.3`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.5.1` to `3.5.2`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.10.1` to `3.11.1`
* Updated `org.apache.maven.plugins:maven-site-plugin:3.9.1` to `3.21.0`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.5.1` to `3.5.2`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.17.1` to `2.18.0`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:4.0.0.4121` to `5.0.0.4389`
