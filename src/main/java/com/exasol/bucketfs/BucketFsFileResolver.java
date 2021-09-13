package com.exasol.bucketfs;

import java.nio.file.Path;

/**
 * This is an interface for classes that resolve files in BucketFS.
 */
public interface BucketFsFileResolver {
    /**
     * Open a file from BucketFS by a given path.
     *
     * @param path: BucketFS path, e.g. {@code /bfsdefault/default/folder/file.txt}
     * @return File defined by the path
     * @throws IllegalArgumentException if the path is invalid or outside the BucketFS.
     */
    Path openFile(String path);
}
