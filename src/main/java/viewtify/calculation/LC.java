/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.calculation;

import java.util.function.Function;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;

/**
 * @version 2017/12/03 13:19:44
 */
abstract class LC<E, R> extends ObjectBinding<R> implements Calculatable<R> {

    /** The element observer. */
    private final InvalidationListener forElement = o -> invalidate();

    /** The list observer. */
    private final ListChangeListener<E> forList = this::onChanged;

    /** The source binding. */
    private final ObservableList<E> list;

    private final Function<E, ObservableValue<R>>[] extractors;

    /**
     * 
     */
    LC(ObservableList<E> list, Function<E, ObservableValue<R>>... extractors) {
        this.list = list;
        this.extractors = extractors;

        list.addListener(forList);
        list.forEach(e -> {
            for (Function<E, ObservableValue<R>> property : extractors) {
                property.apply(e).addListener(forElement);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        list.forEach(e -> {
            for (Function<E, ObservableValue<R>> property : extractors) {
                property.apply(e).removeListener(forElement);
            }
        });
        list.removeListener(forList);
    }

    /**
     * Called after a change has been made to an ObservableList.
     *
     * @param change an object representing the change that was done
     */
    private void onChanged(Change<? extends E> change) {
        while (change.next()) {
            for (E e : change.getRemoved()) {
                for (Function<E, ObservableValue<R>> property : extractors) {
                    property.apply(e).removeListener(forElement);
                }
            }
            for (E e : change.getAddedSubList()) {
                for (Function<E, ObservableValue<R>> property : extractors) {
                    property.apply(e).addListener(forElement);
                }
            }
            invalidate();
        }
    }
}