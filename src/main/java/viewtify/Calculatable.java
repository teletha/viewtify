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

import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import javafx.beans.Observable;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.sun.javafx.collections.ImmutableObservableList;

import kiss.I;
import kiss.Variable;

/**
 * @version 2017/12/06 13:25:40
 */
public abstract class Calculatable<T> extends ObjectBinding<T> {

    /** Store for {@link #dispose()}. */
    private final Observable[] dependencies;

    /**
     * @param dependencies
     */
    public Calculatable(Observable... dependencies) {
        bind(this.dependencies = I.signal(dependencies).skipNull().toList().toArray(new Observable[0]));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        unbind(dependencies);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final T computeValue() {
        try {
            return calculate();
        } catch (Throwable e) {
            return null;
        }
    }

    protected abstract T calculate();

    /**
     * {@inheritDoc}
     */
    @Override
    public final ObservableList<?> getDependencies() {
        return ((dependencies == null) || (dependencies.length == 0)) ? FXCollections.emptyObservableList()
                : (dependencies.length == 1) ? FXCollections.singletonObservableList(dependencies[0])
                        : new ImmutableObservableList<Observable>(dependencies);
    }

    /**
     * Type filtering.
     * 
     * @param type
     * @return
     */
    public <R> Calculatable<R> as(Class<R> type) {
        return (Calculatable<R>) take(v -> type.isInstance(v));
    }

    /**
     * Returns an {@code Variable} describing the value currently held by this ObservableValue, or
     * and empty {@code Variable} if this ObservableValue is empty.
     */
    public Variable<T> asVariable() {
        return Variable.of(getValue());
    }

    /**
     * Returns a new ObservableValue that, when this ObservableValue holds value {@code x}, holds
     * the value held by {@code f(x)}, and is empty when this ObservableValue is empty.
     */
    public <R> Calculatable<R> calculateProperty(Function<? super T, ObservableValue<R>> mapper) {
        return new Calculatable<R>(this) {

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
    public <R> Calculatable<R> calculateVariable(Function<? super T, Variable<R>> mapper) {
        return calculateProperty(v -> new VariableBinding(mapper.apply(v)));
    }

    /**
     * Equality check.
     * 
     * @param active
     * @return
     */
    public Calculatable<Boolean> is(T... values) {
        return isNot(I.set(values));
    }

    /**
     * Equality check.
     * 
     * @param active
     * @return
     */
    public Calculatable<Boolean> is(Set<T> values) {
        return map(v -> values.contains(v));
    }

    /**
     * Equality check.
     * 
     * @param active
     * @return
     */
    public Calculatable<Boolean> isNot(T... values) {
        return isNot(I.set(values));
    }

    /**
     * Equality check.
     * 
     * @param active
     * @return
     */
    public Calculatable<Boolean> isNot(Set<T> values) {
        return map(v -> !values.contains(v));
    }

    /**
     * Equality check.
     */
    public Calculatable<Boolean> isAbsent() {
        return map(v -> v == null);
    }

    /**
     * Equality check.
     */
    public Calculatable<Boolean> isPresent() {
        return map(v -> v != null);
    }

    /**
     * Returns a new ObservableValue that holds a mapping of the value held by this ObservableValue,
     * and is empty when this ObservableValue is empty.
     * 
     * @param mapper function to map the value held by this ObservableValue.
     */
    public <R> Calculatable<R> map(Function<? super T, ? extends R> mapper) {
        return Viewtify.calculate(this, () -> asVariable().map(mapper).v);
    }

    /**
     * Returns a new ObservableValue that holds a mapping of the value held by this ObservableValue,
     * and is empty when this ObservableValue is empty.
     * 
     * @param mapper function to map the value held by this ObservableValue.
     */
    public <R> Calculatable<R> mapTo(R constant) {
        return Viewtify.calculate(this, () -> constant);
    }

    /**
     * Returns a new ObservableValue that holds the value held by this ObservableValue, or
     * {@code other} when this ObservableValue is empty.
     */
    public Calculatable<T> or(T other) {
        return Viewtify.calculate(this, () -> asVariable().or(other).v);
    }

    /**
     * Returns a new ObservableValue that holds the value held by this ObservableValue, or the value
     * held by {@code other} when this ObservableValue is empty.
     */
    public Calculatable<T> or(ObservableValue<T> other) {
        return Viewtify.calculate(this, other, () -> asVariable().or(other::getValue).v);
    }

    /**
     * Returns a new ObservableValue that holds the same value as this ObservableValue when the
     * value satisfies the predicate and is empty when this ObservableValue is empty or its value
     * does not satisfy the given predicate.
     */
    public Calculatable<T> take(Predicate<T> condition) {
        return Viewtify.calculate(this, () -> asVariable().is(condition, Function.identity()).v);
    }
}
