/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package toybox;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

import kiss.I;

/**
 * @version 2018/03/06 20:59:00
 */
public class SelectableModelTest {

    @Test
    public void add() {
        Model list = new Model();
        List added = list.added.toList();

        list.items.add("A");
        list.items.add("B");
        assert added.get(0) == "A";
        assert added.get(1) == "B";
    }

    @Test
    public void addAll() {
        Model list = new Model();
        List added = list.added.toList();

        list.items.addAll(I.list("A", "B", "C"));
        assert added.get(0) == "A";
        assert added.get(1) == "B";
        assert added.get(2) == "C";
    }

    @Test
    public void addIndex() {
        Model list = new Model("A", "B", "C");
        List added = list.added.toList();

        list.items.add(1, "X");
        assert added.get(0) == "X";
    }

    @Test
    public void addAllIndex() {
        Model list = new Model("A", "B", "C");
        List added = list.added.toList();

        list.items.addAll(1, I.list("X", "Y"));
        assert added.get(0) == "X";
        assert added.get(1) == "Y";
    }

    @Test
    public void set() {
        Model list = new Model("A", "B", "C");
        List added = list.added.toList();
        List removed = list.removed.toList();

        list.items.set(1, "X");
        list.items.set(2, "Y");
        assert added.get(0) == "X";
        assert removed.get(0) == "B";
        assert added.get(1) == "Y";
        assert removed.get(1) == "C";
    }

    @Test
    public void setAll() {
        Model list = new Model("A", "B", "C");
        List added = list.added.toList();
        List removed = list.removed.toList();

        list.items.setAll("X", "Y");
        assert added.get(0) == "X";
        assert added.get(1) == "Y";
        assert removed.get(0) == "A";
        assert removed.get(1) == "B";
        assert removed.get(2) == "C";
    }

    @Test
    public void remove() {
        Model list = new Model("A", "B", "C");
        List removed = list.removed.toList();

        list.items.remove("A");
        list.items.remove("B");
        assert removed.get(0) == "A";
        assert removed.get(1) == "B";
    }

    @Test
    public void removeByIndex() {
        Model list = new Model("A", "B", "C");
        List removed = list.removed.toList();

        list.items.remove(1);
        list.items.remove(1);
        assert removed.get(0) == "B";
        assert removed.get(1) == "C";
    }

    @Test
    public void removeByIndexRange() {
        Model list = new Model("A", "B", "C");
        List removed = list.removed.toList();

        list.items.remove(1, 3);
        assert removed.get(0) == "B";
        assert removed.get(1) == "C";
    }

    @Test
    public void removeAll() {
        Model list = new Model("A", "B", "C");
        List removed = list.removed.toList();

        list.items.removeAll("A", "C");
        assert removed.get(0) == "A";
        assert removed.get(1) == "C";
    }

    @Test
    public void clear() {
        Model list = new Model("A", "B", "C");
        List removed = list.removed.toList();

        list.items.clear();
        assert removed.get(0) == "A";
        assert removed.get(1) == "B";
        assert removed.get(2) == "C";
    }

    @Test
    public void sort() {
        Model list = new Model("C", "D", "B", "A");
        List added = list.added.toList();
        List removed = list.removed.toList();

        list.items.sort(Comparator.naturalOrder());
        assert added.get(0) == "A";
        assert added.get(1) == "B";
        assert added.get(2) == "C";
        assert added.get(3) == "D";
        assert removed.get(0) == "C";
        assert removed.get(1) == "D";
        assert removed.get(2) == "B";
        assert removed.get(3) == "A";
    }

    @Test
    public void sortExternal() {
        Model list = new Model("C", "D", "B", "A");
        List added = list.added.toList();
        List removed = list.removed.toList();

        Collections.sort(list.items);
        assert added.get(0) == "A";
        assert added.get(1) == "B";
        assert added.get(2) == "C";
        assert added.get(3) == "D";
        assert removed.get(0) == "C";
        assert removed.get(1) == "D";
        assert removed.get(2) == "B";
        assert removed.get(3) == "A";
    }

    /**
     * @version 2018/03/07 9:19:55
     */
    private static class Model extends SelectableModel<String> {

        /**
         * @param values
         */
        private Model(String... values) {
            super(values);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Model restore() {
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Model store() {
            return this;
        }
    }
}
