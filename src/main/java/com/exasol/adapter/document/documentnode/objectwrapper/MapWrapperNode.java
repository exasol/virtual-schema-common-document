package com.exasol.adapter.document.documentnode.objectwrapper;

import java.util.Map;
import java.util.stream.Collectors;

import com.exasol.adapter.document.documentnode.DocumentNode;
import com.exasol.adapter.document.documentnode.DocumentObject;

/**
 * {@link DocumentNode} wrapping a java map.
 */
class MapWrapperNode implements DocumentObject {
    /** @serial */
    private final Map<String, Object> values;

    /**
     * Create a new instance of {@link MapWrapperNode}.
     *
     * @param values java map to wrap
     */
    MapWrapperNode(final Map<String, Object> values) {
        this.values = values;
    }

    @Override
    public Map<String, DocumentNode> getKeyValueMap() {
        return this.values.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                entry -> ObjectWrapperDocumentNodeFactory.getNodeFor(entry.getValue())));
    }

    @Override
    public DocumentNode get(final String key) {
        return ObjectWrapperDocumentNodeFactory.getNodeFor(this.values.get(key));
    }

    @Override
    public boolean hasKey(final String key) {
        return this.values.containsKey(key);
    }
}
