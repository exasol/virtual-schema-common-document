package com.exasol.adapter.document.mapping.auto;

import com.exasol.adapter.document.edml.ColumnNameMapping;

/**
 * This interface allows customizing the mapping of column names when using automatic schema inference. It converts
 * column names from the source (e.g. Parquet, CSV) to Exasol column names.
 */
@FunctionalInterface
public interface ColumnNameConverter {
    /**
     * Convert a column name from the source to an Exasol column name.
     * 
     * @param columnName source column name
     * @return Exasol column name
     */
    String convertColumnName(String columnName);

    /**
     * Create a column name converter for the given column name mapping.
     * 
     * @param columnNameMapping column name mapping
     * @return column name converter
     */
    public static ColumnNameConverter from(final ColumnNameMapping columnNameMapping) {
        if (columnNameMapping == ColumnNameMapping.KEEP_SOURCE) {
            return originalNameConverter();
        }
        return upperSnakeCaseConverter();
    }

    static ColumnNameConverter upperSnakeCaseConverter() {
        return name -> {
            boolean isPreviousUpperOrUnderscore = false;
            boolean isFirst = true;
            final StringBuilder result = new StringBuilder();
            for (int index = 0; index < name.length(); index++) {
                char currentChar = name.charAt(index);
                if (Character.isWhitespace(currentChar)) {
                    currentChar = '_';
                }
                if (Character.isUpperCase(currentChar)) {
                    if (!isPreviousUpperOrUnderscore && !isFirst) {
                        result.append("_");
                    }
                    isPreviousUpperOrUnderscore = true;
                } else {
                    isPreviousUpperOrUnderscore = currentChar == '_';
                }
                result.append(Character.toUpperCase(currentChar));
                isFirst = false;
            }
            return result.toString();
        };
    }

    static ColumnNameConverter originalNameConverter() {
        return name -> name;
    }
}
