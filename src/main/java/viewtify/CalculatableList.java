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
 * @version 2017/12/06 0:26:59
 */
public abstract class CalculatableList<E> extends ListBinding<E> {

    /** The element observer. */
    private final InvalidationListener forElement = o -> invalidate();

    /** The list observer. */
    private final ListChangeListener<E> forList = this::onChanged;

    /** The source binding. */
    private final ObservableList<E> list;

    private final Function<E, ObservableValue>[] extractors;

    /**
     * 
     */
    public CalculatableList(ObservableList<E> list, Function<E, ObservableValue>... extractors) {
        this.list = list;
        this.extractors = extractors;

        list.addListener(forList);
        list.forEach(e -> {
            for (Function<E, ObservableValue> property : extractors) {
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
            for (Function<E, ObservableValue> property : extractors) {
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
                for (Function<E, ObservableValue> property : extractors) {
                    property.apply(e).removeListener(forElement);
                }
            }
            for (E e : change.getAddedSubList()) {
                for (Function<E, ObservableValue> property : extractors) {
                    property.apply(e).addListener(forElement);
                }
            }
            invalidate();
        }
    }

    public <R> CalculatableList<R> as(Class<R> type) {
        return (CalculatableList<R>) take(e -> type.isInstance(e));
    }

    public CalculatableList<E> take(Predicate<E> condition) {
        return new CalculatableList<E>(this) {

            /**
             * {@inheritDoc}
             */
            @Override
            protected ObservableList<E> computeValue() {
                return new MappedList<>(CalculatableList.this, e -> condition.test(e) ? e : null);
            }
        };
    }

    public Calculatable<Boolean> isNot(E value) {
        return new Calculatable<Boolean>(this) {

            /**
             * {@inheritDoc}
             */
            @Override
            protected Boolean calculate() {
                return !CalculatableList.this.get().contains(value);
            }
        };
    }

    public <R> CalculatableList<R> map(Function<E, R> mapper) {
        return new CalculatableList(this) {

            /**
             * {@inheritDoc}
             */
            @Override
            protected ObservableList<R> computeValue() {
                return new MappedList<>(CalculatableList.this, mapper);
            }
        };
    }

    public <R> CalculatableList<R> flatObservable(Function<E, ObservableValue<R>> mapper) {
        return new CalculatableList(this, mapper) {

            /**
             * {@inheritDoc}
             */
            @Override
            protected ObservableList<R> computeValue() {
                return new MappedList<>(CalculatableList.this, e -> mapper.apply(e).getValue());
            }
        };
    }

    public <R> CalculatableList<R> flatVariable(Function<E, Variable<R>> mapper) {
        return flatObservable(e -> new VariableBinding(mapper.apply(e)));
    }

    /**
     * Create new reduce binding.
     * 
     * @param init
     * @param accumulator
     * @return
     */
    public <R> Calculatable<R> reduce(R init, WiseBiFunction<R, E, R> accumulator) {
        return new Calculatable<R>(this) {

            /**
             * {@inheritDoc}
             */
            @Override
            protected R calculate() {
                System.out.println("reduce");
                return I.<E> signal(CalculatableList.this).scan(init, accumulator).to().v;
            }
        };
    }

    /**
     * Create new indexed item binding.
     * 
     * @param index
     * @return
     */
    public Calculatable<E> item(int index) {
        return Viewtify.calculate(this, () -> index < size() ? get(index) : null);
    }
}
