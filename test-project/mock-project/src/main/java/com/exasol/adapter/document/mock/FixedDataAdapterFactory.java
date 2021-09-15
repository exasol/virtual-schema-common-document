package com.exasol.adapter.document.mock;

import com.exasol.adapter.AdapterFactory;
import com.exasol.adapter.VirtualSchemaAdapter;
import com.exasol.logging.VersionCollector;

/**
 * Factory for {@link FixedDataAdapter}.
 */
public class FixedDataAdapterFactory implements AdapterFactory {
    @Override
    public VirtualSchemaAdapter createAdapter() {
        return new FixedDataAdapter();
    }

    @Override
    public String getAdapterVersion() {
        final VersionCollector versionCollector = new VersionCollector(
                "META-INF/maven/com.exasol/virtual-schema-common-document-mock-project/pom.properties");
        return versionCollector.getVersionNumber();
    }

    @Override
    public String getAdapterName() {
        return FixedDataAdapter.ADAPTER_NAME;
    }
}
