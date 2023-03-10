# Common Virtual Schema for Document Data 9.3.0, released 2023-03-10

Code name: Schema Auto-inference

## Summary

This release adds support for auto inference of schemas. This allows users to omit the `mapping` element in the EDML JSON definition. Currently this is only supported for the Parquet file format in file based virtual schemas using `virtual-schema-common-document-files` (e.g. s3-document-files-virtual-schema and azure-data-lake-storage-gen2-document-files-virtual-schema).

To use this feature you can omit the `mapping` element from the EDML JSON Definition. See the [user guide](https://github.com/exasol/virtual-schema-common-document/blob/main/doc/user_guide/edml_user_guide.md) for details.

## Features

* #155: Added support for auto inference of schemas

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:edml-java:1.1.3` to `1.2.0`

### Test Dependency Updates

* Added `com.exasol:exasol-testcontainers:6.5.1`
* Removed `junit:junit:4.13.2`
* Updated `org.mockito:mockito-junit-jupiter:5.1.1` to `5.2.0`

### Plugin Dependency Updates

* Updated `org.apache.maven.plugins:maven-jar-plugin:2.2` to `3.3.0`
