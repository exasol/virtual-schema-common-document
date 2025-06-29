# Common Virtual Schema for Document Data 11.0.4, released 2025-06-24

Code name: Improve query plan logging

## Summary

This release implements `toString()` for classes `TableMapping` and `RemoteTableQuery`. This allows improving logging around the paths that lead to the creation of the query plan.

## Features

* #202: Add more logging around the paths that lead to the creation of the query plan

## Dependency Updates

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:5.1.0` to `5.2.2`
* Added `org.sonatype.central:central-publishing-maven-plugin:0.7.0`
* Removed `org.sonatype.plugins:nexus-staging-maven-plugin:1.7.0`
