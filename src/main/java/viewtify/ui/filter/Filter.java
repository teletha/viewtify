/*
 * Copyright (C) 2021 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

import kiss.Extensible;
import kiss.I;
import kiss.model.Model;

public interface Filter<V> extends Extensible, BiPredicate<V, V> {

    /**
     * {@inheritDoc}
     */
    @Override
    boolean test(V value, V tester);

    static <T> List<Filter> by(Class<T> type) {
        List<Filter> filters = new ArrayList();
        List<Class<Filter>> list = I.findAs(Filter.class);
        for (Class<Filter> item : list) {
            if (item.isInterface()) {
                continue;
            }
            if (((Class) Model.collectParameters(item, Filter.class)[0]).isAssignableFrom(type)) {
                filters.add(I.make(item));
            }
        }
        return filters;
    }
}
