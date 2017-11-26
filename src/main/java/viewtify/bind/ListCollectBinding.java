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
import java.util.function.Function;
import java.util.stream.Collector;

import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;

/**
 * @version 2017/11/26 17:05:48
 */
public class ListCollectBinding<E, A, R> extends ObjectBinding<R> {

    /** The element observer. */
    private final WeakInvalidationListener forElement = new WeakInvalidationListener(obs -> invalidate());

    /** The list observer. */
    private final WeakListChangeListener<E> forList = new WeakListChangeListener<>(this::onChanged);

    /** The source list. */
    private final ObservableList<E> list;

    /** The accumulator. */
    private final Collector<? super E, A, R> collector;

    /** The element converter. */
    private final List<Function<E, Observable>> observingProperties;

    /**
     * @param list
     * @param properties
     */
    public ListCollectBinding(ObservableList<E> list, Collector<? super E, A, R> collector, List<Function<E, Observable>> observingProperties) {
        this.list = list;
        this.collector = collector;
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
        return list.stream().collect(collector);
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
