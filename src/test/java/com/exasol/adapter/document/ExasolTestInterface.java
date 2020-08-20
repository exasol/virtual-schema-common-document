package com.exasol.adapter.document;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

import com.exasol.bucketfs.BucketAccessException;

/**
 * Unified interface for Exasol for different test platforms.
 */
public interface ExasolTestInterface {

    void teardown();

    /**
     * Hacky method for retrieving the host address for access from inside the docker container.
     */
    String getTestHostIpAddress();

    void uploadFileToBucketfs(Path localPath, String bucketPath)
            throws InterruptedException, BucketAccessException, TimeoutException;

    Connection getConnection() throws SQLException, IOException;
}
