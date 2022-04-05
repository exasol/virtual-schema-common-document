<!-- @formatter:off -->
# Dependencies

## Compile Dependencies

| Dependency                                            | License                                                                                                        |
| ----------------------------------------------------- | -------------------------------------------------------------------------------------------------------------- |
| [Common module of Exasol Virtual Schemas Adapters][0] | [MIT][1]                                                                                                       |
| [SLF4J JDK14 Binding][2]                              | [MIT License][3]                                                                                               |
| [everit-org/json-schema][4]                           | [Apache License, Version 2.0][5]                                                                               |
| [Exasol SQL Statement Builder][6]                     | [MIT][1]                                                                                                       |
| [LogicNG][8]                                          | [The Apache License, Version 2.0][5]                                                                           |
| [error-reporting-java][10]                            | [MIT][1]                                                                                                       |
| [Project Lombok][12]                                  | [The MIT License][13]                                                                                          |
| [Jakarta JSON Processing API][14]                     | [Eclipse Public License 2.0][15]; [GNU General Public License, version 2 with the GNU Classpath Exception][16] |
| [Maven Project Version Getter][17]                    | [MIT][1]                                                                                                       |

## Test Dependencies

| Dependency                               | License                           |
| ---------------------------------------- | --------------------------------- |
| [Hamcrest][19]                           | [BSD License 3][20]               |
| [JUnit Jupiter Engine][21]               | [Eclipse Public License v2.0][22] |
| [JUnit Jupiter Params][21]               | [Eclipse Public License v2.0][22] |
| [mockito-junit-jupiter][25]              | [The MIT License][26]             |
| [exasol-test-setup-abstraction-java][27] | [MIT][1]                          |
| [Test Database Builder for Java][29]     | [MIT][1]                          |
| [udf-debugging-java][31]                 | [MIT][1]                          |
| [Apache Maven Verifier Component][33]    | [Apache License, Version 2.0][34] |
| [Apache Commons IO][35]                  | [Apache License, Version 2.0][34] |
| [JUnit][37]                              | [Eclipse Public License 1.0][38]  |
| [Matcher for SQL Result Sets][39]        | [MIT][1]                          |
| [JaCoCo :: Agent][41]                    | [Eclipse Public License 2.0][42]  |

## Runtime Dependencies

| Dependency                    | License                                                                                                        |
| ----------------------------- | -------------------------------------------------------------------------------------------------------------- |
| [JSON-P Default Provider][14] | [Eclipse Public License 2.0][15]; [GNU General Public License, version 2 with the GNU Classpath Exception][16] |

## Plugin Dependencies

| Dependency                                              | License                                       |
| ------------------------------------------------------- | --------------------------------------------- |
| [Apache Maven Compiler Plugin][46]                      | [Apache License, Version 2.0][34]             |
| [Apache Maven Enforcer Plugin][48]                      | [Apache License, Version 2.0][34]             |
| [Maven Flatten Plugin][50]                              | [Apache Software Licenese][5]                 |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][52] | [ASL2][5]                                     |
| [Reproducible Build Maven Plugin][54]                   | [Apache 2.0][5]                               |
| [Maven Surefire Plugin][56]                             | [Apache License, Version 2.0][34]             |
| [Versions Maven Plugin][58]                             | [Apache License, Version 2.0][34]             |
| [Apache Maven Deploy Plugin][60]                        | [Apache License, Version 2.0][34]             |
| [Apache Maven GPG Plugin][62]                           | [Apache License, Version 2.0][34]             |
| [Apache Maven Source Plugin][64]                        | [Apache License, Version 2.0][34]             |
| [Apache Maven Javadoc Plugin][66]                       | [Apache License, Version 2.0][34]             |
| [Nexus Staging Maven Plugin][68]                        | [Eclipse Public License][38]                  |
| [Apache Maven Dependency Plugin][70]                    | [Apache License, Version 2.0][34]             |
| [Lombok Maven Plugin][72]                               | [The MIT License][1]                          |
| [Maven Jar Plugin][74]                                  | [The Apache Software License, Version 2.0][5] |
| [Project keeper maven plugin][76]                       | [MIT][1]                                      |
| [Maven Failsafe Plugin][78]                             | [Apache License, Version 2.0][34]             |
| [JaCoCo :: Maven Plugin][80]                            | [Eclipse Public License 2.0][42]              |
| [error-code-crawler-maven-plugin][82]                   | [MIT][1]                                      |
| [Maven Clean Plugin][84]                                | [The Apache Software License, Version 2.0][5] |
| [Maven Resources Plugin][86]                            | [The Apache Software License, Version 2.0][5] |
| [Maven Install Plugin][88]                              | [The Apache Software License, Version 2.0][5] |
| [Maven Site Plugin 3][90]                               | [The Apache Software License, Version 2.0][5] |

