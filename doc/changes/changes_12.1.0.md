# Common Virtual Schema for Document Data 12.1.0, released 2026-07-31

Code name: Add support for `TIMESTAMP` with precision

## Summary

This release adds support for mapping `TIMESTAMP` types with precision from `TIMESTAMP(0)` to `TIMESTAMP(9)`.

## Features

* #186: Added support for `TIMESTAMP` with precision.

## Dependency Updates

### Test Dependency Updates

* Updated `com.exasol:hamcrest-resultset-matcher:1.7.2` to `1.7.3`
* Updated `com.exasol:test-db-builder-java:4.0.1` to `4.0.2`
