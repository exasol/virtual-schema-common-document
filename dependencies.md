<!-- @formatter:off -->
# Dependencies

## Compile Dependencies

| Dependency                                            | License                              |
| ----------------------------------------------------- | ------------------------------------ |
| [Common module of Exasol Virtual Schemas Adapters][0] | [MIT][1]                             |
| [SLF4J JDK14 Binding][2]                              | [MIT License][3]                     |
| [Exasol SQL Statement Builder][4]                     | [MIT][1]                             |
| [LogicNG][6]                                          | [The Apache License, Version 2.0][7] |
| [error-reporting-java][8]                             | [MIT][1]                             |
| [Project Lombok][10]                                  | [The MIT License][11]                |
| [EDML Java][12]                                       | [MIT][1]                             |
| [Maven Project Version Getter][14]                    | [MIT][1]                             |

## Test Dependencies

| Dependency                               | License                           |
| ---------------------------------------- | --------------------------------- |
| [Hamcrest][16]                           | [BSD License 3][17]               |
| [JUnit Jupiter Engine][18]               | [Eclipse Public License v2.0][19] |
| [JUnit Jupiter Params][18]               | [Eclipse Public License v2.0][19] |
| [mockito-junit-jupiter][22]              | [The MIT License][23]             |
| [exasol-test-setup-abstraction-java][24] | [MIT][1]                          |
| [Test Database Builder for Java][26]     | [MIT License][27]                 |
| [udf-debugging-java][28]                 | [MIT][1]                          |
| [Apache Maven Verifier Component][30]    | [Apache License, Version 2.0][31] |
| [Apache Commons IO][32]                  | [Apache License, Version 2.0][31] |
| [JUnit][34]                              | [Eclipse Public License 1.0][35]  |
| [Matcher for SQL Result Sets][36]        | [MIT][1]                          |
| [JaCoCo :: Agent][38]                    | [Eclipse Public License 2.0][39]  |

## Plugin Dependencies

| Dependency                                              | License                                       |
| ------------------------------------------------------- | --------------------------------------------- |
| [Apache Maven Compiler Plugin][40]                      | [Apache License, Version 2.0][31]             |
| [Apache Maven Enforcer Plugin][42]                      | [Apache License, Version 2.0][31]             |
| [Maven Flatten Plugin][44]                              | [Apache Software Licenese][7]                 |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][46] | [ASL2][7]                                     |
| [Reproducible Build Maven Plugin][48]                   | [Apache 2.0][7]                               |
| [Maven Surefire Plugin][50]                             | [Apache License, Version 2.0][31]             |
| [Versions Maven Plugin][52]                             | [Apache License, Version 2.0][31]             |
| [Apache Maven Deploy Plugin][54]                        | [Apache License, Version 2.0][31]             |
| [Apache Maven GPG Plugin][56]                           | [Apache License, Version 2.0][31]             |
| [Apache Maven Source Plugin][58]                        | [Apache License, Version 2.0][31]             |
| [Apache Maven Javadoc Plugin][60]                       | [Apache License, Version 2.0][31]             |
| [Nexus Staging Maven Plugin][62]                        | [Eclipse Public License][35]                  |
| [Apache Maven Dependency Plugin][64]                    | [Apache License, Version 2.0][31]             |
| [Lombok Maven Plugin][66]                               | [The MIT License][1]                          |
| [Maven Jar Plugin][68]                                  | [The Apache Software License, Version 2.0][7] |
| [Project keeper maven plugin][70]                       | [MIT][1]                                      |
| [Maven Failsafe Plugin][72]                             | [Apache License, Version 2.0][31]             |
| [JaCoCo :: Maven Plugin][74]                            | [Eclipse Public License 2.0][39]              |
| [error-code-crawler-maven-plugin][76]                   | [MIT][1]                                      |
| [Maven Clean Plugin][78]                                | [The Apache Software License, Version 2.0][7] |
| [Maven Resources Plugin][80]                            | [The Apache Software License, Version 2.0][7] |
| [Maven Install Plugin][82]                              | [The Apache Software License, Version 2.0][7] |
| [Maven Site Plugin 3][84]                               | [The Apache Software License, Version 2.0][7] |

[38]: https://www.eclemma.org/jacoco/index.html
[8]: https://github.com/exasol/error-reporting-java
[7]: http://www.apache.org/licenses/LICENSE-2.0.txt
[10]: https://projectlombok.org
[50]: https://maven.apache.org/surefire/maven-surefire-plugin/
[78]: http://maven.apache.org/plugins/maven-clean-plugin/
[30]: https://maven.apache.org/shared/maven-verifier/
[1]: https://opensource.org/licenses/MIT
[22]: https://github.com/mockito/mockito
[14]: https://github.com/exasol/maven-project-version-getter
[52]: http://www.mojohaus.org/versions-maven-plugin/
[17]: http://opensource.org/licenses/BSD-3-Clause
[40]: https://maven.apache.org/plugins/maven-compiler-plugin/
[27]: https://github.com/exasol/test-db-builder-java/blob/main/LICENSE
[34]: http://junit.org
[39]: https://www.eclipse.org/legal/epl-2.0/
[54]: https://maven.apache.org/plugins/maven-deploy-plugin/
[74]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[23]: https://github.com/mockito/mockito/blob/main/LICENSE
[11]: https://projectlombok.org/LICENSE
[32]: https://commons.apache.org/proper/commons-io/
[36]: https://github.com/exasol/hamcrest-resultset-matcher
[48]: http://zlika.github.io/reproducible-build-maven-plugin
[3]: http://www.opensource.org/licenses/mit-license.php
[70]: https://github.com/exasol/project-keeper-maven-plugin/project-keeper-maven-plugin-generated-parent/project-keeper-maven-plugin
[18]: https://junit.org/junit5/
[44]: https://www.mojohaus.org/flatten-maven-plugin/flatten-maven-plugin
[58]: https://maven.apache.org/plugins/maven-source-plugin/
[16]: http://hamcrest.org/JavaHamcrest/
[2]: http://www.slf4j.org
[80]: http://maven.apache.org/plugins/maven-resources-plugin/
[24]: https://github.com/exasol/exasol-test-setup-abstraction-java
[26]: https://github.com/exasol/test-db-builder-java/
[62]: http://www.sonatype.com/public-parent/nexus-maven-plugins/nexus-staging/nexus-staging-maven-plugin/
[72]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[6]: http://www.logicng.org
[4]: https://github.com/exasol/sql-statement-builder
[66]: http://anthonywhitford.com/lombok.maven/lombok-maven-plugin/
[35]: http://www.eclipse.org/legal/epl-v10.html
[64]: https://maven.apache.org/plugins/maven-dependency-plugin/
[68]: http://maven.apache.org/plugins/maven-jar-plugin/
[31]: https://www.apache.org/licenses/LICENSE-2.0.txt
[12]: https://github.com/exasol/edml-java
[42]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[19]: https://www.eclipse.org/legal/epl-v20.html
[82]: http://maven.apache.org/plugins/maven-install-plugin/
[46]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[56]: https://maven.apache.org/plugins/maven-gpg-plugin/
[28]: https://github.com/exasol/udf-debugging-java
[84]: http://maven.apache.org/plugins/maven-site-plugin/
[60]: https://maven.apache.org/plugins/maven-javadoc-plugin/
[0]: https://github.com/exasol/virtual-schema-common-java
[76]: https://github.com/exasol/error-code-crawler-maven-plugin
