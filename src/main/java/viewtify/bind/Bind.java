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

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;

import kiss.Variable;
import viewtify.Viewtify;

/**
 * Adds monadic operations to {@link ObservableValue}.
 */
public interface Bind<T> extends ObservableObjectValue<T> {

    /**
     * Returns an {@code Optional} describing the value currently held by this ObservableValue, or
     * and empty {@code Optional} if this ObservableValue is empty.
     */
    default Optional<T> asOptional() {
        return Optional.ofNullable(getValue());
    }

    /**
     * Returns an {@code Variable} describing the value currently held by this ObservableValue, or
     * and empty {@code Variable} if this ObservableValue is empty.
     */
    default Variable<T> asVariable() {
        return Variable.of(getValue());
    }

    /**
     * Returns a new ObservableValue that holds the value held by this ObservableValue, or
     * {@code other} when this ObservableValue is empty.
     */
    default Bind<T> or(T other) {
        return Viewtify.bind(this, () -> asVariable().or(other).v);
    }

    /**
     * Returns a new ObservableValue that holds the value held by this ObservableValue, or the value
     * held by {@code other} when this ObservableValue is empty.
     */
    default Bind<T> or(ObservableValue<T> other) {
        return new FirstNonNullBinding<>(this, other);
    }

    /**
     * Returns a new ObservableValue that holds the same value as this ObservableValue when the
     * value satisfies the predicate and is empty when this ObservableValue is empty or its value
     * does not satisfy the given predicate.
     */
    default Bind<T> filter(Predicate<T> condition) {
        return Viewtify.bind(this, () -> asVariable().is(condition, Function.identity()).v);
    }

    /**
     * Returns a new ObservableValue that holds a mapping of the value held by this ObservableValue,
     * and is empty when this ObservableValue is empty.
     * 
     * @param mapper function to map the value held by this ObservableValue.
     */
    default <U> Bind<U> map(Function<? super T, ? extends U> mapper) {
        return Viewtify.bind(this, () -> asVariable().map(mapper).v);
    }

    /**
     * Returns a new ObservableValue that holds a mapping of the value held by this ObservableValue,
     * and is empty when this ObservableValue is empty.
     * 
     * @param f function to map the value held by this ObservableValue.
     */
    default <U> Bind<U> flatVariable(Function<? super T, Variable<U>> f) {
        return new FlatMapBinding<>(this, a -> Viewtify.wrap(f.apply(a)));
    }

    /**
     * Returns a new ObservableValue that, when this ObservableValue holds value {@code x}, holds
     * the value held by {@code f(x)}, and is empty when this ObservableValue is empty.
     */
    default <U> Bind<U> flatMap(Function<? super T, ObservableValue<U>> f) {
        return new FlatMapBinding<>(this, f);
    }

    /**
     * Similar to {@link #flatMap(Function)}, except the returned Binding is also a Property. This
     * means you can call {@code setValue()} and {@code bind()} methods on the returned value, which
     * delegate to the currently selected Property.
     * <p>
     * As the value of this ObservableValue changes, so does the selected Property. When the
     * Property returned from this method is bound, as the selected Property changes, the previously
     * selected property is unbound and the newly selected property is bound.
     * <p>
     * Note that if the currently selected property is {@code null}, then calling {@code getValue()}
     * on the returned value will return {@code null} regardless of any prior call to
     * {@code setValue()} or {@code bind()}.
     * <p>
     * Note that you need to retain a reference to the returned value to prevent it from being
     * garbage collected.
     */
    default <U> PropertyBinding<U> selectProperty(Function<? super T, Property<U>> f) {
        return new FlatMapProperty<>(this, f);
    }

    /**
     * Adds an invalidation listener and returns a Subscription that can be used to remove that
     * listener. <pre>
     * {@code
     * Subscription s = observable.subscribe(obs -> doSomething());
     *
     * // later
     * s.unsubscribe();
     * }</pre> is equivalent to <pre>
     * {@code
     * InvalidationListener l = obs -> doSomething();
     * observable.addListener(l);
     *
     * // later
     * observable.removeListener();
     * }</pre>
     */
    default Subscription subscribe(InvalidationListener listener) {
        addListener(listener);
        return () -> removeListener(listener);
    }

    /**
     * Adds a change listener and returns a Subscription that can be used to remove that listener.
     * See the example at {@link #subscribe(InvalidationListener)}.
     */
    default Subscription subscribe(ChangeListener<? super T> listener) {
        addListener(listener);
        return () -> removeListener(listener);
    }
}
