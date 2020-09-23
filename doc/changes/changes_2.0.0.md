# Virtual Schema Common Document 2.0.0, released 2020-09-23

Code name: Refactoring + Small new features

## Summary

Breaking API Changes:

* `DataLoaderUdf` was replaced by `DataLoader` interface
* UDF entry point renamed from `UdfRequestDispatcher` to `UdfEntryPoint`
* UDF name is now `IMPORT_FORM_DOCUMENT` for all dialects
* #22 caused an EDML version change from 1.0.0 to 1.1.0


## Features / Enhancements

* #23: Refactored UDF factory API
* #22: EDML: Made behaviour for toVarchar mapping if not string configurable

## Bug Fixes

* #24: Removed hardcoded UDF schema (#26)

## Documentation

* #28: Added generic EDML user guide
* #30: Generate documentation for EDML 1.1.0

## Dependency updates

* Updated `com.exasol:exasol-testcontainers` from 0.2.1 to 3.0.0
