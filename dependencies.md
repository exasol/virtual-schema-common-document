<!-- @formatter:off -->
# Dependencies

## Compile Dependencies

| Dependency                                            | License                              |
| ----------------------------------------------------- | ------------------------------------ |
| [Common module of Exasol Virtual Schemas Adapters][0] | [The MIT License (MIT)][1]           |
| [Exasol SQL Statement Builder][2]                     | [MIT License][3]                     |
| [LogicNG][4]                                          | [The Apache License, Version 2.0][5] |
| [error-reporting-java][6]                             | [MIT License][7]                     |
| [Project Lombok][8]                                   | [The MIT License][9]                 |
| edml-java                                             |                                      |
| [Maven Project Version Getter][10]                    | [MIT License][11]                    |

## Test Dependencies

| Dependency                                 | License                           |
| ------------------------------------------ | --------------------------------- |
| [Hamcrest][12]                             | [BSD License 3][13]               |
| [JUnit Jupiter Params][14]                 | [Eclipse Public License v2.0][15] |
| [mockito-junit-jupiter][16]                | [MIT][17]                         |
| [SLF4J JDK14 Provider][18]                 | [MIT License][19]                 |
| [exasol-test-setup-abstraction-java][20]   | [MIT License][21]                 |
| [Test Database Builder for Java][22]       | [MIT License][23]                 |
| [udf-debugging-java][24]                   | [MIT License][25]                 |
| [Apache Maven Verifier Component][26]      | [Apache License, Version 2.0][27] |
| [Apache Commons IO][28]                    | [Apache-2.0][27]                  |
| [Matcher for SQL Result Sets][29]          | [MIT License][30]                 |
| [EqualsVerifier \| release normal jar][31] | [Apache License, Version 2.0][27] |
| [to-string-verifier][32]                   | [MIT License][19]                 |
| [JaCoCo :: Agent][33]                      | [Eclipse Public License 2.0][34]  |

## Plugin Dependencies

| Dependency                                              | License                           |
| ------------------------------------------------------- | --------------------------------- |
| [SonarQube Scanner for Maven][35]                       | [GNU LGPL 3][36]                  |
| [Apache Maven Compiler Plugin][37]                      | [Apache-2.0][27]                  |
| [Apache Maven Enforcer Plugin][38]                      | [Apache-2.0][27]                  |
| [Maven Flatten Plugin][39]                              | [Apache Software Licenese][27]    |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][40] | [ASL2][5]                         |
| [Maven Surefire Plugin][41]                             | [Apache-2.0][27]                  |
| [Versions Maven Plugin][42]                             | [Apache License, Version 2.0][27] |
| [duplicate-finder-maven-plugin Maven Mojo][43]          | [Apache License 2.0][44]          |
| [Apache Maven Deploy Plugin][45]                        | [Apache-2.0][27]                  |
| [Apache Maven GPG Plugin][46]                           | [Apache-2.0][27]                  |
| [Apache Maven Source Plugin][47]                        | [Apache License, Version 2.0][27] |
| [Apache Maven Javadoc Plugin][48]                       | [Apache-2.0][27]                  |
| [Nexus Staging Maven Plugin][49]                        | [Eclipse Public License][50]      |
| [Apache Maven Dependency Plugin][51]                    | [Apache-2.0][27]                  |
| [Apache Maven JAR Plugin][52]                           | [Apache License, Version 2.0][27] |
| [Project Keeper Maven plugin][53]                       | [The MIT License][54]             |
| [Maven Failsafe Plugin][55]                             | [Apache-2.0][27]                  |
| [JaCoCo :: Maven Plugin][56]                            | [Eclipse Public License 2.0][34]  |
| [error-code-crawler-maven-plugin][57]                   | [MIT License][58]                 |
| [Reproducible Build Maven Plugin][59]                   | [Apache 2.0][5]                   |

