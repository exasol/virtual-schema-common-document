package com.exasol.adapter.document;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {

    private ListUtils() {
        // empty on purpose
    }

    public static <T> List<T> listWith(final List<T> list, final T additionalElement) {
        final ArrayList<T> result = new ArrayList<>(list);
        result.add(additionalElement);
        return result;
    }

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
