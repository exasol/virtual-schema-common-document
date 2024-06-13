package com.exasol.adapter.document.mapping.auto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import com.exasol.adapter.document.edml.ColumnNameMapping;

class ColumnNameConverterTest {

    @ParameterizedTest(name = "Name ''{0}'' converted to ''{1}''")
    @CsvSource({ //
            "'',''", //
            "' ','_'", //
            "' test', _TEST", //
            "'test ', TEST_", //
            "test, TEST", //
            "test1, TEST1", //
            "myTable1, MY_TABLE1", //
            "myTable, MY_TABLE", //
            "mytable, MYTABLE", //
            "MyTable, MY_TABLE", //
            "myCSV, MY_CSV", //
            "MyCSV, MY_CSV", //
            "my_table, MY_TABLE", //
            "MY_TABLE, MY_TABLE", //
            "my column, MY_COLUMN", //
            "a b, A_B", //
            "a  b, A__B", //
            "aB, A_B", //
            "AB, AB", //
            "aBc, A_BC", //
            "1 leading number, 1_LEADING_NUMBER" })
    void upperSnakeCaseConverter(final String input, final String expected) {
        assertThat(ColumnNameConverter.upperSnakeCaseConverter().convertColumnName(input), equalTo(expected));
    }

    @Test
    void originalNameConverterTest() {
        final ColumnNameConverter columnNameConverter = ColumnNameConverter.originalNameConverter();
        assertOriginalNameConverter(columnNameConverter);
    }

    @ParameterizedTest
    @EnumSource(ColumnNameMapping.class)
    void fromConnectionInfo(final ColumnNameMapping mapping) {
        final ColumnNameConverter columnNameConverter = ColumnNameConverter.from(mapping);
        switch (mapping) {
        case CONVERT_TO_UPPER_SNAKE_CASE:
            assertUpperSnakeCaseConverter(columnNameConverter);
            break;
        case KEEP_ORIGINAL_NAME:
            assertOriginalNameConverter(columnNameConverter);
            break;
        }
    }

    private void assertUpperSnakeCaseConverter(final ColumnNameConverter columnNameConverter) {
        assertThat(columnNameConverter.convertColumnName("columnName"), equalTo("COLUMN_NAME"));
    }

    private void assertOriginalNameConverter(final ColumnNameConverter columnNameConverter) {
        assertThat(columnNameConverter.convertColumnName("columnName"), equalTo("columnName"));
    }
}
