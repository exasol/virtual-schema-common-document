package com.exasol.adapter.document.mapping;

import static com.exasol.adapter.document.mapping.PropertyToColumnMappingBuilderQuickAccess.configureExampleMapping;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.exasol.adapter.document.documentnode.DocumentNode;
import com.exasol.sql.expression.NullLiteral;
import com.exasol.sql.expression.ValueExpression;

class PropertyToVarcharColumnValueExtractorTest {

    private static final String TEST_STRING = "test";
    public static final PropertyToVarcharColumnValueExtractor.MappedStringResult TEST_STRING_RESULT = new PropertyToVarcharColumnValueExtractor.MappedStringResult(
            TEST_STRING, false);

    private static PropertyToVarcharColumnMapping.Builder getDefaultMappingBuilder() {
        return configureExampleMapping(PropertyToVarcharColumnMapping.builder())//
                .varcharColumnSize(TEST_STRING.length())//
                .overflowBehaviour(TruncateableMappingErrorBehaviour.ABORT)
                .nonStringBehaviour(ConvertableMappingErrorBehaviour.CONVERT_OR_ABORT);
    }

    @Test
    void testConvertStringRowBasic() {
        final PropertyToVarcharColumnMapping toStringColumnMappingDefinition = getDefaultMappingBuilder().build();
        final ValueExpression exasolCellValue = new ToVarcharValueMapperStub(toStringColumnMappingDefinition,
                TEST_STRING_RESULT).mapValue(null);
        assertThat(exasolCellValue.toString(), equalTo(TEST_STRING));
    }

    @Test
    void testConvertRowOverflowTruncate() {
        final PropertyToVarcharColumnMapping toStringColumnMappingDefinition = getDefaultMappingBuilder()//
                .varcharColumnSize(TEST_STRING.length() - 1)//
                .overflowBehaviour(TruncateableMappingErrorBehaviour.TRUNCATE)//
                .build();
        final ValueExpression exasolCellValue = new ToVarcharValueMapperStub(toStringColumnMappingDefinition,
                TEST_STRING_RESULT).mapValue(null);
        final String expected = TEST_STRING.substring(0, TEST_STRING.length() - 1);
        assertThat(exasolCellValue.toString(), equalTo(expected));
    }

    @Test
    void testConvertRowOverflowException() {
        final PropertyToVarcharColumnMapping toStringColumnMappingDefinition = getDefaultMappingBuilder()//
                .varcharColumnSize(TEST_STRING.length() - 1)//
                .overflowBehaviour(TruncateableMappingErrorBehaviour.ABORT)//
                .build();
        final ToVarcharValueMapperStub valueMapper = new ToVarcharValueMapperStub(toStringColumnMappingDefinition,
                TEST_STRING_RESULT);
        assertThrows(OverflowException.class, () -> valueMapper.mapValue(null));
    }

    @Test
    void testCouldNotConvertWithConvertOrNullNonStringBehaviour() {
        final PropertyToVarcharColumnMapping mapping = getDefaultMappingBuilder()
                .nonStringBehaviour(ConvertableMappingErrorBehaviour.CONVERT_OR_NULL).build();
        final ToVarcharValueMapperStub valueMapper = new ToVarcharValueMapperStub(mapping, null);
        assertThat(valueMapper.mapValue(null), equalTo(NullLiteral.nullLiteral()));
    }

    @Test
    void testCouldNotConvertWithConvertOrAbortNonStringBehaviour() {
        final PropertyToVarcharColumnMapping mapping = getDefaultMappingBuilder()
                .nonStringBehaviour(ConvertableMappingErrorBehaviour.CONVERT_OR_ABORT).build();
        final ToVarcharValueMapperStub valueMapper = new ToVarcharValueMapperStub(mapping,
                new PropertyToVarcharColumnValueExtractor.CouldNotConvertResult("myType"));
        final ColumnValueExtractorException exception = assertThrows(ColumnValueExtractorException.class,
                () -> valueMapper.mapValue(null));
        assertThat(exception.getMessage(), equalTo(
                "E-VSD-37: An input value of type 'myType' for column 'EXASOL_COLUMN' could not be converted to string. Known mitigations:\n* Change the value in your input data\n* Change the nonStringBehaviour of the column to NULL or CONVERT_OR_NULL."));
    }

    @Test
    void testConvertedWithConvertOrAbortNonStringBehaviour() {
        final PropertyToVarcharColumnMapping mapping = getDefaultMappingBuilder()
                .nonStringBehaviour(ConvertableMappingErrorBehaviour.CONVERT_OR_ABORT).build();
        final ToVarcharValueMapperStub valueMapper = new ToVarcharValueMapperStub(mapping,
                new PropertyToVarcharColumnValueExtractor.MappedStringResult("123", true));
        assertThat(valueMapper.mapValue(null).toString(), equalTo("123"));
    }

    @Test
    void testConvertedWithConvertOrNullNonStringBehaviour() {
        final PropertyToVarcharColumnMapping mapping = getDefaultMappingBuilder()
                .nonStringBehaviour(ConvertableMappingErrorBehaviour.CONVERT_OR_NULL).build();
        final ToVarcharValueMapperStub valueMapper = new ToVarcharValueMapperStub(mapping,
                new PropertyToVarcharColumnValueExtractor.MappedStringResult("123", true));
        assertThat(valueMapper.mapValue(null).toString(), equalTo("123"));
    }

    @Test
    void testConvertedWithNullNonStringBehaviour() {
        final PropertyToVarcharColumnMapping mapping = getDefaultMappingBuilder()
                .nonStringBehaviour(ConvertableMappingErrorBehaviour.NULL).build();
        final ToVarcharValueMapperStub valueMapper = new ToVarcharValueMapperStub(mapping,
                new PropertyToVarcharColumnValueExtractor.MappedStringResult("123", true));
        assertThat(valueMapper.mapValue(null), equalTo(NullLiteral.nullLiteral()));
    }

    @Test
    void testConvertedWithAbortNonStringBehaviour() {
        final PropertyToVarcharColumnMapping mapping = getDefaultMappingBuilder()
                .nonStringBehaviour(ConvertableMappingErrorBehaviour.ABORT).build();
        final ToVarcharValueMapperStub valueMapper = new ToVarcharValueMapperStub(mapping,
                new PropertyToVarcharColumnValueExtractor.MappedStringResult("123", true));
        final ColumnValueExtractorException exception = assertThrows(ColumnValueExtractorException.class,
                () -> valueMapper.mapValue(null));
        assertThat(exception.getMessage(), equalTo(
                "E-VSD-36: The input value '123' is not a string. This adapter could convert it to string, but it is disabled because 'nonStringBehaviour' setting is set to ABORT. Known mitigations:\n* Set 'nonStringBehaviour' to CONVERT_OR_ABORT or CONVERT_OR_NULL.\n* Change your input data to strings."));
    }

    private static class ToVarcharValueMapperStub extends PropertyToVarcharColumnValueExtractor<Void> {
        private final ConversionResult result;

        public ToVarcharValueMapperStub(final PropertyToVarcharColumnMapping column, final ConversionResult result) {
            super(column);
            this.result = result;
        }

        @Override
        protected ConversionResult mapStringValue(final DocumentNode<Void> dynamodbProperty) {
            return this.result;
        }
    }
}
