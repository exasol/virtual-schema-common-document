# Common Virtual Schema for Document Data 11.0.7, released 2026-02-19

Code name: LIMIT trick on subselect with LIKE

## Summary

Due to the nature of document-based Virtual Schemas, `(NOT) LIKE` pushdown is only supported on the source reference column.

We clarified the error message that users get in case they try to apply that predicate to other columns of the VS.

In this case, users must use a subselect and add all filter criteria to the outer select. Users should also use a `LIMIT` on the outer select that is high enough to not really restrict the number of results from the outer select.

```sql
SELECT (
    SELECT * FROM virtual_schema
    -- ...
) WHERE any_other_column LIKE 'foo' LIMIT 1E15;
```

The limit prevents Exasol's optimizer from pulling the filter criteria into the inner select.

We also added a link to the VS FAQ.

This release also fixes S3 Virtual Schema file mapping failure if there is more than one table definition in the mapping

## Bugfixes

* #208: S3 Virtual Schema for PARQUET files fails if there is more than one table definition in the mapping

## Documentation

* Added `LIMIT` trick and link to FAQ to LIKE error message (PR #207)

## Dependency Updates

### Test Dependency Updates

* Updated `com.exasol:exasol-test-setup-abstraction-java:2.1.8` to `2.1.11`
* Updated `org.jacoco:org.jacoco.agent:0.8.13` to `0.8.14`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:2.0.4` to `2.0.6`
* Updated `com.exasol:project-keeper-maven-plugin:5.2.3` to `5.4.6`
* Updated `com.exasol:quality-summarizer-maven-plugin:0.2.0` to `0.2.1`
* Updated `io.github.git-commit-id:git-commit-id-maven-plugin:9.0.1` to `9.0.2`
* Updated `org.apache.maven.plugins:maven-artifact-plugin:3.6.0` to `3.6.1`
* Updated `org.apache.maven.plugins:maven-clean-plugin:3.4.1` to `3.5.0`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.14.0` to `3.15.0`
* Updated `org.apache.maven.plugins:maven-dependency-plugin:3.8.1` to `3.10.0`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.5.0` to `3.6.2`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.5.3` to `3.5.4`
* Updated `org.apache.maven.plugins:maven-gpg-plugin:3.2.7` to `3.2.8`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.11.2` to `3.12.0`
* Updated `org.apache.maven.plugins:maven-resources-plugin:3.3.1` to `3.4.0`
* Updated `org.apache.maven.plugins:maven-source-plugin:3.2.1` to `3.4.0`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.5.3` to `3.5.4`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.7.0` to `1.7.3`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.18.0` to `2.21.0`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.13` to `0.8.14`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:5.1.0.4751` to `5.5.0.6356`
* Updated `org.sonatype.central:central-publishing-maven-plugin:0.7.0` to `0.10.0`
