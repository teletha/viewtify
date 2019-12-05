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
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;

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
    default <T> T valueOr(ObservableValue<T> defaultValue) {
        return valueOr(defaultValue.getValue());
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
    default <P extends ObservableValue<V> & WritableValue<V>> Self sync(P value) {
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
    default <P extends ObservableValue<V> & WritableValue<V>> Self sync(P value, Disposable unsynchronizer) {
        return sync(value, null, unsynchronizer);
    }

    /**
     * Synchronizes with the specified value.
     * 
     * @param value The value that is synchronized with each other.
     * @param synchronizer Synchronize at the specified timing.
     * @return Chainable API.
     */
    default <P extends ObservableValue<V> & WritableValue<V>> Self sync(P value, Function<Signal<V>, Signal<V>> synchronizer) {
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
    default <P extends ObservableValue<V> & WritableValue<V>> Self sync(P value, Function<Signal<V>, Signal<V>> synchronizer, Disposable unsynchronizer) {
        if (value != null) {
            sync(Viewtify.observeNow(value), value::setValue, synchronizer, unsynchronizer);
        }
        return (Self) this;
    }

    /**
     * This value will be synchronized from the specified value.
     * 
     * @param value The synchronized source.
     * @return Chainable API.
     */
    default Self syncFrom(ObservableValue<V> value) {
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
    default Self syncFrom(ObservableValue<V> value, Disposable unsynchronizer) {
        return syncFrom(value, null, unsynchronizer);
    }

    /**
     * This value will be synchronized from the specified value.
     * 
     * @param value The synchronized source.
     * @param synchronizer Synchronize at the specified timing.
     * @return Chainable API.
     */
    default Self syncFrom(ObservableValue<V> value, Function<Signal<V>, Signal<V>> synchronizer) {
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
    default Self syncFrom(ObservableValue<V> value, Function<Signal<V>, Signal<V>> synchronizer, Disposable unsynchronizer) {
        if (value != null) {
            sync(Viewtify.observeNow(value), null, synchronizer, unsynchronizer);
        }
        return (Self) this;
    }

    /**
     * This value will synchronize to the specified value.
     * 
     * @param value The synchronized target.
     * @return Chainable API.
     */
    default Self syncTo(WritableValue<V> value) {
        return syncTo(value, null, null);
    }

    /**
     * This value will synchronize to the specified value.
     * 
     * @param value The synchronized target.
     * @param unsynchronizer The synchronization is canceled by calling
     *            {@link Disposable#dispose()}.
     * @return Chainable API.
     */
    default Self syncTo(WritableValue<V> value, Disposable unsynchronizer) {
        return syncTo(value, null, unsynchronizer);
    }

    /**
     * This value will synchronize to the specified value.
     * 
     * @param value The synchronized target.
     * @param synchronizer Synchronize at the specified timing.
     * @return Chainable API.
     */
    default Self syncTo(WritableValue<V> value, Function<Signal<V>, Signal<V>> synchronizer) {
        return syncTo(value, synchronizer, null);
    }

    /**
     * This value will synchronize to the specified value.
     * 
     * @param value The synchronized target.
     * @param synchronizer Synchronize at the specified timing.
     * @param unsynchronizer The synchronization is canceled by calling
     *            {@link Disposable#dispose()}.
     * @return Chainable API.
     */
    default Self syncTo(WritableValue<V> value, Function<Signal<V>, Signal<V>> synchronizer, Disposable unsynchronizer) {
        if (value != null) {
            sync(null, value::setValue, synchronizer, unsynchronizer);
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
    default Self sync(Variable<V> value, Disposable unsynchronizer) {
        return sync(value, null, unsynchronizer);
    }

    /**
     * Synchronizes with the specified value.
     * 
     * @param value The value that is synchronized with each other.
     * @param synchronizer Synchronize at the specified timing.
     * @return Chainable API.
     */
    default Self sync(Variable<V> value, Function<Signal<V>, Signal<V>> synchronizer) {
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
    default Self sync(Variable<V> value, Function<Signal<V>, Signal<V>> synchronizer, Disposable unsynchronizer) {
        if (value != null) {
            sync(value.observeNow(), value::set, synchronizer, unsynchronizer);
        }
        return (Self) this;
    }

    /**
     * This value will be synchronized from the specified value.
     * 
     * @param value The synchronized source.
     * @return Chainable API.
     */
    default Self syncFrom(Variable<V> value) {
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
    default Self syncFrom(Variable<V> value, Disposable unsynchronizer) {
        return syncFrom(value, null, unsynchronizer);
    }

    /**
     * This value will be synchronized from the specified value.
     * 
     * @param value The synchronized source.
     * @param synchronizer Synchronize at the specified timing.
     * @return Chainable API.
     */
    default Self syncFrom(Variable<V> value, Function<Signal<V>, Signal<V>> synchronizer) {
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
    default Self syncFrom(Variable<V> value, Function<Signal<V>, Signal<V>> synchronizer, Disposable unsynchronizer) {
        if (value != null) {
            sync(value.observeNow(), null, synchronizer, unsynchronizer);
        }
        return (Self) this;
    }

    /**
     * This value will be synchronized from the specified value.
     * 
     * @param value The synchronized source.
     * @return Chainable API.
     */
    default Self syncFrom(Signal<V> value) {
        return syncFrom(value, null);
    }

    /**
     * This value will be synchronized from the specified value.
     * 
     * @param value The synchronized source.
     * @param unsynchronizer The synchronization is canceled by calling
     *            {@link Disposable#dispose()}.
     * @return Chainable API.
     */
    default Self syncFrom(Signal<V> value, Disposable unsynchronizer) {
        if (value != null) {
            sync(value, null, null, unsynchronizer);
        }
        return (Self) this;
    }

    /**
     * This value will synchronize to the specified value.
     * 
     * @param value The synchronized target.
     * @return Chainable API.
     */
    default Self syncTo(Consumer<V> value) {
        return syncTo(value, null, null);
    }

    /**
     * This value will synchronize to the specified value.
     * 
     * @param value The synchronized target.
     * @param unsynchronizer The synchronization is canceled by calling
     *            {@link Disposable#dispose()}.
     * @return Chainable API.
     */
    default Self syncTo(Consumer<V> value, Disposable unsynchronizer) {
        return syncTo(value, null, unsynchronizer);
    }

    /**
     * This value will synchronize to the specified value.
     * 
     * @param value The synchronized target.
     * @param synchronizer Synchronize at the specified timing.
     * @return Chainable API.
     */
    default Self syncTo(Consumer<V> value, Function<Signal<V>, Signal<V>> synchronizer) {
        return syncTo(value, synchronizer, null);
    }

    /**
     * This value will synchronize to the specified value.
     * 
     * @param value The synchronized target.
     * @param synchronizer Synchronize at the specified timing.
     * @param unsynchronizer The synchronization is canceled by calling
     *            {@link Disposable#dispose()}.
     * @return Chainable API.
     */
    default Self syncTo(Consumer<V> value, Function<Signal<V>, Signal<V>> synchronizer, Disposable unsynchronizer) {
        if (value != null) {
            sync(null, value, synchronizer, unsynchronizer);
        }
        return (Self) this;
    }

    /**
     * Synchronizes with the specified value.
     * 
     * @param publisher An external change event notifier.
     * @param receiver An external change event receiver.
     * @param synchronizer Synchronize at the specified timing.
     * @param unsynchronizer The synchronization is canceled by calling
     *            {@link Disposable#dispose()}.
     * @return Chainable API.
     */
    private void sync(Signal<V> publisher, Consumer<V> receiver, Function<Signal<V>, Signal<V>> synchronizer, Disposable unsynchronizer) {
        if (synchronizer == null) {
            synchronizer = Function.identity();
        }

        if (publisher != null) {
            Disposable from = publisher.plug(synchronizer).to((Consumer<V>) this::value);
            if (unsynchronizer != null) unsynchronizer.add(from);
        }

        if (receiver != null) {
            Disposable to = (publisher == null ? observeNow() : observe()).plug(synchronizer).to(receiver);
            if (unsynchronizer != null) unsynchronizer.add(to);
        }
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
