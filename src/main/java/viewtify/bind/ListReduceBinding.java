/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.bind;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;

import kiss.I;

/**
 * @version 2017/11/26 17:05:48
 */
public class ListReduceBinding<E, R> extends ObjectBinding<R> {

    /** The element observer. */
    private final WeakInvalidationListener forElement = new WeakInvalidationListener(obs -> invalidate());

    /** The list observer. */
    private final WeakListChangeListener<E> forList = new WeakListChangeListener<>(this::onChanged);

    /** The source list. */
    private final ObservableList<E> list;

    /** The initial value. */
    private final R init;

    /** The accumulator. */
    private final BiFunction<R, E, R> accumulator;

    /** The element converter. */
    private final List<Function<E, Observable>> observingProperties;

    /**
     * @param list
     * @param properties
     */
    public ListReduceBinding(ObservableList<E> list, R init, BiFunction<R, E, R> accumulator, List<Function<E, Observable>> observingProperties) {
        this.list = list;
        this.init = init;
        this.accumulator = accumulator;
        this.observingProperties = observingProperties;

        list.addListener(forList);
        list.forEach(e -> {
            for (Function<E, Observable> property : observingProperties) {
                property.apply(e).addListener(forElement);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected R computeValue() {
        return I.signal(list).scan(init, accumulator).to().get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        list.forEach(elem -> {
            if (elem instanceof ObservableValue) {
                ((ObservableValue) elem).removeListener(forElement);
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
            // for removed elements
            change.getRemoved().forEach(e -> {
                for (Function<E, Observable> property : observingProperties) {
                    property.apply(e).removeListener(forElement);
                }
            });

            // for added elements
            change.getAddedSubList().forEach(e -> {
                for (Function<E, Observable> property : observingProperties) {
                    property.apply(e).addListener(forElement);
                }
            });
            invalidate();
        }
    }
}
