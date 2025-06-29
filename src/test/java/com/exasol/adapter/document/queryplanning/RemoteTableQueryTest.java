package com.exasol.adapter.document.queryplanning;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.exasol.adapter.document.mapping.MockPropertyToColumnMapping;
import com.exasol.adapter.document.mapping.TableMapping;
import com.exasol.adapter.document.querypredicate.NoPredicate;
import com.exasol.adapter.document.querypredicate.QueryPredicate;
import com.jparams.verifier.tostring.ToStringVerifier;

class RemoteTableQueryTest {
    @Test
    void testSetAndGetColumns() {
        final MockPropertyToColumnMapping columnDefinition = new MockPropertyToColumnMapping("", null, null);
        final TableMapping tableDefinition = TableMapping.rootTableBuilder("", "", null)
                .withColumnMappingDefinition(columnDefinition).build();
        final QueryPredicate selection = new NoPredicate();
        final RemoteTableQuery remoteTableQuery = new RemoteTableQuery(tableDefinition, List.of(columnDefinition),
                selection);
        assertAll(//
                () -> assertThat(remoteTableQuery.getSelectList(), containsInAnyOrder(columnDefinition)),
                () -> assertThat(remoteTableQuery.getFromTable(), equalTo(tableDefinition)),
                () -> assertThat(remoteTableQuery.getSelection(), equalTo(selection))//
        );
    }

    @Test
    void testToString() {
        ToStringVerifier.forClass(RemoteTableQuery.class).verify();
    }
}
