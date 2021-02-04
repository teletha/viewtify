/*
 * Copyright (C) 2021 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.filter.builtin;

import viewtify.ui.filter.Filter;

class LessThanOrEqual implements Filter<Comparable> {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean test(Comparable value, Comparable tester) {
        return value.compareTo(tester) <= 0;
    }
}