package com.exasol.adapter.document.mapping;

import static com.exasol.adapter.document.mapping.PropertyToColumnMappingBuilderQuickAccess.getColumnMappingExample;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.exasol.adapter.document.documentpath.DocumentPathExpression;
import org.junit.jupiter.api.Test;

class TableMappingTest {
    @Test
    void additionalPropertiesReturnedTest() {
        final String testValue = "testAdditionalConfig";
        final TableMapping table = TableMapping.rootTableBuilder("", "", testValue).build();
        assertThat(table.getAdditionalConfiguration(), equalTo(testValue));
    }
    @Test
    void additionalPropertiesReturnedNestedTableBuilderTest() {
        final String testValue = "testAdditionalConfig";
        final TableMapping table = TableMapping.nestedTableBuilder("", "",DocumentPathExpression.builder().build(), testValue).build();
        assertThat(table.getAdditionalConfiguration(), equalTo(testValue));
    }
}
