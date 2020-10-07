package com.exasol.adapter.document.queryplanning;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.exasol.adapter.document.documentpath.DocumentPathExpression;
import com.exasol.adapter.document.mapping.ColumnMapping;
import com.exasol.adapter.document.mapping.IterationIndexColumnMapping;
import com.exasol.adapter.document.mapping.PropertyToColumnMapping;
import com.exasol.adapter.document.mapping.SourceReferenceColumnMapping;

class RequiredPathExpressionExtractorTest {
    private static final RequiredPathExpressionExtractor EXTRACTOR = new RequiredPathExpressionExtractor();
    private static final DocumentPathExpression PATH = DocumentPathExpression.builder().addObjectLookup("test").build();

    @Test
    void testWithIterationIndexColumn() {
        final ColumnMapping column = new IterationIndexColumnMapping("", PATH);
        final RemoteTableQuery remoteTableQuery = mock(RemoteTableQuery.class);
        when(remoteTableQuery.getRequiredColumns()).thenReturn(List.of(column));
        assertThat(EXTRACTOR.getRequiredProperties(remoteTableQuery), containsInAnyOrder(PATH));
    }

    @Test
    void testWithPropertyMappingColumn() {
        final PropertyToColumnMapping column = spy(PropertyToColumnMapping.class);
        when(column.getPathToSourceProperty()).thenReturn(PATH);
        final RemoteTableQuery remoteTableQuery = mock(RemoteTableQuery.class);
        when(remoteTableQuery.getRequiredColumns()).thenReturn(List.of(column));
        assertThat(EXTRACTOR.getRequiredProperties(remoteTableQuery), containsInAnyOrder(PATH));
    }

    @Test
    void testWithSourcePathColumn() {
        final ColumnMapping column = new SourceReferenceColumnMapping();
        final RemoteTableQuery remoteTableQuery = mock(RemoteTableQuery.class);
        when(remoteTableQuery.getRequiredColumns()).thenReturn(List.of(column));
        assertThat(EXTRACTOR.getRequiredProperties(remoteTableQuery), empty());
    }
}