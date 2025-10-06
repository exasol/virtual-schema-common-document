# Common Virtual Schema for Document Data 11.0.7, released 2025-10-??

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

## Documentation

* Added `LIMIT` trick and link to FAQ to LIKE error message (PR #207)

## Dependency Updates

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:2.0.4` to `2.0.5`
* Updated `com.exasol:project-keeper-maven-plugin:5.2.3` to `5.4.0`
* Updated `com.exasol:quality-summarizer-maven-plugin:0.2.0` to `0.2.1`
* Updated `io.github.git-commit-id:git-commit-id-maven-plugin:9.0.1` to `9.0.2`
* Updated `org.apache.maven.plugins:maven-clean-plugin:3.4.1` to `3.5.0`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.14.0` to `3.14.1`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.5.0` to `3.6.1`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.5.3` to `3.5.4`
* Updated `org.apache.maven.plugins:maven-gpg-plugin:3.2.7` to `3.2.8`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.11.2` to `3.12.0`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.5.3` to `3.5.4`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.7.0` to `1.7.3`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.18.0` to `2.19.1`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:5.1.0.4751` to `5.2.0.4988`
* Updated `org.sonatype.central:central-publishing-maven-plugin:0.7.0` to `0.8.0`
