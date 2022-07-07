package com.exasol.adapter.document.mapping.converter;

import java.util.List;
import java.util.Optional;

import com.exasol.adapter.document.documentpath.DocumentPathExpression;
import com.exasol.adapter.document.documentpath.ObjectLookupPathSegment;
import com.exasol.adapter.document.documentpath.PathSegment;

/**
 * This class finds the name of the last property in a path.
 */
class LastPropertyNameFinder {
    /**
     * Get the name of the last property in a path.
     * <p>
     * example: test[1].address.street --> street
     * </p>
     * 
     * @param path path to analyze
     * @return last property in the path
     */
    Optional<String> getLastPropertyName(final DocumentPathExpression path) {
        final List<PathSegment> segments = path.getSegments();
        if (segments.isEmpty()) {
            return Optional.empty();
        }
        final PathSegment lastElement = segments.get(segments.size() - 1);
        if (!(lastElement instanceof ObjectLookupPathSegment)) {
            return Optional.empty();
        }
        final ObjectLookupPathSegment lookup = (ObjectLookupPathSegment) lastElement;
        return Optional.of(lookup.getLookupKey());
    }
}
