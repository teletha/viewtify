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
import java.util.function.Predicate;

import javafx.beans.value.ObservableListValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

import kiss.I;
import kiss.Variable;
import kiss.WiseBiFunction;
import viewtify.Viewtify;

/**
 * @version 2017/12/06 0:26:59
 */
public interface CalculatableList<T> extends ObservableListValue<T> {

    default <R> CalculatableList<R> as(Class<R> type) {
        return (CalculatableList<R>) take(e -> type.isInstance(e));
    }

    default CalculatableList<T> take(Predicate<T> condition) {
        return new ListCalculation<T, T>(this) {

            /**
             * {@inheritDoc}
             */
            @Override
            protected ObservableList<T> computeValue() {
                return new MappedList<>(CalculatableList.this, e -> condition.test(e) ? e : null);
            }
        };
    }

    default Calculatable<Boolean> isNot(T value) {
        return new LC<T, Boolean>(this) {

            /**
             * {@inheritDoc}
             */
            @Override
            protected Boolean computeValue() {
                return !CalculatableList.this.get().contains(value);
            }
        };
    }

    default <R> CalculatableList<R> map(Function<T, R> mapper) {
        return new ListCalculation<T, R>(this) {

            /**
             * {@inheritDoc}
             */
            @Override
            protected ObservableList<R> computeValue() {
                return new MappedList<>(CalculatableList.this, mapper);
            }
        };
    }

    default <R> CalculatableList<R> flatObservable(Function<T, ObservableValue<R>> mapper) {
        return new ListCalculation<T, R>(this, mapper) {

            /**
             * {@inheritDoc}
             */
            @Override
            protected ObservableList<R> computeValue() {
                return new MappedList<>(CalculatableList.this, e -> mapper.apply(e).getValue());
            }
        };
    }

    default <R> CalculatableList<R> flatVariable(Function<T, Variable<R>> mapper) {
        return flatObservable(e -> new VariableBinding(mapper.apply(e)));
    }

    /**
     * Create new reduce binding.
     * 
     * @param init
     * @param accumulator
     * @return
     */
    default <R> Calculatable<R> reduce(R init, WiseBiFunction<R, T, R> accumulator) {
        return new LC<T, R>(this) {

            /**
             * {@inheritDoc}
             */
            @Override
            protected R computeValue() {
                return I.<T> signal(CalculatableList.this).scan(init, accumulator).to().v;
            }
        };
    }

    /**
     * Create new indexed item binding.
     * 
     * @param index
     * @return
     */
    default Calculatable<T> item(int index) {
        return Viewtify.calculate(this, () -> index < size() ? get(index) : null);
    }
}
