package com.exasol.adapter.document.documentpath;

/**
 * Visitor interface for {@link PathSegment}.
 */
public interface PathSegmentVisitor {

    /**
     * Visits a {@link ObjectLookupPathSegment}.
     * 
     * @param objectLookupPathSegment to visit
     */
    void visit(ObjectLookupPathSegment objectLookupPathSegment);

    /**
     * Visits a {@link ArrayLookupPathSegment}.
     *
     * @param arrayLookupPathSegment to visit
     */
    void visit(ArrayLookupPathSegment arrayLookupPathSegment);

    /**
     * Visits a {@link ArrayAllPathSegment}.
     *
     * @param arrayAllPathSegment to visit
     */
    void visit(ArrayAllPathSegment arrayAllPathSegment);
}
