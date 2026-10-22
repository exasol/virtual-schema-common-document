package com.exasol.adapter.document;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

class MockProjectPomTest {
    private static final Pattern JAVA_VERSION_PROPERTY = Pattern
            .compile("<java\\.version>\\s*([^<\\s]+)\\s*</java\\.version>");

    @Test
    void testMockProjectUsesSameJavaVersionAsMainProject() throws IOException {
        final String mainProjectJavaVersion = getJavaVersion(Path.of("pom.xml"));
        final String mockProjectJavaVersion = getJavaVersion(Path.of("test-project/mock-project/pom.xml"));

        assertThat(mockProjectJavaVersion, equalTo(mainProjectJavaVersion));
    }

    private String getJavaVersion(final Path pomFile) throws IOException {
        final Matcher matcher = JAVA_VERSION_PROPERTY.matcher(Files.readString(pomFile));
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("No java.version property found in " + pomFile);
    }
}
