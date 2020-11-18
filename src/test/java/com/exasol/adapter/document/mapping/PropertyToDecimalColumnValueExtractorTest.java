package com.exasol.adapter.document.mapping;

import static com.exasol.adapter.document.mapping.PropertyToColumnMappingBuilderQuickAccess.configureExampleMapping;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.exasol.adapter.document.documentnode.DocumentNode;
import com.exasol.sql.expression.BigDecimalLiteral;
import com.exasol.sql.expression.NullLiteral;
import com.exasol.sql.expression.ValueExpression;

class PropertyToDecimalColumnValueExtractorTest {

    private static final PropertyToDecimalColumnMapping ABORT_MAPPING = commonMappingBuilder()//
            .overflowBehaviour(MappingErrorBehaviour.ABORT)//
            .notNumericBehaviour(MappingErrorBehaviour.ABORT)//
            .build();
    private static final PropertyToDecimalColumnMapping NULL_MAPPING = commonMappingBuilder()//
            .overflowBehaviour(MappingErrorBehaviour.NULL)//
            .notNumericBehaviour(MappingErrorBehaviour.NULL)//
            .build();

    private static PropertyToDecimalColumnMapping.Builder commonMappingBuilder() {
        return configureExampleMapping(PropertyToDecimalColumnMapping.builder())//
                .decimalPrecision(2)//
                .decimalScale(0);
    }

    @Test
    void testConvert() {
        final BigDecimal bigDecimalValue = BigDecimal.valueOf(10);
        final ToDecimalExtractorStub extractor = new ToDecimalExtractorStub(ABORT_MAPPING,
                new PropertyToDecimalColumnValueExtractor.ConvertedResult(bigDecimalValue));
        final BigDecimalLiteral result = (BigDecimalLiteral) extractor.mapValue(null);
        assertThat(result.getValue(), equalTo(BigDecimal.valueOf(10)));
    }

    @Test
    void testOverflowException() {
        final BigDecimal bigDecimalValue = BigDecimal.valueOf(100);
        final ToDecimalExtractorStub extractor = new ToDecimalExtractorStub(ABORT_MAPPING,
                new PropertyToDecimalColumnValueExtractor.ConvertedResult(bigDecimalValue));
        final OverflowException exception = assertThrows(OverflowException.class, () -> extractor.mapValue(null));
        assertThat(exception.getMessage(), equalTo(
                "E-VSD-34: An input value exceeded the size of the DECIMAL column 'EXASOL_COLUMN'. Known mitigations:\n* Increase the decimalPrecision of this column in your mapping definition.\n* Set the overflow behaviour to NULL."));
    }

    @Test
    void testOverflowNull() {
        final ToDecimalExtractorStub extractor = new ToDecimalExtractorStub(NULL_MAPPING,
                new PropertyToDecimalColumnValueExtractor.ConvertedResult(BigDecimal.valueOf(100)));
        final ValueExpression valueExpression = extractor.mapValue(null);
        assertThat(valueExpression, instanceOf(NullLiteral.class));
    }

    @Test
    void testNaNHandlingException() {
        final ToDecimalExtractorStub extractor = new ToDecimalExtractorStub(ABORT_MAPPING,
                new PropertyToDecimalColumnValueExtractor.NotNumericResult("test"));
        final ColumnValueExtractorException exception = assertThrows(ColumnValueExtractorException.class,
                () -> extractor.mapValue(null));
        assertThat(exception.getMessage(), equalTo(
                "E-VSD-33: Could not convert 'test' to decimal column ('EXASOL_COLUMN'). Known mitigations:\n* Try using a different mapping.\n* Ignore this error by setting 'notNumericBehaviour' to 'null'."));
    }

    @Test
    void testNaNHandlingNull() {
        final ToDecimalExtractorStub extractor = new ToDecimalExtractorStub(NULL_MAPPING,
                new PropertyToDecimalColumnValueExtractor.NotNumericResult("test"));
        final ValueExpression valueExpression = extractor.mapValue(null);
        assertThat(valueExpression, instanceOf(NullLiteral.class));
    }

    private static class ToDecimalExtractorStub extends PropertyToDecimalColumnValueExtractor<Object> {
        private final ConversionResult result;

        public ToDecimalExtractorStub(final PropertyToDecimalColumnMapping column, final ConversionResult result) {
            super(column);
            this.result = result;
        }

        @Override
        protected ConversionResult mapValueToDecimal(final DocumentNode<Object> documentValue) {
            return this.result;
        }
    }
}