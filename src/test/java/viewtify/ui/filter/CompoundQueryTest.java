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

import org.junit.jupiter.api.Test;

import viewtify.ui.filter.CompoundQuery.Query;

class CompoundQueryTest {

    @Test
    void dynamicQuery() {
        CompoundQuery<String> set = new CompoundQuery();
        Query<String, String> dynamic = set.addQuery("test");
        assert dynamic.description.get().equals("test");
        assert dynamic.type == String.class;
    }
}
