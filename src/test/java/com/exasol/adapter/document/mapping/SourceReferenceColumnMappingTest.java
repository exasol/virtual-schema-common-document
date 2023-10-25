package com.exasol.adapter.document.mapping;

import static com.exasol.EqualityMatchers.assertSymmetricEqualWithHashAndEquals;
import static com.exasol.EqualityMatchers.assertSymmetricNotEqualWithHashAndEquals;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import com.jparams.verifier.tostring.ToStringVerifier;

import nl.jqno.equalsverifier.EqualsVerifier;

class SourceReferenceColumnMappingTest {

    private static final SourceReferenceColumnMapping TEST_OBJECT = new SourceReferenceColumnMapping();

    @Test
    void testGetExasolDataType() {
        assertThat(TEST_OBJECT.getExasolDataType().toString(), equalTo("VARCHAR(2000) UTF8"));
    }

    @Test
    void testIsExasolColumnNullable() {
        assertThat(TEST_OBJECT.isExasolColumnNullable(), equalTo(false));
    }

    @Test
    void testWithNewExasolName() {
        assertThat(TEST_OBJECT.withNewExasolName("otherName").getExasolColumnName(), equalTo("otherName"));
    }

    @Test
    void testIdentical() {
        assertSymmetricEqualWithHashAndEquals(TEST_OBJECT, TEST_OBJECT);
    }

    @Test
    void testSame() {
        assertSymmetricEqualWithHashAndEquals(TEST_OBJECT,
                TEST_OBJECT.withNewExasolName(TEST_OBJECT.getExasolColumnName()));
    }

    @Test
    void testDifferentClass() {
        assertSymmetricNotEqualWithHashAndEquals(TEST_OBJECT, new Object());
    }

    @Test
    void testDifferentByName() {
        assertSymmetricNotEqualWithHashAndEquals(TEST_OBJECT, TEST_OBJECT.withNewExasolName("my other name"));
    }

    @Test
    void testEqualsContract() {
        EqualsVerifier.forClass(SourceReferenceColumnMapping.class).verify();
    }

    @Test
    void testToString() {
        ToStringVerifier.forClass(SourceReferenceColumnMapping.class).verify();
    }
}
