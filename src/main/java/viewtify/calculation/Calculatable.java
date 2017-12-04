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

import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;

import kiss.I;
import kiss.Variable;
import viewtify.Viewtify;

/**
 * @version 2017/12/04 10:12:20
 */
public interface Calculatable<T> extends ObservableObjectValue<T> {

    /**
     * Type filtering.
     * 
     * @param type
     * @return
     */
    default <R> Calculatable<R> as(Class<R> type) {
        return (Calculatable<R>) take(v -> type.isInstance(v));
    }

    /**
     * Returns an {@code Variable} describing the value currently held by this ObservableValue, or
     * and empty {@code Variable} if this ObservableValue is empty.
     */
    default Variable<T> asVariable() {
        return Variable.of(getValue());
    }

    /**
     * Returns a new ObservableValue that, when this ObservableValue holds value {@code x}, holds
     * the value held by {@code f(x)}, and is empty when this ObservableValue is empty.
     */
    default <R> Calculatable<R> calculateProperty(Function<? super T, ObservableValue<R>> mapper) {
        return new Calculation<R>(this) {

            /** The latest mapper value. */
            private ObservableValue<R> latest;

            /**
             * {@inheritDoc}
             */
            @Override
            protected R calculate() {
                if (latest != null) {
                    unbind(latest);
                }

                latest = mapper.apply(Calculatable.this.get());

                if (latest == null) {
                    return null;
                } else {
                    bind(latest);
                    return latest.getValue();
                }
            }
        };
    }

    /**
     * Returns a new ObservableValue that holds a mapping of the value held by this ObservableValue,
     * and is empty when this ObservableValue is empty.
     * 
     * @param mapper function to map the value held by this ObservableValue.
     */
    default <R> Calculatable<R> calculateVariable(Function<? super T, Variable<R>> mapper) {
        return calculateProperty(v -> new VariableBinding(mapper.apply(v)));
    }

    /**
     * Equality check.
     * 
     * @param active
     * @return
     */
    default Calculatable<Boolean> is(T... values) {
        return isNot(I.set(values));
    }

    /**
     * Equality check.
     * 
     * @param active
     * @return
     */
    default Calculatable<Boolean> is(Set<T> values) {
        return map(v -> values.contains(v));
    }

    /**
     * Equality check.
     * 
     * @param active
     * @return
     */
    default Calculatable<Boolean> isNot(T... values) {
        return isNot(I.set(values));
    }

    /**
     * Equality check.
     * 
     * @param active
     * @return
     */
    default Calculatable<Boolean> isNot(Set<T> values) {
        return map(v -> !values.contains(v));
    }

    /**
     * Equality check.
     */
    default Calculatable<Boolean> isAbsent() {
        return map(v -> v == null);
    }

    /**
     * Equality check.
     */
    default Calculatable<Boolean> isPresent() {
        return map(v -> v != null);
    }

    /**
     * Returns a new ObservableValue that holds a mapping of the value held by this ObservableValue,
     * and is empty when this ObservableValue is empty.
     * 
     * @param mapper function to map the value held by this ObservableValue.
     */
    default <R> Calculatable<R> map(Function<? super T, ? extends R> mapper) {
        return Viewtify.calculate(this, () -> asVariable().map(mapper).v);
    }

    /**
     * Returns a new ObservableValue that holds a mapping of the value held by this ObservableValue,
     * and is empty when this ObservableValue is empty.
     * 
     * @param mapper function to map the value held by this ObservableValue.
     */
    default <R> Calculatable<R> mapTo(R constant) {
        return Viewtify.calculate(this, () -> constant);
    }

    /**
     * Returns a new ObservableValue that holds the value held by this ObservableValue, or
     * {@code other} when this ObservableValue is empty.
     */
    default Calculatable<T> or(T other) {
        return Viewtify.calculate(this, () -> asVariable().or(other).v);
    }

    /**
     * Returns a new ObservableValue that holds the value held by this ObservableValue, or the value
     * held by {@code other} when this ObservableValue is empty.
     */
    default Calculatable<T> or(ObservableValue<T> other) {
        return Viewtify.calculate(this, other, () -> asVariable().or(other::getValue).v);
    }

    /**
     * Returns a new ObservableValue that holds the same value as this ObservableValue when the
     * value satisfies the predicate and is empty when this ObservableValue is empty or its value
     * does not satisfy the given predicate.
     */
    default Calculatable<T> take(Predicate<T> condition) {
        return Viewtify.calculate(this, () -> asVariable().is(condition, Function.identity()).v);
    }
}