[0]: https://github.com/exasol/virtual-schema-common-java/
[1]: https://github.com/exasol/virtual-schema-common-java/blob/main/LICENSE
[2]: https://github.com/exasol/sql-statement-builder/
[3]: https://github.com/exasol/sql-statement-builder/blob/main/LICENSE
[4]: http://www.logicng.org
[5]: http://www.apache.org/licenses/LICENSE-2.0.txt
[6]: https://github.com/exasol/error-reporting-java/
[7]: https://github.com/exasol/error-reporting-java/blob/main/LICENSE
[8]: https://projectlombok.org
[9]: https://projectlombok.org/LICENSE
[10]: https://github.com/exasol/maven-project-version-getter/
[11]: https://github.com/exasol/maven-project-version-getter/blob/main/LICENSE
[12]: http://hamcrest.org/JavaHamcrest/
[13]: http://opensource.org/licenses/BSD-3-Clause
[14]: https://junit.org/junit5/
[15]: https://www.eclipse.org/legal/epl-v20.html
[16]: https://github.com/mockito/mockito
[17]: https://opensource.org/licenses/MIT
[18]: http://www.slf4j.org
[19]: http://www.opensource.org/licenses/mit-license.php
[20]: https://github.com/exasol/exasol-test-setup-abstraction-java/
[21]: https://github.com/exasol/exasol-test-setup-abstraction-java/blob/main/LICENSE
[22]: https://github.com/exasol/test-db-builder-java/
[23]: https://github.com/exasol/test-db-builder-java/blob/main/LICENSE
[24]: https://github.com/exasol/udf-debugging-java/
[25]: https://github.com/exasol/udf-debugging-java/blob/main/LICENSE
[26]: https://maven.apache.org/shared/maven-verifier/
[27]: https://www.apache.org/licenses/LICENSE-2.0.txt
[28]: https://commons.apache.org/proper/commons-io/
[29]: https://github.com/exasol/hamcrest-resultset-matcher/
[30]: https://github.com/exasol/hamcrest-resultset-matcher/blob/main/LICENSE
[31]: https://www.jqno.nl/equalsverifier
[32]: https://github.com/jparams/to-string-verifier
[33]: https://www.eclemma.org/jacoco/index.html
[34]: https://www.eclipse.org/legal/epl-2.0/
[35]: http://sonarsource.github.io/sonar-scanner-maven/
[36]: http://www.gnu.org/licenses/lgpl.txt
[37]: https://maven.apache.org/plugins/maven-compiler-plugin/
[38]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[39]: https://www.mojohaus.org/flatten-maven-plugin/
[40]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[41]: https://maven.apache.org/surefire/maven-surefire-plugin/
[42]: https://www.mojohaus.org/versions/versions-maven-plugin/
[43]: https://basepom.github.io/duplicate-finder-maven-plugin
[44]: http://www.apache.org/licenses/LICENSE-2.0.html
[45]: https://maven.apache.org/plugins/maven-deploy-plugin/
[46]: https://maven.apache.org/plugins/maven-gpg-plugin/
[47]: https://maven.apache.org/plugins/maven-source-plugin/
[48]: https://maven.apache.org/plugins/maven-javadoc-plugin/
[49]: http://www.sonatype.com/public-parent/nexus-maven-plugins/nexus-staging/nexus-staging-maven-plugin/
[50]: http://www.eclipse.org/legal/epl-v10.html
[51]: https://maven.apache.org/plugins/maven-dependency-plugin/
[52]: https://maven.apache.org/plugins/maven-jar-plugin/
[53]: https://github.com/exasol/project-keeper/
[54]: https://github.com/exasol/project-keeper/blob/main/LICENSE
[55]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[56]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[57]: https://github.com/exasol/error-code-crawler-maven-plugin/
[58]: https://github.com/exasol/error-code-crawler-maven-plugin/blob/main/LICENSE
[59]: http://zlika.github.io/reproducible-build-maven-plugin
