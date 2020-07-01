/*
 * Copyright (C) 2020 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.helper;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import kiss.I;

class CollectableHelperTest {

    @Test
    void items() {
        SimpleList<Integer> list = new SimpleList(1, 2, 3);
        Assertions.assertIterableEquals(List.of(1, 2, 3), list.items());
    }

    @Test
    void setArray() {
        SimpleList<Integer> list = new SimpleList();
        list.items(1, 2, 3);
        Assertions.assertIterableEquals(List.of(1, 2, 3), list.items());
    }

    @Test
    void setList() {
        SimpleList<Integer> list = new SimpleList();
        list.items(List.of(1, 2, 3));
        Assertions.assertIterableEquals(List.of(1, 2, 3), list.items());
    }

    @Test
    void setIterable() {
        SimpleList<Integer> list = new SimpleList();
        list.items((Iterable<Integer>) List.of(1, 2, 3));
        Assertions.assertIterableEquals(List.of(1, 2, 3), list.items());
    }

    @Test
    void setStream() {
        SimpleList<Integer> list = new SimpleList();
        list.items(IntStream.range(1, 4).boxed());
        Assertions.assertIterableEquals(List.of(1, 2, 3), list.items());
    }

    @Test
    void setSignal() {
        SimpleList<Integer> list = new SimpleList();
        list.items(I.signal(1, 2, 3));
        Assertions.assertIterableEquals(List.of(1, 2, 3), list.items());
    }

    @Test
    void take() {
        SimpleList<Integer> list = new SimpleList(1, 2, 3, 4, 5);
        list.take(v -> v % 2 == 0);

        Assertions.assertIterableEquals(List.of(2, 4), list.itemsProperty().getValue());
    }

    @Test
    void takeWithObservableValue() {
        Property<Integer> p = new SimpleObjectProperty(3);

        SimpleList<Integer> list = new SimpleList(1, 2, 3, 4, 5);
        list.take(p, (v, number) -> v < number);
        Assertions.assertIterableEquals(List.of(1, 2), list.itemsProperty().getValue());

        p.setValue(4);
        Assertions.assertIterableEquals(List.of(1, 2, 3), list.itemsProperty().getValue());
    }

    @Test
    void skip() {
        SimpleList<Integer> list = new SimpleList(1, 2, 3, 4, 5);
        list.skip(v -> v % 2 == 0);

        Assertions.assertIterableEquals(List.of(1, 3, 5), list.itemsProperty().getValue());
    }

    @Test
    void skipWithObservableValue() {
        Property<Integer> p = new SimpleObjectProperty(3);

        SimpleList<Integer> list = new SimpleList(1, 2, 3, 4, 5);
        list.skip(p, (v, number) -> v < number);
        Assertions.assertIterableEquals(List.of(3, 4, 5), list.itemsProperty().getValue());

        p.setValue(4);
        Assertions.assertIterableEquals(List.of(4, 5), list.itemsProperty().getValue());
    }

    /**
     * Simple Implementation.
     */
    private static class SimpleList<T> extends ReferenceHolder implements CollectableHelper<SimpleList<T>, T> {

        /** The actual list. */
        private final ObjectProperty<ObservableList<T>> property = new SimpleObjectProperty(FXCollections.observableArrayList());

        private SimpleList(T... initial) {
            property.getValue().addAll(initial);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Property<ObservableList<T>> itemsProperty() {
            return property;
        }
    }
}