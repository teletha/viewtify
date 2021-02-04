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

import java.util.function.Function;
import java.util.function.Predicate;

public class ValuedFilter<M, V> implements Predicate<M> {

    public Function<M, V> valueExtractor;

    public V tester;

    public Filter<V> filter;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean test(M model) {
        if (tester == null) {
            return true;
        }

        V value = valueExtractor.apply(model);
        if (value == null) {
            return false;
        }
        return filter.test(value, tester);
    }
}
