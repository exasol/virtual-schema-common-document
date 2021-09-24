package com.exasol.adapter.document.mapping;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.any;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.exasol.adapter.document.documentfetcher.FetchedDocument;
import com.exasol.adapter.document.documentnode.holder.ObjectHolderNode;
import com.exasol.adapter.document.documentnode.holder.StringHolderNode;
import com.exasol.adapter.document.documentpath.DocumentPathExpression;
import com.exasol.adapter.document.documentpath.StaticDocumentPathIterator;

class AbstractPropertyToColumnValueExtractorTest {

    public static final String KEY = "isbn";
    public static final StringHolderNode EXPECTED_VALUE = new StringHolderNode("testValue");
    private static final FetchedDocument STUB_DOCUMENT = new FetchedDocument(
            new ObjectHolderNode(Map.of(KEY, EXPECTED_VALUE)), "test source");

    @Test
    void testLookup() {
        final DocumentPathExpression sourcePath = DocumentPathExpression.builder().addObjectLookup(KEY).build();
        final MockPropertyToColumnMapping columnMappingDefinition = new MockPropertyToColumnMapping("d", sourcePath,
                MappingErrorBehaviour.ABORT);
        final AbstractPropertyToColumnValueExtractor extractor = getMock(columnMappingDefinition);
        extractor.extractColumnValue(STUB_DOCUMENT, new StaticDocumentPathIterator());
        verify(extractor).mapValue(EXPECTED_VALUE);
    }

    @Test
    void testNullMappingErrorBehaviour() throws ColumnValueExtractorException {
        final DocumentPathExpression sourcePath = DocumentPathExpression.builder().addObjectLookup("nonExistingColumn")
                .build();
        final MockPropertyToColumnMapping columnMappingDefinition = new MockPropertyToColumnMapping("d", sourcePath,
                MappingErrorBehaviour.NULL);
        final AbstractPropertyToColumnValueExtractor extractor = getMock(columnMappingDefinition);
        final Object result = extractor.extractColumnValue(STUB_DOCUMENT, new StaticDocumentPathIterator());
        assertThat(result, is(nullValue()));
    }

    @Test
    void testExceptionMappingErrorBehaviour() {
        final DocumentPathExpression sourcePath = DocumentPathExpression.builder().addObjectLookup("nonExistingColumn")
                .build();
        final MockPropertyToColumnMapping columnMappingDefinition = new MockPropertyToColumnMapping("d", sourcePath,
                MappingErrorBehaviour.ABORT);
        final AbstractPropertyToColumnValueExtractor extractor = getMock(columnMappingDefinition);
        final StaticDocumentPathIterator pathIterator = new StaticDocumentPathIterator();
        assertThrows(SchemaMappingException.class, () -> extractor.extractColumnValue(STUB_DOCUMENT, pathIterator));
    }

    @Test
    void testColumnMappingException() {
        final String columnName = "name";
        final MockPropertyToColumnMapping mappingDefinition = new MockPropertyToColumnMapping(columnName,
                DocumentPathExpression.empty(), MappingErrorBehaviour.ABORT);
        final AbstractPropertyToColumnValueExtractor extractor = getMock(mappingDefinition);
        when(extractor.mapValue(any())).thenThrow(new ColumnValueExtractorException("mocMessage", mappingDefinition));
        final StaticDocumentPathIterator pathIterator = new StaticDocumentPathIterator();
        final ColumnValueExtractorException exception = assertThrows(ColumnValueExtractorException.class,
                () -> extractor.extractColumnValue(STUB_DOCUMENT, pathIterator));
        assertThat(exception.getCausingColumn().getExasolColumnName(), equalTo(columnName));
    }

    private AbstractPropertyToColumnValueExtractor getMock(final MockPropertyToColumnMapping columnMappingDefinition) {
        return mock(AbstractPropertyToColumnValueExtractor.class, Mockito.withSettings()
                .useConstructor(columnMappingDefinition).defaultAnswer(Mockito.CALLS_REAL_METHODS));
    }
}
