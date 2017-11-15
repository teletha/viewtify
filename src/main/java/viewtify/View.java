/*
 * Copyright (C) 2017 Nameless Production Committee
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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.ListSpinnerValueFactory;
import javafx.util.StringConverter;

import kiss.Extensible;
import kiss.Signal;

/**
 * @version 2017/11/13 20:39:23
 */
public abstract class View implements Extensible {

    /**
     * Initialize this view.
     */
    protected abstract void initialize();

    /**
     * Observe the specified value.
     * 
     * @param value
     * @return
     */
    protected final <T> Signal<T> observe(ObservableValue<T> value) {
        return new Signal<>((observer, disposer) -> {
            ChangeListener<T> listener = (e, o, n) -> {
                observer.accept(n);
            };
            value.addListener(listener);

            return disposer.add(() -> {
                value.removeListener(listener);
            });
        });
    }

    /**
     * Helper to create {@link SpinnerValueFactory}.
     * 
     * @param values
     * @return
     */
    protected final <T> SpinnerValueFactory<T> spinnerV(T... values) {
        ListSpinnerValueFactory factory = new ListSpinnerValueFactory(values(values));
        factory.setWrapAround(false);
        factory.setValue(values[0]);

        return factory;
    }

    /**
     * Helper to create {@link SpinnerValueFactory}.
     * 
     * @param values
     * @return
     */
    protected final <T> SpinnerValueFactory<List<T>> spinner(int size, T... values) {
        ObservableList<List<T>> lists = FXCollections.observableArrayList();

        for (int i = 0; i < values.length;) {
            List<T> list = new ArrayList();

            for (int j = 0; j < size; j++) {
                list.add(values[i++]);
            }
            lists.add(list);
        }

        ListSpinnerValueFactory<List<T>> factory = new ListSpinnerValueFactory<>(lists);
        factory.setConverter(new StringConverter<List<T>>() {

            /**
             * {@inheritDoc}
             */
            @Override
            public String toString(List<T> object) {
                return object.get(0).toString();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public List<T> fromString(String string) {
                return null;
            }
        });
        return factory;
    }

    /**
     * Helper to create {@link ObservableList}.
     * 
     * @param values
     * @return
     */
    protected final <T> ObservableList<T> values(T... values) {
        return FXCollections.observableArrayList(values);
    }
}
