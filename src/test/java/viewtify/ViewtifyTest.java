/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify;

import java.util.HashMap;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;

import org.junit.jupiter.api.Test;

class ViewtifyTest {

    @Test
    void observeList() {
        ObservableList<String> list = FXCollections.observableArrayList();
        List<ObservableList<String>> result = Viewtify.observe(list).toList();
        list.add("one");

        assert result.size() == 1;
        assert result.get(0) == list;
    }

    @Test
    void observeSet() {
        ObservableSet<String> set = FXCollections.observableSet();
        List<ObservableSet<String>> result = Viewtify.observe(set).toList();
        set.add("one");

        assert result.size() == 1;
        assert result.get(0) == set;
    }

    @Test
    void observeMap() {
        ObservableMap<String, String> map = FXCollections.observableMap(new HashMap());
        List<ObservableMap<String, String>> result = Viewtify.observe(map).toList();
        map.put("key", "value");

        assert result.size() == 1;
        assert result.get(0) == map;
    }
}
