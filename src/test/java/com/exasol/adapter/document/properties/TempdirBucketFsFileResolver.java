package com.exasol.adapter.document.properties;

import java.nio.file.Path;

import com.exasol.bucketfs.BucketFsFileResolver;

class TempdirBucketFsFileResolver implements BucketFsFileResolver {
    private final Path tempDir;

    TempdirBucketFsFileResolver(final Path tempDir) {
        this.tempDir = tempDir;
    }

    @Override
    public Path openFile(final String path) {
        return this.tempDir.resolve(path);
    }
}
