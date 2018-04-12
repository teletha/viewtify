/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import org.junit.jupiter.api.Test;

/**
 * @version 2018/04/12 12:00:51
 */
class ObservableListWrapTest {

    @Test
    void noConcurrentModificationException() {
        ObservableList<String> list = Viewtify.observe(new CopyOnWriteArrayList<String>());
        list.addAll("A", "B", "C", "D");

        for (String item : list) {
            list.remove(item);
        }
    }

    @Test
    void observableAdd() {
        List<String> result = new ArrayList();
        ObservableList<String> list = Viewtify.observe(new CopyOnWriteArrayList<String>());
        list.addListener((ListChangeListener<String>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    change.getAddedSubList().forEach(result::add);
                }
            }
        });

        list.add("A");
        assert result.size() == 1;
        list.addAll("B", "C");
        assert result.size() == 3;
    }
}
