package com.exasol.bucketfs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import com.exasol.errorreporting.ExaError;

/**
 * Default implementation of {@link BucketFsFileResolver}
 */
public class BucketFsFileResolverImpl implements BucketFsFileResolver {
    @SuppressWarnings("java:S1075") // this is not a configurable path
    private static final Path BUCKETFS_BASIC_PATH = Path.of("/buckets");

    @Override
    public Path openFile(final String path) {
        final Path selectedFile = BUCKETFS_BASIC_PATH.resolve(removeTrailingSlash(path));
        preventInjection(selectedFile);
        return selectedFile;
    }

    private String removeTrailingSlash(final String path) {
        if (path.startsWith("/")) {
            return path.substring(1);
        } else {
            return path;
        }
    }

    private void preventInjection(final Path path) {
        final File file = path.toFile();
        try {
            final String absolute = file.getCanonicalPath();
            if (!absolute.startsWith(BUCKETFS_BASIC_PATH.toString())) {
                throw new IllegalArgumentException(ExaError.messageBuilder("E-VSD-10")
                        .message("The path {{PATH}} is outside of BucketFS.").parameter("PATH", file.getCanonicalPath())
                        .mitigation("Change the path to the mapping definition file (remove ../ s).").toString());

            }
        } catch (final IOException exception) {
            throw new IllegalArgumentException(ExaError.messageBuilder("E-VSD-11").message("Could not open {{PATH}}.")
                    .parameter("PATH", file.getAbsolutePath()).toString(), exception);
        }
    }
}