[41]: https://www.eclemma.org/jacoco/index.html
[10]: https://github.com/exasol/error-reporting-java
[4]: https://github.com/everit-org/json-schema
[5]: http://www.apache.org/licenses/LICENSE-2.0.txt
[12]: https://projectlombok.org
[56]: https://maven.apache.org/surefire/maven-surefire-plugin/
[84]: http://maven.apache.org/plugins/maven-clean-plugin/
[33]: https://maven.apache.org/shared/maven-verifier/
[1]: https://opensource.org/licenses/MIT
[25]: https://github.com/mockito/mockito
[17]: https://github.com/exasol/maven-project-version-getter
[58]: http://www.mojohaus.org/versions-maven-plugin/
[20]: http://opensource.org/licenses/BSD-3-Clause
[46]: https://maven.apache.org/plugins/maven-compiler-plugin/
[37]: http://junit.org
[42]: https://www.eclipse.org/legal/epl-2.0/
[60]: https://maven.apache.org/plugins/maven-deploy-plugin/
[80]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[26]: https://github.com/mockito/mockito/blob/main/LICENSE
[13]: https://projectlombok.org/LICENSE
[35]: https://commons.apache.org/proper/commons-io/
[39]: https://github.com/exasol/hamcrest-resultset-matcher
[54]: http://zlika.github.io/reproducible-build-maven-plugin
[3]: http://www.opensource.org/licenses/mit-license.php
[76]: https://github.com/exasol/project-keeper-maven-plugin/project-keeper-maven-plugin-generated-parent/project-keeper-maven-plugin
[21]: https://junit.org/junit5/
[50]: https://www.mojohaus.org/flatten-maven-plugin/flatten-maven-plugin
[14]: https://github.com/eclipse-ee4j/jsonp
[64]: https://maven.apache.org/plugins/maven-source-plugin/
[16]: https://projects.eclipse.org/license/secondary-gpl-2.0-cp
[19]: http://hamcrest.org/JavaHamcrest/
[2]: http://www.slf4j.org
[86]: http://maven.apache.org/plugins/maven-resources-plugin/
[27]: https://github.com/exasol/exasol-test-setup-abstraction-java
[68]: http://www.sonatype.com/public-parent/nexus-maven-plugins/nexus-staging/nexus-staging-maven-plugin/
[78]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[8]: http://www.logicng.org
[29]: https://github.com/exasol/test-db-builder-java
[6]: https://github.com/exasol/sql-statement-builder
[72]: http://anthonywhitford.com/lombok.maven/lombok-maven-plugin/
[38]: http://www.eclipse.org/legal/epl-v10.html
[70]: https://maven.apache.org/plugins/maven-dependency-plugin/
[74]: http://maven.apache.org/plugins/maven-jar-plugin/
[15]: https://projects.eclipse.org/license/epl-2.0
[34]: https://www.apache.org/licenses/LICENSE-2.0.txt
[48]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[22]: https://www.eclipse.org/legal/epl-v20.html
[88]: http://maven.apache.org/plugins/maven-install-plugin/
[52]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[62]: https://maven.apache.org/plugins/maven-gpg-plugin/
[31]: https://github.com/exasol/udf-debugging-java
[90]: http://maven.apache.org/plugins/maven-site-plugin/
[66]: https://maven.apache.org/plugins/maven-javadoc-plugin/
[0]: https://github.com/exasol/virtual-schema-common-java
[82]: https://github.com/exasol/error-code-crawler-maven-plugin
