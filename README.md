 # Common module of Exasol Virtual Schema Adapters for Document Data Sources
 
 [![Build Status](https://travis-ci.com/exasol/virtual-schema-common-document.svg?branch=master)](https://travis-ci.com/exasol/virtual-schema-common-document)
 [![Maven Central](https://img.shields.io/maven-central/v/com.exasol/virtual-schema-common-document)](https://search.maven.org/artifact/com.exasol/virtual-schema-common-document)
 
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
 
 ## Information for Users
 
 * [EDML User Guide](doc/user_guide/edml_user_guide.md)
 * [Changelog](doc/changes/changelog.md)
 
### Dialects:
 
 * [DynamoDB](https://github.com/exasol/dynamodb-virtual-schema)
 
 ## Information for Developers
 
 You can use this repository as a basis for developing a custom Virtual Schema for document data.
 
 ## Dependencies
 
 ### Run Time Dependencies
 
 Running the DynamoDB Virtual Schema requires a Java Runtime version 11 or later.
 
 | Dependency                                                                          | Purpose                                                     | License                          |
 |-------------------------------------------------------------------------------------|-------------------------------------------------------------|----------------------------------|
 | [Exasol Script API](https://docs.exasol.com/database_concepts/udf_scripts.htm)      | Accessing Exasol features                                   | MIT License                      |
 | [Exasol Virtual Schema Common Java][exasol-virtual-schema-common-java]              | Common module of Exasol Virtual Schemas adapters            | MIT License  
 | [JSON Schema Validator](https://github.com/everit-org/json-schema)                  | Validating the Exasol Document Mapping Language definitions | Apache License 2.0
 | [Exasol SQL statement builder](https://github.com/exasol/sql-statement-builder)     | Building pushdown SQL statements                            | MIT License
 | [LogicNG](https://github.com/logic-ng/LogicNG)                                      | DNF normalization                                           | Apache License 2.0
 
 ### Test Dependencies
 
 | Dependency                                                                          | Purpose                                                | License                          |
 |-------------------------------------------------------------------------------------|--------------------------------------------------------|----------------------------------|
 | [Apache Maven](https://maven.apache.org/)                                           | Build tool                                             | Apache License 2.0               |
 | [Exasol Testcontainers][exasol-testcontainers]                                      | Exasol extension for the Testcontainers framework      | MIT License                      |
 | [Java Hamcrest](http://hamcrest.org/JavaHamcrest/)                                  | Checking for conditions in code via matchers           | BSD License                      |
 | [JUnit](https://junit.org/junit5)                                                   | Unit testing framework                                 | Eclipse Public License 1.0       |
 | [Testcontainers](https://www.testcontainers.org/)                                   | Container-based integration tests                      | MIT License                      |
 | [SLF4J](http://www.slf4j.org/)                                                      | Logging facade                                         | MIT License                      |
 | [Apache XMLRPC](https://mvnrepository.com/artifact/org.apache.xmlrpc)               | Connecting to ExaOperation XMLRPC interface            | Apache License 2.0               |
 
 ### CI Dependencies
 | Dependency                                                                          | Purpose                                                  | License                          |
 |-------------------------------------------------------------------------------------|----------------------------------------------------------|----------------------------------|
 | [bootprint](https://www.npmjs.com/package/bootprint)                                | Generating [EDML documentation][edml-doc]                | MIT License                      |
 | [bootprint-json-schema](https://www.npmjs.com/package/bootprint-json-schema)        | Generating [EDML documentation][edml-doc]                | MIT License                      |
 | [GitHub Pages Deploy Action][github-pages-deploy-action]                            | Deploying [EDML documentation][edml-doc] to github pages | MIT License                      |
 | [ssh-agent GitHub Action](https://github.com/webfactory/ssh-agent)                  | Deploying [EDML documentation][edml-doc] to github pages | MIT License                      |
 | [Github Checkout Action](https://github.com/actions/checkout)                       | Github actions                                           | MIT License                      |
 | [GitHub Releases API Action](https://github.com/actions/upload-release-asset)       | Automated release                                        | MIT License                      |
 
 ### Maven Plug-ins
 
 | Plug-in                                                                             | Purpose                                                | License                          |
 |-------------------------------------------------------------------------------------|--------------------------------------------------------|----------------------------------|
 | [Maven Compiler Plugin](https://maven.apache.org/plugins/maven-compiler-plugin/)    | Setting required Java version                          | Apache License 2.0               |
 | [Maven Exec Plugin](https://www.mojohaus.org/exec-maven-plugin/)                    | Executing external applications                        | Apache License 2.0               |
 | [Maven Assembly Plugin](https://maven.apache.org/plugins/maven-assembly-plugin/)    | Creating JAR                                           | Apache License 2.0               |
 | [Maven Enforcer Plugin][maven-enforcer-plugin]                                      | Controlling environment constants                      | Apache License 2.0               |
 | [Maven Failsafe Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/)   | Integration testing                                    | Apache License 2.0               |
 | [Maven Javadoc Plugin](https://maven.apache.org/plugins/maven-javadoc-plugin/)      | Creating a Javadoc JAR                                 | Apache License 2.0               |
 | [Maven GPG Plugin](https://maven.apache.org/plugins/maven-gpg-plugin/)              | Signs JARs                                             | Apache License 2.0               |
 | [Maven Jacoco Plugin](https://www.eclemma.org/jacoco/trunk/doc/maven.html)          | Code coverage metering                                 | Eclipse Public License 2.0       |
 | [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/)   | Unit testing                                           | Apache License 2.0               |
 | [Maven Source Plugin](https://maven.apache.org/plugins/maven-source-plugin/)        | Creating a source code JAR                             | Apache License 2.0               |
 | [Maven Dependency Plugin](https://maven.apache.org/plugins/maven-dependency-plugin/)| Unpacking jacoco agent                                 | Apache License 2.0               |
 | [Sonatype OSS Index Maven Plugin][sonatype-oss-index-maven-plugin]                  | Checking Dependencies Vulnerability                    | ASL2                             |
 | [Versions Maven Plugin][versions-maven-plugin]                                      | Checking if dependencies updates are available         | Apache License 2.0               |
 
 [exasol-testcontainers]: https://github.com/exasol/exasol-testcontainers
 [maven-enforcer-plugin]: http://maven.apache.org/enforcer/maven-enforcer-plugin/
 [mysql-jdbc-driver]: https://dev.mysql.com/downloads/connector/j/
 [oracle-jdbc-driver]: https://www.oracle.com/database/technologies/appdev/jdbc.html
 [postgresql-jdbc-driver]: https://jdbc.postgresql.org/
 [sonatype-oss-index-maven-plugin]: https://sonatype.github.io/ossindex-maven/maven-plugin/
 [versions-maven-plugin]: https://www.mojohaus.org/versions-maven-plugin/
 [exasol-virtual-schema-common-java]: https://github.com/exasol/virtual-schema-common-java
 [edml-doc]: https://exasol.github.io/virtual-schema-common-ducument/schema_doc/edml_1.1.0/index.html
 [github-pages-deploy-action]: https://github.com/JamesIves/github-pages-deploy-action