/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
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
import java.util.function.Supplier;

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
import viewtify.edit.Edito;
import viewtify.ui.UserInterface;
import viewtify.util.GuardedOperation;

public interface ValueHelper<Self extends ValueHelper, V> extends Supplier<V> {

    /**
     * Return the current model.
     * 
     * @return
     */
    Property<V> valueProperty();

    /**
     * {@inheritDoc}
     */
    @Override
    default V get() {
        return value();
    }

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
        Viewtify.inUI(() -> valueProperty().setValue(value));
        return (Self) this;
    }

    /**
     * Updates to the specified value.
     * 
     * @param value A new value to set.
     * @return Chainable API.
     */
    default Self value(Variable<V> value) {
        return value(value.v);
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
     * Update and maintain an edit history of this value with default context.
     * 
     * @param value
     * @return
     */
    default Self historicalValue(Variable<V> value) {
        return historicalValue(value, (WiseConsumer<V>) null);
    }

    /**
     * Update and maintain an edit history of this value with default context.
     * 
     * @param value
     * @return
     */
    default Self historicalValue(Variable<V> value, WiseRunnable save) {
        return historicalValue(value, I.wiseC(save));
    }

    /**
     * Update and maintain an edit history of this value with default context.
     * 
     * @param value
     * @return
     */
    default Self historicalValue(Variable<V> value, WiseConsumer<V> save) {
        value(value);
        return historical(v -> {
            value.set(v);
            if (save != null) save.accept(v);
        });
    }

    /**
     * Maintain an edit history of this value with default context.
     * 
     * @param save
     * @return
     */
    default Self historical(WiseConsumer<V> save) {
        return historical(save, null);
    }

    /**
     * Maintains an edit history of this value with your context.
     * 
     * @param save
     * @return
     */
    default <X extends StyleHelper & ValueHelper<X, V>> Self historical(WiseConsumer<V> save, Edito context) {
        if (save != null) {
            if (context == null) context = Edito.Root;
            context.manageValue((X) this, save);
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
     * Check whether this value is null or not.
     * 
     * @return
     */
    default Signal<Boolean> isNull() {
        return observing().is(Objects::isNull);
    }

    /**
     * Check whether this value is null or not.
     * 
     * @return
     */
    default Signal<Boolean> isNotNull() {
        return observing().is(v -> v != null);
    }

    /**
     * Synchronizes with the specified value.
     * 
     * @param value The value that is synchronized with each other.
     * @return Chainable API.
     */
    default Self sync(ValueHelper<?, V> value) {
        return sync(value, (Function<Signal<V>, Signal<V>>) null, null);
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
     * @param from Value synchronizer from external value to internal model.
     * @param to Value synchronizer from intenral model to external value.
     * @return Chainable API.
     */
    default <X> Self sync(ValueHelper<?, X> value, WiseFunction<X, V> from, WiseFunction<V, X> to) {
        return sync(value, from, to, null);
    }

    /**
     * Synchronizes with the specified value.
     * 
     * @param value The value that is synchronized with each other.
     * @param from Value synchronizer from external value to internal model.
     * @param to Value synchronizer from intenral model to external value.
     * @param unsynchronizer The synchronization is canceled by calling
     *            {@link Disposable#dispose()}.
     * @return Chainable API.
     */
    default <X> Self sync(ValueHelper<?, X> value, WiseFunction<X, V> from, WiseFunction<V, X> to, Disposable unsynchronizer) {
        return sync(value.valueProperty(), from, to, unsynchronizer);
    }

    /**
     * Synchronizes with the specified value.
     * 
     * @param value The value that is synchronized with each other.
     * @return Chainable API.
     */
    default <P extends WritableValue<V> & ObservableValue<V>> Self sync(P value) {
        return sync(value, (Function<Signal<V>, Signal<V>>) null, null);
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
     * Synchronizes with the specified value.
     * 
     * @param value The value that is synchronized with each other.
     * @param from Value synchronizer from external value to internal model.
     * @param to Value synchronizer from intenral model to external value.
     * @return Chainable API.
     */
    default <P extends WritableValue<X> & ObservableValue<X>, X> Self sync(P value, WiseFunction<X, V> from, WiseFunction<V, X> to) {
        return sync(value, from, to, null);
    }

    /**
     * Synchronizes with the specified value.
     * 
     * @param value The value that is synchronized with each other.
     * @param from Value synchronizer from external value to internal model.
     * @param to Value synchronizer from intenral model to external value.
     * @param unsynchronizer The synchronization is canceled by calling
     *            {@link Disposable#dispose()}.
     * @return Chainable API.
     */
    default <P extends WritableValue<X> & ObservableValue<X>, X> Self sync(P value, WiseFunction<X, V> from, WiseFunction<V, X> to, Disposable unsynchronizer) {
        if (value != null) {
            sync(Viewtify.observing(value), value::setValue, x -> x.map(nullable(from)), v -> v.map(nullable(to)), unsynchronizer);
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
        return sync(value, (Function<Signal<V>, Signal<V>>) null, (Disposable) null);
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
     * Synchronizes with the specified value.
     * 
     * @param value The value that is synchronized with each other.
     * @param from Value synchronizer from external value to internal model.
     * @param to Value synchronizer from intenral model to external value.
     * @return Chainable API.
     */
    default <X> Self sync(Variable<X> value, WiseFunction<X, V> from, WiseFunction<V, X> to) {
        return sync(value, from, to, null);
    }

    /**
     * Synchronizes with the specified value.
     * 
     * @param value The value that is synchronized with each other.
     * @param from Value synchronizer from external value to internal model.
     * @param to Value synchronizer from intenral model to external value.
     * @param unsynchronizer The synchronization is canceled by calling
     *            {@link Disposable#dispose()}.
     * @return Chainable API.
     */
    default <X> Self sync(Variable<X> value, WiseFunction<X, V> from, WiseFunction<V, X> to, Disposable unsynchronizer) {
        if (value != null) {
            sync(value.observing(), value::set, x -> x.map(nullable(from)), v -> v.map(nullable(to)), unsynchronizer);
        }
        return (Self) this;
    }

    /**
     * Compose the function which accepts null safety.
     * 
     * @param <S>
     * @param <T>
     * @param fun
     * @return
     */
    private <S, T> WiseFunction<S, T> nullable(WiseFunction<S, T> fun) {
        return v -> v == null ? null : fun.apply(v);
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
     */
    private void sync(Signal<V> publisher, Consumer<V> receiver, Function<Signal<V>, Signal<V>> synchronizer, Disposable unsynchronizer) {
        if (synchronizer == null) {
            synchronizer = Function.identity();
        }
        sync(publisher, receiver, synchronizer, synchronizer, unsynchronizer);
    }

    /**
     * Synchronizes with the specified value.
     * 
     * @param publisher An external change event notifier.
     * @param receiver An external change event receiver.
     * @param fromSynchronizer Value synchronizer from external value to internal model.
     * @param toSynchronizer Value synchronizer from intenral model to external value.
     * @param unsynchronizer The synchronization is canceled by calling
     *            {@link Disposable#dispose()}.
     */
    private <X> void sync(Signal<X> publisher, Consumer<X> receiver, Function<Signal<X>, Signal<V>> fromSynchronizer, Function<Signal<V>, Signal<X>> toSynchronizer, Disposable unsynchronizer) {
        GuardedOperation updating = publisher == null || receiver == null ? GuardedOperation.NoOP : new GuardedOperation();

        if (publisher != null) {
            Disposable from = publisher.plug(fromSynchronizer).to(v -> {
                updating.guard(() -> {
                    value(v);
                });
            });
            if (unsynchronizer != null) unsynchronizer.add(from);
        }

        if (receiver != null) {
            Disposable to = (publisher == null ? observing() : observe()).plug(toSynchronizer).to(v -> {
                updating.guard(() -> {
                    receiver.accept(v);
                });
            });
            if (unsynchronizer != null) unsynchronizer.add(to);
        }
    }

    /**
     * Observe the value modification.
     * 
     * @return A {@link Signal} that notify the change of this value.
     */
    default Signal<V> observe() {
        return observe(false);
    }

    /**
     * Observe the value modification.
     * 
     * @return A {@link Signal} that notify the change of this value.
     */
    default Signal<V> observe(boolean ensureValid) {
        Signal<V> signal = Viewtify.observe(valueProperty());

        if (!ensureValid) {
            return signal;
        }

        signal = signal.skipNull();

        if (this instanceof VerifyHelper == false) {
            return signal;
        }

        return signal.take(((VerifyHelper) this).isValid());
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
            Disposable stop = observe().pair(value()).to(v -> listener.accept(v.ⅰ, v.ⅱ));
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
        return observing(false);
    }

    /**
     * Observe the value modification starting with the current value.
     * 
     * @return A {@link Signal} that notify the change of this value.
     */
    default Signal<V> observing(boolean ensureValid) {
        Signal<V> signal = Viewtify.observe(valueProperty()).startWith(value());

        if (!ensureValid) {
            return signal;
        }

        signal = signal.skipNull();

        if (this instanceof VerifyHelper == false) {
            return signal;
        }

        return signal.take(((VerifyHelper) this).isValid());
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
            Disposable stop = observe().pair(value()).to(v -> listener.accept(v.ⅰ, v.ⅱ));
            if (disposer != null) disposer.add(stop);
        }
        return (Self) this;
    }
}