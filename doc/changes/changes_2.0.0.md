# Virtual Schema Common Document 2.0.0, released 2020-XX-XX

Code name: 

## Summary

Breaking API Changes:

* `DataLoaderUdf` was replaced by `DataLoader` interface
* UDF entry point renamed from `UdfRequestDispatcher` to `UdfEntryPoint`
* UDF name is now `IMPORT_FORM_DOCUMENT` for all dialects


## Features / Enhancements

* #23: Refactored UDF factory API

## Dependency updates

* Updated `com.exasol:exasol-testcontainers` from 0.2.1 to 3.0.0
