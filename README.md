# Common module of Exasol Virtual Schema Adapters for Document Data Sources

[![Build Status](https://github.com/exasol/virtual-schema-common-document/actions/workflows/ci-build.yml/badge.svg)](https://github.com/exasol/virtual-schema-common-document/actions/workflows/ci-build.yml)
[![Maven Central – Common Virtual Schema for document data](https://img.shields.io/maven-central/v/com.exasol/virtual-schema-common-document)](https://search.maven.org/artifact/com.exasol/virtual-schema-common-document)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Avirtual-schema-common-document&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.exasol%3Avirtual-schema-common-document)

[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Avirtual-schema-common-document&metric=security_rating)](https://sonarcloud.io/dashboard?id=com.exasol%3Avirtual-schema-common-document)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Avirtual-schema-common-document&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=com.exasol%3Avirtual-schema-common-document)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Avirtual-schema-common-document&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=com.exasol%3Avirtual-schema-common-document)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Avirtual-schema-common-document&metric=sqale_index)](https://sonarcloud.io/dashboard?id=com.exasol%3Avirtual-schema-common-document)

[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Avirtual-schema-common-document&metric=code_smells)](https://sonarcloud.io/dashboard?id=com.exasol%3Avirtual-schema-common-document)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Avirtual-schema-common-document&metric=coverage)](https://sonarcloud.io/dashboard?id=com.exasol%3Avirtual-schema-common-document)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Avirtual-schema-common-document&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=com.exasol%3Avirtual-schema-common-document)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Avirtual-schema-common-document&metric=ncloc)](https://sonarcloud.io/dashboard?id=com.exasol%3Avirtual-schema-common-document)

This repository contains common parts for Virtual Schema adapters for document data sources.

## Push Down Selection

This Virtual Schema adapter supports pushing-down certain filters from the `WHERE` clause to the data source. Which filters are supported specifically depends on the dialect.

### Like

When pushing down `LIKE` expressions this adapter only supports `\` as escape character.

If you specify a different escape character like in the following example the Virtual Schema will throw an Exception.

```sql
SELECT * FROM FAMILY WHERE NAME LIKE 'T?' ESCAPE ':';
```

If you specify a different escape character by setting `DEFAULT_LIKE_ESCAPE_CHARACTER` the Virtual Schema will ignore it and still use `\`.

## Information for Users

* [EDML User Guide](doc/user_guide/edml_user_guide.md)
* [Changelog](doc/changes/changelog.md)
* [Dependencies](dependencies.md)

### Dialects:

* [DynamoDB](https://github.com/exasol/dynamodb-virtual-schema)
* [Files](https://github.com/exasol/virtual-schema-common-document-files): see [available storage variants](https://github.com/exasol/virtual-schema-common-document-files#storage-variants)

## Information for Developers

You can use this repository as a basis for developing a custom Virtual Schema for document data.
