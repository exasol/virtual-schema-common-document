# Common Virtual Schema for Document Data 9.4.1, released 2023-07-03

Code name: Document CSV auto inference

## Summary

This release adds documentation for the data type support and auto-inference for CSV files. This new feature is available in the following file based virtual schemas:

* [s3-document-files-virtual-schema 2.6.0](https://github.com/exasol/s3-document-files-virtual-schema/releases/tag/2.6.0)
* [azure-data-lake-storage-gen2-document-files-virtual-schema 1.4.0](https://github.com/exasol/azure-data-lake-storage-gen2-document-files-virtual-schema/releases/tag/1.4.0)
* [azure-blob-storage-document-files-virtual-schema 1.3.0](https://github.com/exasol/azure-blob-storage-document-files-virtual-schema/releases/tag/1.3.0)
* [google-cloud-storage-document-files-virtual-schema 1.3.0](https://github.com/exasol/google-cloud-storage-document-files-virtual-schema/releases/tag/1.3.0)
* [bucketfs-document-files-virtual-schema 1.2.0](https://github.com/exasol/bucketfs-document-files-virtual-schema/releases/tag/1.2.0)

The release also fixes vulnerability CVE-2023-34462 in transitive test dependency `io.netty:netty-handler`.

## Documentation

* #168: Documented data type support and auto-inference for CSV files

## Bugfixes

* #165: Upgraded dependencies
## Dependency Updates

### Test Dependency Updates

* Updated `com.exasol:exasol-test-setup-abstraction-java:2.0.1` to `2.0.2`
* Updated `commons-io:commons-io:2.11.0` to `2.13.0`
* Updated `org.mockito:mockito-junit-jupiter:5.3.1` to `5.4.0`
