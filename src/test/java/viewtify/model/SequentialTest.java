/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.model;

import java.util.List;

import org.junit.jupiter.api.Test;

import kiss.I;
import kiss.Ⅱ;

/**
 * @version 2018/04/02 16:43:00
 */
class SequentialTest {

    @Test
    void add() {
        Model list = new Model();
        List<Ⅱ<String, Integer>> result = list.add.map(v -> I.pair(v, list.indexOf(v))).toList();

        list.add("ADD");
        assert result.size() == 1;
        assert result.get(0).ⅱ == 0;
    }

    @Test
    void addIndex() {
        Model list = new Model();
        List<Ⅱ<String, Integer>> result = list.add.map(v -> I.pair(v, list.indexOf(v))).toList();

        list.add(0, "A");
        list.add(0, "B");
        assert result.size() == 2;
        assert result.get(0).ⅱ == 0;
        assert result.get(1).ⅱ == 0;
    }

    @Test
    void set() {
        Model list = new Model();
        list.add(0, "X");

        List<Ⅱ<String, Integer>> result = list.add.map(v -> I.pair(v, list.indexOf(v))).toList();
        List<Ⅱ<String, Integer>> removes = list.remove.map(v -> I.pair(v, list.indexOf(v))).toList();

        list.set(0, "A");
        list.set(0, "B");
        assert result.size() == 2;
        assert result.get(0).ⅱ == 0;
        assert result.get(1).ⅱ == 0;
        assert removes.size() == 2;
        assert removes.get(0).ⅱ == 0;
        assert removes.get(1).ⅱ == 0;
    }

    @Test
    void remove() {
        Model list = new Model();
        list.add("X");
        list.add("Y");

        List<Ⅱ<String, Integer>> result = list.remove.map(v -> I.pair(v, list.indexOf(v))).toList();

        list.remove("Y");
        assert result.size() == 1;
        assert result.get(0).ⅱ == 1;
    }

    @Test
    void removeIndex() {
        Model list = new Model();
        list.add("X");
        list.add("Y");

        List<Ⅱ<String, Integer>> result = list.remove.map(v -> I.pair(v, list.indexOf(v))).toList();

        list.remove(1);
        assert result.size() == 1;
        assert result.get(0).ⅱ == 1;
    }

    @Test
    void clear() {
        Model list = new Model();
        list.add("X");
        list.add("Y");

        List<Ⅱ<String, Integer>> result = list.remove.map(v -> I.pair(v, list.indexOf(v))).toList();

        list.clear();
        assert result.size() == 2;
        assert result.get(0).ⅱ == 0;
        assert result.get(1).ⅱ == 1;
    }

    /**
     * @version 2018/04/02 16:43:15
     */
    private static class Model extends Sequential<String> {
    }
}
