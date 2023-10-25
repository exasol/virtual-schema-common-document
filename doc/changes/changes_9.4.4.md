# Common Virtual Schema for Document Data 9.4.4, released 2023-??-??

Code name: Internal refactoring

## Summary

This release refactors the code to remove the dependency on Lombok.

## Refactoring

* #170: Removed Lombok

## Dependency Updates

### Test Dependency Updates

* Added `com.jparams:to-string-verifier:1.4.8`
* Added `nl.jqno.equalsverifier:equalsverifier:3.15.2`
* Updated `org.jacoco:org.jacoco.agent:0.8.10` to `0.8.11`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:2.9.13` to `2.9.14`
* Removed `org.projectlombok:lombok-maven-plugin:1.18.20.0`
