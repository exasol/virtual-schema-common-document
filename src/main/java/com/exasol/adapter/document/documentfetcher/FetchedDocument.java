package com.exasol.adapter.document.documentfetcher;

import com.exasol.adapter.document.documentnode.DocumentNode;

/**
 * This class groups the fetched {@link DocumentNode} additional metadata.
 */
public class FetchedDocument {
    private final DocumentNode rootDocumentNode;
    private final String sourcePath;

    /**
     * Create an instance of {@link FetchedDocument}.
     * 
     * @param rootDocumentNode fetched document
     * @param sourcePath       path / name of the document's source.
     */
    public FetchedDocument(final DocumentNode rootDocumentNode, final String sourcePath) {
        this.rootDocumentNode = rootDocumentNode;
        this.sourcePath = sourcePath;
    }

    /**
     * Get the fetched {@link DocumentNode}.
     * 
     * @return fetched {@link DocumentNode}
     */
    public DocumentNode getRootDocumentNode() {
        return this.rootDocumentNode;
    }

    /**
     * Get the path or name of the document's source.
     * 
     * @return path or name of the document's source
     */
    public String getSourcePath() {
        return this.sourcePath;
    }
}
