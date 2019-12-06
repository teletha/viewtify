/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.util;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

class GenericFilteredListTest {

    @Test
    void filter() {
        ObservableList<Integer> source = FXCollections.observableArrayList();
        GenericFilteredList<Integer> filtered = new GenericFilteredList<>(source, v -> v % 2 == 0);

        source.add(0);
    }
}
