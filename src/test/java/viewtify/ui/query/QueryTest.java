/*
 * Copyright (C) 2021 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.query;

import org.junit.jupiter.api.Test;

import viewtify.ui.query.CompoundQuery;
import viewtify.ui.query.CompoundQuery.Query;
import viewtify.ui.query.CompoundQuery.Tester;

class QueryTest {

    CompoundQuery<String> compound = new CompoundQuery();

    @Test
    void initialize() {
        Query<String, String> query = compound.addQuery("test");
        assert query.name.get().equals("test");
        assert query.type == String.class;
        assert query.input.isAbsent();
        assert query.tester.isAbsent();
    }

    @Test
    void emptyInputAcceptsAnything() {
        Query<String, String> query = compound.addQuery("empty");
        query.tester.set(Tester.Contain);

        assert query.test("accept");
        assert query.test("anything");
        assert query.test("");
        assert query.test(null);
    }

    @Test
    void emptyMatcherAcceptsAnything() {
        Query<String, String> query = compound.addQuery("empty");
        query.input.set("user input");

        assert query.test("accept");
        assert query.test("anything");
        assert query.test("");
        assert query.test(null);
    }

    @Test
    void stringContain() {
        Query<String, String> query = compound.addQuery("test");
        query.input.set("ok");
        query.tester.set(Tester.Contain);

        assert query.test("ok");
        assert query.test("OK");
        assert query.test("xxokxx");
        assert query.test("okxx");
        assert query.test("xxok");
        assert query.test(null);
        assert query.test("fail") == false;
    }

    @Test
    void stringNotContain() {
        Query<String, String> query = compound.addQuery("test");
        query.input.set("ok");
        query.tester.set(Tester.NotContain);

        assert query.test("ok") == false;
        assert query.test("OK") == false;
        assert query.test("xxokxx") == false;
        assert query.test("okxx") == false;
        assert query.test("xxok") == false;
        assert query.test(null);
        assert query.test("fail");
    }

    @Test
    void stringStartWith() {
        Query<String, String> query = compound.addQuery("test");
        query.input.set("ok");
        query.tester.set(Tester.StartWith);

        assert query.test("ok");
        assert query.test("xxokxx") == false;
        assert query.test("okxx");
        assert query.test("OKxx");
        assert query.test("xxok") == false;
        assert query.test(null);
        assert query.test("fail") == false;
    }

    @Test
    void stringEndWith() {
        Query<String, String> query = compound.addQuery("test");
        query.input.set("ok");
        query.tester.set(Tester.EndWith);

        assert query.test("ok");
        assert query.test("xxokxx") == false;
        assert query.test("okxx") == false;
        assert query.test("xxok");
        assert query.test("xxOK");
        assert query.test(null);
        assert query.test("fail") == false;
    }

    @Test
    void stringRegEx() {
        Query<String, String> query = compound.addQuery("test");
        query.input.set(".+ok.+");
        query.tester.set(Tester.RegEx);

        assert query.test("ok") == false;
        assert query.test("xxokxx");
        assert query.test("xxOKxx");
        assert query.test("okxx") == false;
        assert query.test("xxok") == false;
        assert query.test(null);
        assert query.test("fail") == false;
    }
}
