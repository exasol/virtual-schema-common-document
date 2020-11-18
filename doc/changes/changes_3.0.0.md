# Virtual Schema Common Document 3.0.0, released 2020-11-18

Code name: Added optional SOURCE_REFERENCE column

## Features / Enhancements

* #36: Added optional SOURCE_REFERENCE column
  Caused new EDML version 1.2.0. See the [user-guide](../user_guide/edml_user_guide.md#source-reference-column).
* #40: Support selection on SOURCE_REFERENCE column
* #42: Added support for LIKE predicate
* #49: Moved test resources to dynamodb virtual schema
* #48: Made schema validation errors more readable
* #37: Used unified error codes
* #47: Allowed `/buckets` prefix for `MAPPING` property

## Bugfixes

* #44: Correct data types for empty result table

## Dependency updates
 * Added `com.exasol:project-keeper-maven-plugin` 0.3.0
 * Updated `com.exasol:sql-statement-builder` from 4.1.0 to 4.3.0
 * Updated `org.logicng:logicng` from 2.0.0 to 2.0.2
 * Updated `org.mockito:mockito-core` from 3.5.13 to 3.6.0
 * Removed `com.exasol:exasol-testcontainers`
 * Added `com.exasol:error-reporting-java` 0.1.2
 * Removed `com.exasol:junit-platform-runner`
 * Removed `org.testcontainers:junit-jupiter`
 * Removed `org.jacoco:org.jacoco.agent`
 * Removed `org.jacoco:org.jacoco.core`
 * Removed `org.apache.xmlrpc:xmlrpc-client`
 * Updated `org.junit.jupiter:junit-jupiter-engine` from 5.6.2 to 5.7.0 
 * Updated `org.junit.jupiter:junit-jupiter-params` from 5.6.2 to 5.7.0

