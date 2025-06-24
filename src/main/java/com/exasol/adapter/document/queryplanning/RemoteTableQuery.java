package com.exasol.adapter.document.queryplanning;

import java.util.List;
import java.util.stream.Collectors;

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
     * Returns a human-readable string representation of the {@link RemoteTableQuery}.
     * <p>
     * This includes the mapped source table, a list of selected columns (formatted line by line),
     * and any applied selection predicates.
     *
     * @return a formatted string representation of the query
     */
    @Override
    public String toString() {
        String formattedSelectList = selectList.stream()
                .map(column -> "    - " + column.toString().replace("\n", "\n      "))
                .collect(Collectors.joining("\n"));

        return String.format(
                "RemoteTableQuery {\n" +
                        "  fromTable: %s\n" +
                        "  selectList:\n%s\n" +
                        "  selection: %s\n" +
                        "}",
                fromTable,
                formattedSelectList,
                selection
        );
    }
}
