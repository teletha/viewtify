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

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
}
