/*
 * Copyright (C) 2021 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.helper;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

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
import viewtify.ui.UserInterface;

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
     * Initialize with the specified value. This value is automatically saved whenever it is
     * changed, and is restored the next time it is initialized.
     * 
     * @param initialValue The initial value is mandatory, null values are not accepted.
     * @return Chainable API.
     */
    default Self initialize(V initialValue) {
        if (initialValue == null) {
            initialValue = value();
        }

        Objects.requireNonNull(initialValue);

        try {
            UserInterface ui = (UserInterface) this;
            Method method = UserInterface.class.getDeclaredMethod("restore", Property.class, Object.class);
            method.setAccessible(true);
            method.invoke(ui, valueProperty(), initialValue);
        } catch (Throwable e) {
            throw I.quiet(e);
        }
        return (Self) this;
    }

    /**
     * Initialize with the specified value. This value is automatically saved whenever it is
     * changed, and is restored the next time it is initialized.
     * 
     * @param initialValue The initial value is mandatory, null values are not accepted.
     * @return Chainable API.
     */
    default Self initialize(Variable<V> initialValue) {
        return initialize(initialValue.v);
    }

    /**
     * Initialize with the specified value. This value is automatically saved whenever it is
     * changed, and is restored the next time it is initialized.
     * 
     * @param initialValue The initial value is mandatory, null values are not accepted.
     * @return Chainable API.
     */
    default Self initialize(ObservableValue<V> initialValue) {
        return initialize(initialValue.getValue());
    }

    /**
     * Initialize with the specified value. This value is automatically saved whenever it is
     * changed, and is restored the next time it is initialized.
     * 
     * @param initialValue The initial value is mandatory, null values are not accepted.
     * @return Chainable API.
     */
    default Self initializeLazy(Signal<V> initialValue) {
        if (initialValue != null) {
            initialValue.first().to(this::initialize);
        }
        return (Self) this;
    }

    /**
     * Checks if this value is the same as the specified value.
     * 
     * @param value A value to test.
     * @return A result.
     */
    default boolean is(V value) {
        return value() == value;
    }

    /**
     * Checks if this value is the same as the specified value.
     * 
     * @param condition A condition.
     * @return A result.
     */
    default boolean is(Predicate<V> condition) {
        return condition.test(value());
    }

    /**
     * Checks if this value is NOT the same as the specified value.
     * 
     * @param value A value to test.
     * @return A result.
     */
    default boolean isNot(V value) {
        return value() != value;
    }

    /**
     * Checks if this value is NOT the same as the specified value.
     * 
     * @param condition A condition.
     * @return A result.
     */
    default boolean isNot(Predicate<V> condition) {
        return !condition.test(value());
    }

    /**
     * It is defined as an alias for {@link #observe()}.
     * 
     * @return
     */
    default Signal<V> isChanged() {
        return observe();
    }

    /**
     * Synchronizes with the specified value.
     * 
     * @param value The value that is synchronized with each other.
     * @return Chainable API.
     */
    default Self sync(ValueHelper<?, V> value) {
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
    default Self sync(ValueHelper<?, V> value, Disposable unsynchronizer) {
        return sync(value, null, unsynchronizer);
    }

    /**
     * Synchronizes with the specified value.
     * 
     * @param value The value that is synchronized with each other.
     * @param synchronizer Synchronize at the specified timing.
     * @return Chainable API.
     */
    default Self sync(ValueHelper<?, V> value, Function<Signal<V>, Signal<V>> synchronizer) {
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
    default Self sync(ValueHelper<?, V> value, Function<Signal<V>, Signal<V>> synchronizer, Disposable unsynchronizer) {
        return sync(value.valueProperty(), synchronizer, unsynchronizer);
    }

    /**
     * Synchronizes with the specified value.
     * 
     * @param value The value that is synchronized with each other.
     * @return Chainable API.
     */
    default <P extends WritableValue<V> & ObservableValue<V>> Self sync(P value) {
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
    default <P extends WritableValue<V> & ObservableValue<V>> Self sync(P value, Disposable unsynchronizer) {
        return sync(value, null, unsynchronizer);
    }

    /**
     * Synchronizes with the specified value.
     * 
     * @param value The value that is synchronized with each other.
     * @param synchronizer Synchronize at the specified timing.
     * @return Chainable API.
     */
    default <P extends WritableValue<V> & ObservableValue<V>> Self sync(P value, Function<Signal<V>, Signal<V>> synchronizer) {
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
    default <P extends WritableValue<V> & ObservableValue<V>> Self sync(P value, Function<Signal<V>, Signal<V>> synchronizer, Disposable unsynchronizer) {
        if (value != null) {
            sync(Viewtify.observing(value), value::setValue, synchronizer, unsynchronizer);
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
            sync(Viewtify.observing(value), null, synchronizer, unsynchronizer);
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
            sync(value.observing(), value::set, synchronizer, unsynchronizer);
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
            sync(value.observing(), null, synchronizer, unsynchronizer);
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
            Disposable to = (publisher == null ? observing() : observe()).plug(synchronizer).to(receiver);
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
     * @param listener A modification listener.
     * @return Chainable API.
     */
    default Self observe(WiseRunnable listener) {
        return observe(listener, null);
    }

    /**
     * Observe the value modification.
     * 
     * @param listener A modification listener.
     * @param disposer The modification listening is canceled by calling
     *            {@link Disposable#dispose()}.
     * @return Chainable API.
     */
    default Self observe(WiseRunnable listener, Disposable disposer) {
        if (listener != null) {
            Disposable stop = observe().to(listener);
            if (disposer != null) disposer.add(stop);
        }
        return (Self) this;
    }

    /**
     * Observe the value modification.
     * 
     * @param listener A modification listener.
     * @return Chainable API.
     */
    default Self observe(WiseConsumer<V> listener) {
        return observe(listener, null);
    }

    /**
     * Observe the value modification.
     * 
     * @param listener A modification listener.
     * @param disposer The modification listening is canceled by calling
     *            {@link Disposable#dispose()}.
     * @return Chainable API.
     */
    default Self observe(WiseConsumer<V> listener, Disposable disposer) {
        return observe(Function.identity(), listener, disposer);
    }

    /**
     * Observe the value modification.
     * 
     * @param timing A event timing coordinator.
     * @param listener A modification listener.
     * @return Chainable API.
     */
    default <R> Self observe(Function<Signal<V>, Signal<R>> timing, WiseConsumer<R> listener) {
        return observe(timing, listener, null);
    }

    /**
     * Observe the value modification.
     * 
     * @param timing A event timing coordinator.
     * @param listener A modification listener.
     * @param disposer The modification listening is canceled by calling
     *            {@link Disposable#dispose()}.
     * @return Chainable API.
     */
    default <R> Self observe(Function<Signal<V>, Signal<R>> timing, WiseConsumer<R> listener, Disposable disposer) {
        if (listener != null) {
            Disposable stop = timing.apply(observe()).to(listener);
            if (disposer != null) disposer.add(stop);
        }
        return (Self) this;
    }

    /**
     * Observe the value modification.
     * 
     * @param listener A modification listener.
     * @return Chainable API.
     */
    default Self observe(WiseBiConsumer<V, V> listener) {
        return observe(listener, null);
    }

    /**
     * Observe the value modification.
     * 
     * @param listener A modification listener.
     * @param disposer The modification listening is canceled by calling
     *            {@link Disposable#dispose()}.
     * @return Chainable API.
     */
    default Self observe(WiseBiConsumer<V, V> listener, Disposable disposer) {
        if (listener != null) {
            Disposable stop = observe().maps(value(), (p, v) -> I.pair(p, v)).to(v -> listener.accept(v.ⅰ, v.ⅱ));
            if (disposer != null) disposer.add(stop);
        }
        return (Self) this;
    }

    /**
     * Observe the value modification starting with the current value.
     * 
     * @return A {@link Signal} that notify the change of this value.
     */
    default Signal<V> observing() {
        return observe().startWith(value()).skipNull();
    }

    /**
     * Observe the value modification starting with the current value.
     * 
     * @param listener A modification listener.
     * @return Chainable API.
     */
    default Self observing(WiseRunnable listener) {
        return observing(listener, null);
    }

    /**
     * Observe the value modification starting with the current value.
     * 
     * @param listener A modification listener.
     * @param disposer The modification listening is canceled by calling
     *            {@link Disposable#dispose()}.
     * @return Chainable API.
     */
    default Self observing(WiseRunnable listener, Disposable disposer) {
        if (listener != null) {
            Disposable stop = observing().to(listener);
            if (disposer != null) disposer.add(stop);
        }
        return (Self) this;
    }

    /**
     * Observe the value modification starting with the current value.
     * 
     * @param listener A modification listener.
     * @return Chainable API.
     */
    default Self observing(WiseConsumer<V> listener) {
        return observing(listener, null);
    }

    /**
     * Observe the value modification starting with the current value.
     * 
     * @param listener A modification listener.
     * @param disposer The modification listening is canceled by calling
     *            {@link Disposable#dispose()}.
     * @return Chainable API.
     */
    default Self observing(WiseConsumer<V> listener, Disposable disposer) {
        return observing(Function.identity(), listener, disposer);
    }

    /**
     * Observe the value modification starting with the current value.
     * 
     * @param timing A event timing coordinator.
     * @param listener A modification listener.
     * @return Chainable API.
     */
    default <R> Self observing(Function<Signal<V>, Signal<R>> timing, WiseConsumer<R> listener) {
        return observing(timing, listener, null);
    }

    /**
     * Observe the value modification starting with the current value.
     * 
     * @param timing A event timing coordinator.
     * @param listener A modification listener.
     * @param disposer The modification listening is canceled by calling
     *            {@link Disposable#dispose()}.
     * @return Chainable API.
     */
    default <R> Self observing(Function<Signal<V>, Signal<R>> timing, WiseConsumer<R> listener, Disposable disposer) {
        if (listener != null) {
            Disposable stop = timing.apply(observing()).to(listener);
            if (disposer != null) disposer.add(stop);
        }
        return (Self) this;
    }

    /**
     * Observe the value modification starting with the current value.
     * 
     * @param listener A modification listener.
     * @return Chainable API.
     */
    default Self observing(WiseBiConsumer<V, V> listener) {
        return observing(listener, null);
    }

    /**
     * Observe the value modification starting with the current value.
     * 
     * @param listener A modification listener.
     * @param disposer The modification listening is canceled by calling
     *            {@link Disposable#dispose()}.
     * @return Chainable API.
     */
    default Self observing(WiseBiConsumer<V, V> listener, Disposable disposer) {
        if (listener != null) {
            Disposable stop = observe().maps(value(), (p, v) -> I.pair(p, v)).to(v -> listener.accept(v.ⅰ, v.ⅱ));
            if (disposer != null) disposer.add(stop);
        }
        return (Self) this;
    }
}