<!-- @formatter:off -->
# Dependencies

## Compile Dependencies

| Dependency                                            | License                              |
| ----------------------------------------------------- | ------------------------------------ |
| [Common module of Exasol Virtual Schemas Adapters][0] | [The MIT License (MIT)][1]           |
| [SLF4J JDK14 Binding][2]                              | [MIT License][3]                     |
| [Exasol SQL Statement Builder][4]                     | [MIT License][5]                     |
| [LogicNG][6]                                          | [The Apache License, Version 2.0][7] |
| [error-reporting-java][8]                             | [MIT License][9]                     |
| [Project Lombok][10]                                  | [The MIT License][11]                |
| [EDML Java][12]                                       | [MIT License][13]                    |
| [Maven Project Version Getter][14]                    | [MIT License][15]                    |

## Test Dependencies

| Dependency                               | License                           |
| ---------------------------------------- | --------------------------------- |
| [Hamcrest][16]                           | [BSD License 3][17]               |
| [JUnit Jupiter Engine][18]               | [Eclipse Public License v2.0][19] |
| [JUnit Jupiter Params][18]               | [Eclipse Public License v2.0][19] |
| [mockito-junit-jupiter][20]              | [The MIT License][21]             |
| [exasol-test-setup-abstraction-java][22] | [MIT License][23]                 |
| [Test Database Builder for Java][24]     | [MIT License][25]                 |
| [udf-debugging-java][26]                 | [MIT License][27]                 |
| [Apache Maven Verifier Component][28]    | [Apache License, Version 2.0][29] |
| [Apache Commons IO][30]                  | [Apache License, Version 2.0][29] |
| [JUnit][31]                              | [Eclipse Public License 1.0][32]  |
| [Matcher for SQL Result Sets][33]        | [MIT License][34]                 |
| [JaCoCo :: Agent][35]                    | [Eclipse Public License 2.0][36]  |

## Plugin Dependencies

| Dependency                                              | License                                       |
| ------------------------------------------------------- | --------------------------------------------- |
| [SonarQube Scanner for Maven][37]                       | [GNU LGPL 3][38]                              |
| [Apache Maven Compiler Plugin][39]                      | [Apache License, Version 2.0][29]             |
| [Apache Maven Enforcer Plugin][40]                      | [Apache License, Version 2.0][29]             |
| [Maven Flatten Plugin][41]                              | [Apache Software Licenese][29]                |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][42] | [ASL2][7]                                     |
| [Maven Surefire Plugin][43]                             | [Apache License, Version 2.0][29]             |
| [Versions Maven Plugin][44]                             | [Apache License, Version 2.0][29]             |
| [Apache Maven Deploy Plugin][45]                        | [Apache License, Version 2.0][29]             |
| [Apache Maven GPG Plugin][46]                           | [Apache License, Version 2.0][29]             |
| [Apache Maven Source Plugin][47]                        | [Apache License, Version 2.0][29]             |
| [Apache Maven Javadoc Plugin][48]                       | [Apache License, Version 2.0][29]             |
| [Nexus Staging Maven Plugin][49]                        | [Eclipse Public License][32]                  |
| [Apache Maven Dependency Plugin][50]                    | [Apache License, Version 2.0][29]             |
| [Lombok Maven Plugin][51]                               | [The MIT License][52]                         |
| [Maven Jar Plugin][53]                                  | [The Apache Software License, Version 2.0][7] |
| [Project keeper maven plugin][54]                       | [The MIT License][55]                         |
| [Maven Failsafe Plugin][56]                             | [Apache License, Version 2.0][29]             |
| [JaCoCo :: Maven Plugin][57]                            | [Eclipse Public License 2.0][36]              |
| [error-code-crawler-maven-plugin][58]                   | [MIT License][59]                             |
| [Reproducible Build Maven Plugin][60]                   | [Apache 2.0][7]                               |
| [Maven Clean Plugin][61]                                | [The Apache Software License, Version 2.0][7] |
| [Maven Resources Plugin][62]                            | [The Apache Software License, Version 2.0][7] |
| [Maven Install Plugin][63]                              | [The Apache Software License, Version 2.0][7] |
| [Maven Site Plugin 3][64]                               | [The Apache Software License, Version 2.0][7] |

