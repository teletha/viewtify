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

import java.util.function.Function;
import java.util.function.Predicate;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.ListBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;

import kiss.I;
import kiss.Variable;
import kiss.WiseBiFunction;

/**
 * @version 2017/12/06 14:31:36
 */
public abstract class CalculationList<E> extends ListBinding<E> {

    /** The element observer. */
    private final InvalidationListener forElement = o -> invalidate();

    /** The list observer. */
    private final ListChangeListener<E> forList = this::onChanged;

    /** The source binding. */
    private final ObservableList<E> source;

    /** The nested observable extractors. */
    private final Function<E, ObservableValue>[] extractors;

    /**
     * Create.
     * 
     * @param source
     * @param extractors
     */
    protected CalculationList(ObservableList<E> source, Function<E, ObservableValue>... extractors) {
        this.source = source;
        this.extractors = extractors;

        source.addListener(forList);
        source.forEach(e -> {
            for (Function<E, ObservableValue> property : extractors) {
                property.apply(e).addListener(forElement);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void dispose() {
        source.forEach(e -> {
            for (Function<E, ObservableValue> property : extractors) {
                property.apply(e).removeListener(forElement);
            }
        });
        source.removeListener(forList);
    }

    /**
     * Called after a change has been made to an ObservableList.
     *
     * @param change an object representing the change that was done
     */
    private void onChanged(Change<? extends E> change) {
        while (change.next()) {
            if (change.wasRemoved()) {
                for (E e : change.getRemoved()) {
                    for (Function<E, ObservableValue> property : extractors) {
                        property.apply(e).removeListener(forElement);
                    }
                }
                invalidate();
            }

            if (change.wasAdded()) {
                for (E e : change.getAddedSubList()) {
                    for (Function<E, ObservableValue> property : extractors) {
                        property.apply(e).addListener(forElement);
                    }
                }
                invalidate();
            }
        }
    }

    public <R> CalculationList<R> as(Class<R> type) {
        return (CalculationList<R>) take(e -> type.isInstance(e));
    }

    public CalculationList<E> take(Predicate<E> condition) {
        return new CalculationList<E>(this) {

            /**
             * {@inheritDoc}
             */
            @Override
            protected ObservableList<E> computeValue() {
                return new MappedList<>(CalculationList.this, e -> condition.test(e) ? e : null);
            }
        };
    }

    public Calculation<Boolean> isNot(E value) {
        return new Calculation<Boolean>(() -> !get().contains(value), null, this);
    }

    public <R> CalculationList<R> map(Function<E, R> mapper) {
        return new CalculationList(this) {

            /**
             * {@inheritDoc}
             */
            @Override
            protected ObservableList<R> computeValue() {
                return new MappedList<>(CalculationList.this, mapper);
            }
        };
    }

    public <R> CalculationList<R> flatObservable(Function<E, ObservableValue<R>> mapper) {
        return new CalculationList(this, mapper) {

            /**
             * {@inheritDoc}
             */
            @Override
            protected ObservableList<R> computeValue() {
                return new MappedList<>(CalculationList.this, e -> mapper.apply(e).getValue());
            }
        };
    }

    public <R> CalculationList<R> flatVariable(Function<E, Variable<R>> mapper) {
        return flatObservable(e -> Viewtify.calculate(mapper.apply(e)));
    }

    /**
     * Create new reduce binding.
     * 
     * @param init
     * @param accumulator
     * @return
     */
    public <R> Calculation<R> reduce(R init, WiseBiFunction<R, E, R> accumulator) {
        return new Calculation<R>(() -> I.signal(this).scan(init, accumulator).to().v, null, this);
    }

    /**
     * Create new indexed item binding.
     * 
     * @param index
     * @return
     */
    public Calculation<E> item(int index) {
        return Viewtify.calculate(this, () -> index < size() ? get(index) : null);
    }
}
