package com.exasol.adapter.document.documentnode.objectwrapper;

import java.util.List;
import java.util.stream.Collectors;

import com.exasol.adapter.document.documentnode.DocumentArray;
import com.exasol.adapter.document.documentnode.DocumentNode;

/**
 * This class wraps list of java objects as {@link DocumentNode}.
 */
class ListWrapperNode implements DocumentArray {
    private static final long serialVersionUID = -2400489519576753876L;
    /** @serial */
    private final List<Object> values;

    ListWrapperNode(final List<Object> values) {
        this.values = values;
    }

    @Override
    public List<? extends DocumentNode> getValuesList() {
        return this.values.stream().map(ObjectWrapperDocumentNodeFactory::getNodeFor).collect(Collectors.toList());
    }

    @Override
    public DocumentNode getValue(final int index) {
        return ObjectWrapperDocumentNodeFactory.getNodeFor(this.values.get(index));
    }

    @Override
    public int size() {
        return this.values.size();
    }
}
