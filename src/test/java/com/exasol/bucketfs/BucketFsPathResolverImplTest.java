package com.exasol.bucketfs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class BucketFsPathResolverImplTest {

    @Test
    void testOpenFile() {
        final String path = "/bfsdefault/default/folder/file.txt";
        final Path file = new BucketFsFileResolverImpl().openFile(path);
        assertThat(file.toAbsolutePath().toString(), equalTo("/buckets/bfsdefault/default/folder/file.txt"));
    }

    @Test
    void testInjection() {
        final String injectionPath = "/../etc/secrets.conf";
        final BucketFsFileResolver bucketfsFileFactory = new BucketFsFileResolverImpl();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bucketfsFileFactory.openFile(injectionPath));
        assertThat(exception.getMessage(), equalTo(
                "E-VSD-10: The path '/etc/secrets.conf' is outside of BucketFS. Change the path to the mapping definition file (remove ../ s)."));
    }
}
