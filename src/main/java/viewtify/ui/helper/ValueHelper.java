/*
 * Copyright (C) 2019 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.helper;

import java.util.function.Consumer;
import java.util.function.Function;

import javafx.beans.property.Property;

import kiss.Disposable;
import kiss.I;
import kiss.Signal;
import kiss.Variable;
import kiss.WiseBiConsumer;
import kiss.WiseConsumer;
import kiss.WiseFunction;
import kiss.WiseRunnable;
import viewtify.Viewtify;

public interface ValueHelper<Self extends ValueHelper, V> {

    /**
     * Return the current model.
     * 
     * @return
     */
    Property<V> valueProperty();

    /**
     * Get the current value.
     * 
     * @return The current value.
     */
    default V value() {
        return valueProperty().getValue();
    }

    /**
     * Get the current value as the specified type.
     * 
     * @param type A value type to transform.
     * @return A transformed value.
     */
    default <T> T valueAs(Class<T> type) {
        return I.transform(value(), type);
    }

    /**
     * Get the current value or default value.
     * 
     * @param defaultValue A default value.
     * @return A preference value.
     */
    default <T> T valueOr(T defaultValue) {
        try {
            return valueAs((Class<T>) defaultValue.getClass());
        } catch (Throwable e) {
            return defaultValue;
        }
    }

    /**
     * Get the current value or default value.
     * 
     * @param defaultValue A default value.
     * @return A preference value.
     */
    default <T> T valueOr(Variable<T> defaultValue) {
        return valueOr(defaultValue.v);
    }

    /**
     * Updates to the specified value.
     * 
     * @param value A new value to set.
     * @return Chainable API.
     */
    default Self value(V value) {
        valueProperty().setValue(value);
        return (Self) this;
    }

    /**
     * Updates to the specified value.
     * 
     * @param setter Calculates a new value based on the current value.
     * @return Chainable API.
     */
    default Self value(WiseFunction<V, V> setter) {
        if (setter != null) {
            value(setter.apply(value()));
        }
        return (Self) this;
    }

    /**
     * Synchronizes with the specified value.
     * 
     * @param value The value that is synchronized with each other.
     * @return Chainable API.
     */
    default Self sync(Property<V> value) {
        return sync(value, null, null);
    }

    /**
     * Synchronizes with the specified value.
     * 
     * @param value The value that is synchronized with each other.
     * @param unsynchronizer The synchronization is canceled by calling
     *            {@link Disposable#dispose()}.
     * @return Chainable API.
     */
    default Self sync(Property<V> value, Disposable unsynchronizer) {
        return sync(value, null, unsynchronizer);
    }

    /**
     * Synchronizes with the specified value.
     * 
     * @param value The value that is synchronized with each other.
     * @param synchronizer Synchronize at the specified timing.
     * @return Chainable API.
     */
    default Self sync(Property<V> value, Function<Signal<V>, Signal<V>> synchronizer) {
        return sync(value, synchronizer, null);
    }

    /**
     * Synchronizes with the specified value.
     * 
     * @param value The value that is synchronized with each other.
     * @param synchronizer Synchronize at the specified timing.
     * @param unsynchronizer The synchronization is canceled by calling
     *            {@link Disposable#dispose()}.
     * @return Chainable API.
     */
    default Self sync(Property<V> value, Function<Signal<V>, Signal<V>> synchronizer, Disposable unsynchronizer) {
        if (value != null) {
            if (synchronizer == null) {
                synchronizer = Function.identity();
            }

            Disposable from = Viewtify.observeNow(value).plug(synchronizer).to((Consumer<V>) this::value);
            Disposable to = observe().plug(synchronizer).to(value::setValue);

            if (unsynchronizer != null) {
                unsynchronizer.add(from).add(to);
            }
        }
        return (Self) this;
    }

    /**
     * This value will be synchronized from the specified value.
     * 
     * @param value The synchronized source.
     * @return Chainable API.
     */
    default Self syncFrom(Property<V> value) {
        return syncFrom(value, null, null);
    }

    /**
     * This value will be synchronized from the specified value.
     * 
     * @param value The synchronized source.
     * @param unsynchronizer The synchronization is canceled by calling
     *            {@link Disposable#dispose()}.
     * @return Chainable API.
     */
    default Self syncFrom(Property<V> value, Disposable unsynchronizer) {
        return syncFrom(value, null, unsynchronizer);
    }

    /**
     * This value will be synchronized from the specified value.
     * 
     * @param value The synchronized source.
     * @param synchronizer Synchronize at the specified timing.
     * @return Chainable API.
     */
    default Self syncFrom(Property<V> value, Function<Signal<V>, Signal<V>> synchronizer) {
        return syncFrom(value, synchronizer, null);
    }

    /**
     * This value will be synchronized from the specified value.
     * 
     * @param value The synchronized source.
     * @param synchronizer Synchronize at the specified timing.
     * @param unsynchronizer The synchronization is canceled by calling
     *            {@link Disposable#dispose()}.
     * @return Chainable API.
     */
    default Self syncFrom(Property<V> value, Function<Signal<V>, Signal<V>> synchronizer, Disposable unsynchronizer) {
        if (value != null) {
            if (synchronizer == null) {
                synchronizer = Function.identity();
            }

            Disposable from = Viewtify.observeNow(value).plug(synchronizer).to((Consumer<V>) this::value);

            if (unsynchronizer != null) {
                unsynchronizer.add(from);
            }
        }
        return (Self) this;
    }

    /**
     * This value will be synchronized from the specified value.
     * 
     * @param value The synchronized source.
     * @return Chainable API.
     */
    default Self syncTo(Property<V> value) {
        return syncTo(value, null, null);
    }

    /**
     * This value will be synchronized from the specified value.
     * 
     * @param value The synchronized source.
     * @param unsynchronizer The synchronization is canceled by calling
     *            {@link Disposable#dispose()}.
     * @return Chainable API.
     */
    default Self syncTo(Property<V> value, Disposable unsynchronizer) {
        return syncTo(value, null, unsynchronizer);
    }

    /**
     * This value will be synchronized from the specified value.
     * 
     * @param value The synchronized source.
     * @param synchronizer Synchronize at the specified timing.
     * @return Chainable API.
     */
    default Self syncTo(Property<V> value, Function<Signal<V>, Signal<V>> synchronizer) {
        return syncTo(value, synchronizer, null);
    }

    /**
     * This value will be synchronized from the specified value.
     * 
     * @param value The synchronized source.
     * @param synchronizer Synchronize at the specified timing.
     * @param unsynchronizer The synchronization is canceled by calling
     *            {@link Disposable#dispose()}.
     * @return Chainable API.
     */
    default Self syncTo(Property<V> value, Function<Signal<V>, Signal<V>> synchronizer, Disposable unsynchronizer) {
        if (value != null) {
            if (synchronizer == null) {
                synchronizer = Function.identity();
            }

            Disposable to = observeNow().plug(synchronizer).to(value::setValue);

            if (unsynchronizer != null) {
                unsynchronizer.add(to);
            }
        }
        return (Self) this;
    }

    /**
     * Synchronizes with the specified value.
     * 
     * @param value The value that is synchronized with each other.
     * @return Chainable API.
     */
    default Self sync(Variable<V> value) {
        if (value != null) {
            Property<V> pref = valueProperty();
            pref.setValue(value.get());
            valueProperty().addListener((source, oldValue, newValue) -> {
                value.set(newValue);
            });
        }
        return (Self) this;
    }

    /**
     * Observe the value modification.
     * 
     * @return A {@link Signal} that notify the change of this value.
     */
    default Signal<V> observe() {
        return Viewtify.observe(valueProperty()).skipNull();
    }

    /**
     * Observe the value modification.
     * 
     * @param listener
     * @return
     */
    default Self observe(WiseRunnable listener) {
        valueProperty().addListener((p, o, n) -> listener.run());
        return (Self) this;
    }

    /**
     * Observe the value modification.
     * 
     * @param listener
     * @return
     */
    default Self observe(WiseConsumer<V> listener) {
        valueProperty().addListener((p, o, n) -> listener.accept(n));
        return (Self) this;
    }

    /**
     * Observe the value modification.
     * 
     * @param listener
     * @return
     */
    default Self observe(WiseBiConsumer<V, V> listener) {
        valueProperty().addListener((p, o, n) -> listener.accept(o, n));
        return (Self) this;
    }

    /**
     * Observe the value modification.
     * 
     * @return A {@link Signal} that notify the change of this value.
     */
    default Signal<V> observeNow() {
        return observe().startWith(value()).skipNull();
    }

    /**
     * Observe the value modification.
     * 
     * @param listener
     * @return
     */
    default Self observeNow(WiseRunnable listener) {
        observe(listener);
        listener.run();
        return (Self) this;
    }

    /**
     * Observe the value modification.
     * 
     * @param listener
     * @return
     */
    default Self observeNow(WiseConsumer<V> listener) {
        observe(listener);
        listener.accept(valueProperty().getValue());
        return (Self) this;
    }

    /**
     * Observe the value modification.
     * 
     * @param listener
     * @return
     */
    default Self observeNow(WiseBiConsumer<V, V> listener) {
        observe(listener);
        listener.accept(null, valueProperty().getValue());
        return (Self) this;
    }
}
