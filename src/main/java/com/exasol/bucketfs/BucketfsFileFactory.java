package com.exasol.bucketfs;

import java.io.File;
import java.io.IOException;

import com.exasol.errorreporting.ExaError;

/**
 * Factory for files in bucketfs. Breaking out of the bucketfs using injection is prevented.
 */
public class BucketfsFileFactory {
    @SuppressWarnings("java:S1075") // this is not a configurable path
    private static final String BUCKETFS_BASIC_PATH = "/buckets";

    /**
     * Opens a file from bucketfs by a given path.
     * 
     * @param path: bucketfs path, e.g. {@code /bfsdefault/default/folder/file.txt}
     * @return File defined by the path
     * @throws IllegalArgumentException if the path is invalid or outside of the BucketFS.
     */
    public File openFile(final String path) {
        final String bucketfsPath = BUCKETFS_BASIC_PATH + path;
        final File selectedFile = new File(bucketfsPath);
        preventInjection(selectedFile);
        return selectedFile;
    }

    private void preventInjection(final File file) {
        try {
            final String absolute = file.getCanonicalPath();
            if (!absolute.startsWith(BUCKETFS_BASIC_PATH)) {
                throw new IllegalArgumentException(
                        ExaError.messageBuilder("E-VSD-10").message("The path {{PATH}} is outside of BucketFS.")
                                .parameter("PATH", file.getCanonicalPath())
                                .mitigation("Change the path to the mapping definition file (remove ../ s).")
                                .toString());

            }
        } catch (final IOException exception) {
            throw new IllegalArgumentException(ExaError.messageBuilder("E-VSD-11").message("Could not open {{PATH}}.")
                    .parameter("PATH", file.getAbsolutePath()).toString(), exception);
        }
    }
}
