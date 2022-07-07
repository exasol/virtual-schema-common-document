package com.exasol.adapter.document.mapping;

import static com.exasol.adapter.document.mapping.PropertyToColumnMappingBuilderQuickAccess.getColumnMappingExample;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

public class TableMappingTest {
    @Test
    void additionalPropertiesReturnedTest() {
        final String testValue = "testAdditionalConfig";
        final TableMapping table = TableMapping.rootTableBuilder("", "", testValue).build();
        assertThat(table.getAdditionalConfiguration(), equalTo(testValue));
    }
}
