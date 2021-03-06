package com.exasol.adapter.document;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import com.exasol.adapter.AdapterException;
import com.exasol.adapter.capabilities.Capabilities;
import com.exasol.adapter.capabilities.MainCapability;
import com.exasol.adapter.response.GetCapabilitiesResponse;

class DocumentAdapterTest {

    @Test
    void testUnsupportedMainCapability() throws AdapterException {
        final DocumentAdapter documentAdapter = spy(DocumentAdapter.class);
        when(documentAdapter.getCapabilities())
                .thenReturn(Capabilities.builder().addMain(MainCapability.AGGREGATE_HAVING).build());
        final UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
                () -> documentAdapter.getCapabilities(null, null));
        assertThat(exception.getMessage(), matchesPattern(Pattern.quote(
                "F-VSD-3: This dialect specified main-capabilities (AGGREGATE_HAVING) that are not supported by the abstract DocumentAdapter. Please remove the capability from the specific adapter implementation. Supported main-capabilities are [")
                + "[^]]*\\]\\."));
    }

    @Test
    void testSupportedCapabilities() throws AdapterException {
        final DocumentAdapter documentAdapter = spy(DocumentAdapter.class);
        final Capabilities capabilities = Capabilities.builder().addMain(MainCapability.SELECTLIST_PROJECTION).build();
        when(documentAdapter.getCapabilities()).thenReturn(capabilities);
        final GetCapabilitiesResponse response = documentAdapter.getCapabilities(null, null);
        assertThat(response.getCapabilities(), equalTo(capabilities));
    }
}