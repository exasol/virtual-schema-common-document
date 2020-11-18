package com.exasol.adapter.document.mapping;

import com.exasol.adapter.document.documentnode.DocumentNode;
import com.exasol.errorreporting.ExaError;
import com.exasol.sql.expression.NullLiteral;
import com.exasol.sql.expression.StringLiteral;
import com.exasol.sql.expression.ValueExpression;

/**
 * {@link ColumnValueExtractor} for {@link PropertyToJsonColumnMapping}.
 */
@java.lang.SuppressWarnings("squid:S119") // DocumentVisitorType does not fit naming conventions.
public abstract class PropertyToJsonColumnValueExtractor<DocumentVisitorType>
        extends AbstractPropertyToColumnValueExtractor<DocumentVisitorType> {
    private final PropertyToJsonColumnMapping column;

    /**
     * Create an instance of {@link PropertyToJsonColumnValueExtractor}.
     * 
     * @param column {@link PropertyToJsonColumnMapping}
     */
    public PropertyToJsonColumnValueExtractor(final PropertyToJsonColumnMapping column) {
        super(column);
        this.column = column;
    }

    @Override
    protected final ValueExpression mapValue(final DocumentNode<DocumentVisitorType> documentValue) {
        final String jsonValue = mapJsonValue(documentValue);
        if (jsonValue.length() > this.column.getVarcharColumnSize()) {
            if (this.column.getOverflowBehaviour().equals(MappingErrorBehaviour.ABORT)) {
                throw new OverflowException(
                        ExaError.messageBuilder("E-VSD-35").message(
                                "A generated JSON did exceed the configured maximum size of the column {{COLUMN_NAME}}.")
                                .parameter("COLUMN_NAME", this.column.getExasolColumnName())
                                .mitigation("Increase the 'varcharColumnSize' in your mapping definition.")
                                .mitigation("Set the 'overflowBehaviour' to 'NULL'.").toString(),
                        this.column);
            } else {
                return NullLiteral.nullLiteral();
            }
        } else {
            return StringLiteral.of(jsonValue);
        }
    }

    protected abstract String mapJsonValue(final DocumentNode<DocumentVisitorType> dynamodbProperty);
}
