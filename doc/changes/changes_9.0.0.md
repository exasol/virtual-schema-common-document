# Common Virtual Schema for document data 9.0.0, released 2022-??-??

Code name:

## Refactoring

* #132: Removed Jackson databind dependency
* #134: Moved EDML model to dedicated repo

## Dependency Updates

### Compile Dependency Updates

* Added `com.exasol:edml-java:1.0.0`
* Removed `com.fasterxml.jackson.core:jackson-databind:2.13.1`
* Removed `com.github.everit-org.json-schema:org.everit.json.schema:1.14.0`
* Removed `jakarta.json:jakarta.json-api:2.0.1`

### Runtime Dependency Updates

* Removed `org.glassfish:jakarta.json:2.0.1`

### Test Dependency Updates

* Updated `com.exasol:test-db-builder-java:3.3.0` to `3.3.1`
* Updated `com.exasol:udf-debugging-java:0.5.0` to `0.6.0`
* Updated `org.apache.maven.shared:maven-verifier:1.7.2` to `1.8.0`
* Updated `org.jacoco:org.jacoco.agent:0.8.7` to `0.8.5`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:1.3.4` to `2.1.0`
* Updated `io.github.zlika:reproducible-build-maven-plugin:0.13` to `0.15`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.8.1` to `3.9.0`
* Updated `org.apache.maven.plugins:maven-dependency-plugin:2.8` to `3.2.0`
* Updated `org.apache.maven.plugins:maven-deploy-plugin:2.7` to `3.0.0-M1`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.0.0-M3` to `3.0.0`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.0.0-M4` to `3.0.0-M5`
* Updated `org.apache.maven.plugins:maven-gpg-plugin:1.6` to `3.0.1`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.0.0-M4` to `3.0.0-M5`
* Added `org.codehaus.mojo:flatten-maven-plugin:1.2.7`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.7` to `2.8.1`
