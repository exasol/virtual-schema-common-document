<!-- @formatter:off -->
# Dependencies

## Compile Dependencies

| Dependency                                            | License                                       |
| ----------------------------------------------------- | --------------------------------------------- |
| [Common module of Exasol Virtual Schemas Adapters][0] | [MIT][1]                                      |
| [SLF4J JDK14 Binding][2]                              | [MIT License][3]                              |
| [everit-org/json-schema][4]                           | [Apache License, Version 2.0][5]              |
| [Exasol SQL Statement Builder][6]                     | [MIT][1]                                      |
| [LogicNG][8]                                          | [The Apache License, Version 2.0][5]          |
| [error-reporting-java][10]                            | [MIT][1]                                      |
| [Project Lombok][12]                                  | [The MIT License][13]                         |
| [jackson-databind][14]                                | [The Apache Software License, Version 2.0][5] |

## Test Dependencies

| Dependency                               | License                           |
| ---------------------------------------- | --------------------------------- |
| [Hamcrest][16]                           | [BSD License 3][17]               |
| [JUnit Jupiter Engine][18]               | [Eclipse Public License v2.0][19] |
| [JUnit Jupiter Params][18]               | [Eclipse Public License v2.0][19] |
| [mockito-junit-jupiter][22]              | [The MIT License][23]             |
| [exasol-test-setup-abstraction-java][24] | [MIT][1]                          |
| [Test Database Builder for Java][26]     | [MIT][1]                          |
| [udf-debugging-java][28]                 | [MIT][1]                          |
| [Apache Maven Verifier Component][30]    | [Apache License, Version 2.0][31] |
| [JUnit][32]                              | [Eclipse Public License 1.0][33]  |
| [Matcher for SQL Result Sets][34]        | [MIT][1]                          |

## Runtime Dependencies

| Dependency            | License                          |
| --------------------- | -------------------------------- |
| [JaCoCo :: Agent][36] | [Eclipse Public License 2.0][37] |

## Plugin Dependencies

| Dependency                                              | License                                       |
| ------------------------------------------------------- | --------------------------------------------- |
| [Maven Surefire Plugin][38]                             | [Apache License, Version 2.0][31]             |
| [Maven Failsafe Plugin][40]                             | [Apache License, Version 2.0][31]             |
| [JaCoCo :: Maven Plugin][42]                            | [Eclipse Public License 2.0][37]              |
| [Apache Maven Compiler Plugin][44]                      | [Apache License, Version 2.0][31]             |
| [Maven Dependency Plugin][46]                           | [The Apache Software License, Version 2.0][5] |
| [Maven Jar Plugin][48]                                  | [The Apache Software License, Version 2.0][5] |
| [Versions Maven Plugin][50]                             | [Apache License, Version 2.0][31]             |
| [Apache Maven Source Plugin][52]                        | [Apache License, Version 2.0][31]             |
| [Apache Maven Javadoc Plugin][54]                       | [Apache License, Version 2.0][31]             |
| [Apache Maven GPG Plugin][56]                           | [Apache License, Version 2.0][5]              |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][58] | [ASL2][5]                                     |
| [Apache Maven Enforcer Plugin][60]                      | [Apache License, Version 2.0][31]             |
| [Project keeper maven plugin][62]                       | [MIT][1]                                      |
| [Maven Deploy Plugin][64]                               | [The Apache Software License, Version 2.0][5] |
| [Nexus Staging Maven Plugin][66]                        | [Eclipse Public License][33]                  |
| [error-code-crawler-maven-plugin][68]                   | [MIT][1]                                      |
| [Reproducible Build Maven Plugin][70]                   | [Apache 2.0][5]                               |
| [Lombok Maven Plugin][72]                               | [The MIT License][1]                          |
| [Maven Clean Plugin][74]                                | [The Apache Software License, Version 2.0][5] |
| [Maven Resources Plugin][76]                            | [The Apache Software License, Version 2.0][5] |
| [Maven Install Plugin][78]                              | [The Apache Software License, Version 2.0][5] |
| [Maven Site Plugin 3][80]                               | [The Apache Software License, Version 2.0][5] |

[36]: https://www.eclemma.org/jacoco/index.html
[62]: https://github.com/exasol/project-keeper-maven-plugin
[10]: https://github.com/exasol/error-reporting-java
[4]: https://github.com/everit-org/json-schema
[5]: http://www.apache.org/licenses/LICENSE-2.0.txt
[12]: https://projectlombok.org
[38]: https://maven.apache.org/surefire/maven-surefire-plugin/
[74]: http://maven.apache.org/plugins/maven-clean-plugin/
[30]: https://maven.apache.org/shared/maven-verifier/
[1]: https://opensource.org/licenses/MIT
[22]: https://github.com/mockito/mockito
[50]: http://www.mojohaus.org/versions-maven-plugin/
[17]: http://opensource.org/licenses/BSD-3-Clause
[44]: https://maven.apache.org/plugins/maven-compiler-plugin/
[56]: http://maven.apache.org/plugins/maven-gpg-plugin/
[32]: http://junit.org
[37]: https://www.eclipse.org/legal/epl-2.0/
[14]: http://github.com/FasterXML/jackson
[42]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[23]: https://github.com/mockito/mockito/blob/main/LICENSE
[13]: https://projectlombok.org/LICENSE
[34]: https://github.com/exasol/hamcrest-resultset-matcher
[70]: http://zlika.github.io/reproducible-build-maven-plugin
[3]: http://www.opensource.org/licenses/mit-license.php
[18]: https://junit.org/junit5/
[52]: https://maven.apache.org/plugins/maven-source-plugin/
[16]: http://hamcrest.org/JavaHamcrest/
[2]: http://www.slf4j.org
[76]: http://maven.apache.org/plugins/maven-resources-plugin/
[24]: https://github.com/exasol/exasol-test-setup-abstraction-java
[66]: http://www.sonatype.com/public-parent/nexus-maven-plugins/nexus-staging/nexus-staging-maven-plugin/
[40]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[8]: http://www.logicng.org
[26]: https://github.com/exasol/test-db-builder-java
[6]: https://github.com/exasol/sql-statement-builder
[46]: http://maven.apache.org/plugins/maven-dependency-plugin/
[72]: http://anthonywhitford.com/lombok.maven/lombok-maven-plugin/
[33]: http://www.eclipse.org/legal/epl-v10.html
[48]: http://maven.apache.org/plugins/maven-jar-plugin/
[31]: https://www.apache.org/licenses/LICENSE-2.0.txt
[60]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[19]: https://www.eclipse.org/legal/epl-v20.html
[78]: http://maven.apache.org/plugins/maven-install-plugin/
[58]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[28]: https://github.com/exasol/udf-debugging-java
[64]: http://maven.apache.org/plugins/maven-deploy-plugin/
[80]: http://maven.apache.org/plugins/maven-site-plugin/
[54]: https://maven.apache.org/plugins/maven-javadoc-plugin/
[0]: https://github.com/exasol/virtual-schema-common-java
[68]: https://github.com/exasol/error-code-crawler-maven-plugin
