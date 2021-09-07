package com.exasol.adapter.document;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains static functions for concatenating lists.
 */
public class ListUtils {

    private ListUtils() {
        // empty on purpose
    }

    /**
     * Add an element to an unmodifiable list.
     * 
     * @param list              list to add element to
     * @param additionalElement element to add
     * @param <T>               type of the list elements
     * @return copy of the list with added element
     */
    public static <T> List<T> listWith(final List<T> list, final T additionalElement) {
        final ArrayList<T> result = new ArrayList<>(list);
        result.add(additionalElement);
        return result;
    }

    /**
     * Concatenate two lists with removing duplicates.
     * 
     * @param list      first list
     * @param otherList second list
     * @param <T>       type of the list elements
     * @return new list
     */
    public static <T> List<T> union(final List<T> list, final List<T> otherList) {
        final ArrayList<T> result = new ArrayList<>(list);
        for (final T item : otherList) {
            if (!result.contains(item)) {
                result.add(item);
            }
        }
        return result;
    }
}
