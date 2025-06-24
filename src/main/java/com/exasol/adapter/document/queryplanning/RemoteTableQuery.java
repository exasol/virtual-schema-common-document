package com.exasol.adapter.document.queryplanning;

import java.util.List;

import com.exasol.adapter.document.mapping.ColumnMapping;
import com.exasol.adapter.document.mapping.TableMapping;
import com.exasol.adapter.document.querypredicate.QueryPredicate;

/**
 * This class represents the whole query inside of one document.
 */
public class RemoteTableQuery {
    private final TableMapping fromTable;
    private final List<ColumnMapping> selectList;
    private final QueryPredicate selection;

    /**
     * Create an instance of {@link RemoteTableQuery}.
     * 
     * @param fromTable  remote table to query
     * @param selectList in correct order
     * @param selection  the selection
     */
    public RemoteTableQuery(final TableMapping fromTable, final List<ColumnMapping> selectList,
            final QueryPredicate selection) {
        this.fromTable = fromTable;
        this.selectList = selectList;
        this.selection = selection;
    }

    /**
     * Get the table the data is loaded from.
     * 
     * @return source table
     */
    public TableMapping getFromTable() {
        return this.fromTable;
    }

    /**
     * Get the select list columns.
     *
     * @return select list columns
     */
    public List<ColumnMapping> getSelectList() {
        return this.selectList;
    }

    /**
     * Get the where clause of this query.
     * 
     * @return Predicate representing the selection
     */
    public QueryPredicate getSelection() {
        return this.selection;
    }

    /**
     * Returns a compact string representation of this {@link RemoteTableQuery} instance.
     * <p>
     * The returned string includes the source table mapping, the list of selected columns,
     * and any applied selection predicates in a single-line format, suitable for logging or debugging.
     *
     * @return a concise string representation of the query
     */
    @Override
    public String toString() {
        return String.format(
                "RemoteTableQuery{fromTable=%s, selectList=%s, selection=%s}",
                fromTable, selectList, selection
        );
    }
}