[0]: https://github.com/exasol/virtual-schema-common-java/
[1]: https://github.com/exasol/virtual-schema-common-java/blob/main/LICENSE
[2]: http://www.slf4j.org
[3]: http://www.opensource.org/licenses/mit-license.php
[4]: https://github.com/exasol/sql-statement-builder/
[5]: https://github.com/exasol/sql-statement-builder/blob/main/LICENSE
[6]: http://www.logicng.org
[7]: http://www.apache.org/licenses/LICENSE-2.0.txt
[8]: https://github.com/exasol/error-reporting-java/
[9]: https://github.com/exasol/error-reporting-java/blob/main/LICENSE
[10]: https://projectlombok.org
[11]: https://projectlombok.org/LICENSE
[12]: https://github.com/exasol/edml-java/
[13]: https://github.com/exasol/edml-java/blob/main/LICENSE
[14]: https://github.com/exasol/maven-project-version-getter/
[15]: https://github.com/exasol/maven-project-version-getter/blob/main/LICENSE
[16]: http://hamcrest.org/JavaHamcrest/
[17]: http://opensource.org/licenses/BSD-3-Clause
[18]: https://junit.org/junit5/
[19]: https://www.eclipse.org/legal/epl-v20.html
[20]: https://github.com/mockito/mockito
[21]: https://github.com/mockito/mockito/blob/main/LICENSE
[22]: https://github.com/exasol/exasol-test-setup-abstraction-java/
[23]: https://github.com/exasol/exasol-test-setup-abstraction-java/blob/main/LICENSE
[24]: https://github.com/exasol/test-db-builder-java/
[25]: https://github.com/exasol/test-db-builder-java/blob/main/LICENSE
[26]: https://github.com/exasol/udf-debugging-java/
[27]: https://github.com/exasol/udf-debugging-java/blob/main/LICENSE
[28]: https://maven.apache.org/shared/maven-verifier/
[29]: https://www.apache.org/licenses/LICENSE-2.0.txt
[30]: https://commons.apache.org/proper/commons-io/
[31]: http://junit.org
[32]: http://www.eclipse.org/legal/epl-v10.html
[33]: https://github.com/exasol/hamcrest-resultset-matcher/
[34]: https://github.com/exasol/hamcrest-resultset-matcher/blob/main/LICENSE
[35]: https://www.eclemma.org/jacoco/index.html
[36]: https://www.eclipse.org/legal/epl-2.0/
[37]: http://sonarsource.github.io/sonar-scanner-maven/
[38]: https://www.gnu.org/licenses/lgpl-3.0.txt
[39]: https://maven.apache.org/plugins/maven-compiler-plugin/
[40]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[41]: https://www.mojohaus.org/flatten-maven-plugin/
[42]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[43]: https://maven.apache.org/surefire/maven-surefire-plugin/
[44]: https://www.mojohaus.org/versions/versions-maven-plugin/
[45]: https://maven.apache.org/plugins/maven-deploy-plugin/
[46]: https://maven.apache.org/plugins/maven-gpg-plugin/
[47]: https://maven.apache.org/plugins/maven-source-plugin/
[48]: https://maven.apache.org/plugins/maven-javadoc-plugin/
[49]: http://www.sonatype.com/public-parent/nexus-maven-plugins/nexus-staging/nexus-staging-maven-plugin/
[50]: https://maven.apache.org/plugins/maven-dependency-plugin/
[51]: http://anthonywhitford.com/lombok.maven/lombok-maven-plugin/
[52]: https://opensource.org/licenses/MIT
[53]: http://maven.apache.org/plugins/maven-jar-plugin/
[54]: https://github.com/exasol/project-keeper/
[55]: https://github.com/exasol/project-keeper/blob/main/LICENSE
[56]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[57]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[58]: https://github.com/exasol/error-code-crawler-maven-plugin/
[59]: https://github.com/exasol/error-code-crawler-maven-plugin/blob/main/LICENSE
[60]: http://zlika.github.io/reproducible-build-maven-plugin
[61]: http://maven.apache.org/plugins/maven-clean-plugin/
[62]: http://maven.apache.org/plugins/maven-resources-plugin/
[63]: http://maven.apache.org/plugins/maven-install-plugin/
[64]: http://maven.apache.org/plugins/maven-site-plugin/
