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
| [jackson-databind][14]                                | [The Apache Software License, Version 2.0][5]                                                                  |
| [Jakarta JSON Processing API][16]                     | [Eclipse Public License 2.0][17]; [GNU General Public License, version 2 with the GNU Classpath Exception][18] |
| [Maven Project Version Getter][19]                    | [MIT][1]                                                                                                       |

## Test Dependencies

| Dependency                               | License                           |
| ---------------------------------------- | --------------------------------- |
| [Hamcrest][21]                           | [BSD License 3][22]               |
| [JUnit Jupiter Engine][23]               | [Eclipse Public License v2.0][24] |
| [JUnit Jupiter Params][23]               | [Eclipse Public License v2.0][24] |
| [mockito-junit-jupiter][27]              | [The MIT License][28]             |
| [exasol-test-setup-abstraction-java][29] | [MIT][1]                          |
| [Test Database Builder for Java][31]     | [MIT][1]                          |
| [udf-debugging-java][33]                 | [MIT][1]                          |
| [Apache Maven Verifier Component][35]    | [Apache License, Version 2.0][36] |
| [JUnit][37]                              | [Eclipse Public License 1.0][38]  |
| [Matcher for SQL Result Sets][39]        | [MIT][1]                          |
| [JaCoCo :: Agent][41]                    | [Eclipse Public License 2.0][42]  |

## Runtime Dependencies

| Dependency                    | License                                                                                                        |
| ----------------------------- | -------------------------------------------------------------------------------------------------------------- |
| [JSON-P Default Provider][16] | [Eclipse Public License 2.0][17]; [GNU General Public License, version 2 with the GNU Classpath Exception][18] |

## Plugin Dependencies

| Dependency                                              | License                                       |
| ------------------------------------------------------- | --------------------------------------------- |
| [Maven Surefire Plugin][46]                             | [Apache License, Version 2.0][36]             |
| [Maven Failsafe Plugin][48]                             | [Apache License, Version 2.0][36]             |
| [JaCoCo :: Maven Plugin][50]                            | [Eclipse Public License 2.0][42]              |
| [Apache Maven Compiler Plugin][52]                      | [Apache License, Version 2.0][36]             |
| [Maven Dependency Plugin][54]                           | [The Apache Software License, Version 2.0][5] |
| [Maven Jar Plugin][56]                                  | [The Apache Software License, Version 2.0][5] |
| [Versions Maven Plugin][58]                             | [Apache License, Version 2.0][36]             |
| [Apache Maven Source Plugin][60]                        | [Apache License, Version 2.0][36]             |
| [Apache Maven Javadoc Plugin][62]                       | [Apache License, Version 2.0][36]             |
| [Apache Maven GPG Plugin][64]                           | [Apache License, Version 2.0][5]              |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][66] | [ASL2][5]                                     |
| [Apache Maven Enforcer Plugin][68]                      | [Apache License, Version 2.0][36]             |
| [Project keeper maven plugin][70]                       | [MIT][1]                                      |
| [Maven Deploy Plugin][72]                               | [The Apache Software License, Version 2.0][5] |
| [Nexus Staging Maven Plugin][74]                        | [Eclipse Public License][38]                  |
| [error-code-crawler-maven-plugin][76]                   | [MIT][1]                                      |
| [Reproducible Build Maven Plugin][78]                   | [Apache 2.0][5]                               |
| [Lombok Maven Plugin][80]                               | [The MIT License][1]                          |
| [Maven Clean Plugin][82]                                | [The Apache Software License, Version 2.0][5] |
| [Maven Resources Plugin][84]                            | [The Apache Software License, Version 2.0][5] |
| [Maven Install Plugin][86]                              | [The Apache Software License, Version 2.0][5] |
| [Maven Site Plugin 3][88]                               | [The Apache Software License, Version 2.0][5] |

[41]: https://www.eclemma.org/jacoco/index.html
[70]: https://github.com/exasol/project-keeper-maven-plugin
[10]: https://github.com/exasol/error-reporting-java
[4]: https://github.com/everit-org/json-schema
[5]: http://www.apache.org/licenses/LICENSE-2.0.txt
[12]: https://projectlombok.org
[46]: https://maven.apache.org/surefire/maven-surefire-plugin/
[82]: http://maven.apache.org/plugins/maven-clean-plugin/
[35]: https://maven.apache.org/shared/maven-verifier/
[1]: https://opensource.org/licenses/MIT
[27]: https://github.com/mockito/mockito
[19]: https://github.com/exasol/maven-project-version-getter
[58]: http://www.mojohaus.org/versions-maven-plugin/
[22]: http://opensource.org/licenses/BSD-3-Clause
[52]: https://maven.apache.org/plugins/maven-compiler-plugin/
[64]: http://maven.apache.org/plugins/maven-gpg-plugin/
[37]: http://junit.org
[42]: https://www.eclipse.org/legal/epl-2.0/
[14]: http://github.com/FasterXML/jackson
[50]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[28]: https://github.com/mockito/mockito/blob/main/LICENSE
[13]: https://projectlombok.org/LICENSE
[39]: https://github.com/exasol/hamcrest-resultset-matcher
[78]: http://zlika.github.io/reproducible-build-maven-plugin
[3]: http://www.opensource.org/licenses/mit-license.php
[23]: https://junit.org/junit5/
[16]: https://github.com/eclipse-ee4j/jsonp
[60]: https://maven.apache.org/plugins/maven-source-plugin/
[18]: https://projects.eclipse.org/license/secondary-gpl-2.0-cp
[21]: http://hamcrest.org/JavaHamcrest/
[2]: http://www.slf4j.org
[84]: http://maven.apache.org/plugins/maven-resources-plugin/
[29]: https://github.com/exasol/exasol-test-setup-abstraction-java
[74]: http://www.sonatype.com/public-parent/nexus-maven-plugins/nexus-staging/nexus-staging-maven-plugin/
[48]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[8]: http://www.logicng.org
[31]: https://github.com/exasol/test-db-builder-java
[6]: https://github.com/exasol/sql-statement-builder
[54]: http://maven.apache.org/plugins/maven-dependency-plugin/
[80]: http://anthonywhitford.com/lombok.maven/lombok-maven-plugin/
[38]: http://www.eclipse.org/legal/epl-v10.html
[56]: http://maven.apache.org/plugins/maven-jar-plugin/
[17]: https://projects.eclipse.org/license/epl-2.0
[36]: https://www.apache.org/licenses/LICENSE-2.0.txt
[68]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[24]: https://www.eclipse.org/legal/epl-v20.html
[86]: http://maven.apache.org/plugins/maven-install-plugin/
[66]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[33]: https://github.com/exasol/udf-debugging-java
[72]: http://maven.apache.org/plugins/maven-deploy-plugin/
[88]: http://maven.apache.org/plugins/maven-site-plugin/
[62]: https://maven.apache.org/plugins/maven-javadoc-plugin/
[0]: https://github.com/exasol/virtual-schema-common-java
[76]: https://github.com/exasol/error-code-crawler-maven-plugin
