/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.query;

import org.junit.jupiter.api.Test;

import kiss.Variable;
import viewtify.ui.query.CompoundQuery.Query;
import viewtify.ui.query.CompoundQuery.Tester;

class CompoundQueryTest {

    @Test
    void empty() {
        CompoundQuery<String> compound = new CompoundQuery();
        assert compound.test("accept");
        assert compound.test("anything");
        assert compound.test("");
        assert compound.test(null);
    }

    @Test
    void singleQuery() {
        CompoundQuery<String> compound = new CompoundQuery();
        Query<String, String> query = compound.addQuery("test");
        query.input.set("a");
        query.tester.set(Tester.Contain);

        assert compound.test("accept");
        assert compound.test("anything");
        assert compound.test("") == false;
        assert compound.test(null);
    }

    @Test
    void multipleQueries() {
        CompoundQuery<String> compound = new CompoundQuery();
        Query<String, String> query1 = compound.addQuery("test1");
        query1.input.set("a");
        query1.tester.set(Tester.Contain);
        Query<String, String> query2 = compound.addQuery("test2");
        query2.input.set("c");
        query2.tester.set(Tester.Contain);

        assert compound.test("accept");
        assert compound.test("anything") == false;
        assert compound.test("") == false;
        assert compound.test(null);
    }

    @Test
    void updateByInputModification() {
        CompoundQuery<String> compound = new CompoundQuery();
        Variable<CompoundQuery<String>> updated = compound.updated.to();
        assert updated.isAbsent();

        Query<String, String> query = compound.addQuery("test");
        query.input.set("new value");
        assert updated.isPresent();
    }

    @Test
    void updateByTesterModification() {
        CompoundQuery<String> compound = new CompoundQuery();
        Variable<CompoundQuery<String>> updated = compound.updated.to();
        assert updated.isAbsent();

        Query<String, String> query = compound.addQuery("test");
        query.tester.set(Tester.Contain);
        assert updated.isPresent();
    }
}