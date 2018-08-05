/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.helper;

import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javafx.beans.property.Property;

import kiss.I;
import kiss.Signal;
import kiss.Variable;
import kiss.WiseBiConsumer;
import kiss.WiseConsumer;
import kiss.WiseFunction;
import viewtify.Viewtify;
import viewtify.ui.UserInterface;

/**
 * @version 2018/08/05 12:22:03
 */
public interface PreferenceHelper<Self extends PreferenceHelper, V> extends Supplier<V>, Consumer<V> {

    /**
     * The preference.
     * 
     * @return
     */
    Property<V> model();

    /**
     * {@inheritDoc}
     */
    @Override
    default V get() {
        return value();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default void accept(V value) {
        value(value);
    }

    /**
     * The preference value.
     * 
     * @return
     */
    default V value() {
        return model().getValue();
    }

    /**
     * Set the current value.
     * 
     * @param value
     * @return
     */
    default Self value(V value) {
        model().setValue(value);
        return (Self) this;
    }

    /**
     * Set the current value.
     * 
     * @param value
     * @return
     */
    default Self value(WiseFunction<V, V> setter) {
        if (setter != null) {
            model().setValue(setter.apply(value()));
        }
        return (Self) this;
    }

    /**
     * Set initial value.
     * 
     * @param initialValue
     * @return
     */
    default Self initial(V initialValue) {
        if (this instanceof UserInterface) {
            try {
                UserInterface ui = (UserInterface) this;
                Method method = UserInterface.class.getDeclaredMethod("restore", Property.class, Object.class);
                method.setAccessible(true);
                method.invoke(ui, model(), initialValue);
            } catch (Exception e) {
                throw I.quiet(e);
            }
        }
        return (Self) this;
    }

    /**
     * This preference synchronizes with the specified value.
     */
    default Self model(Property<V> value) {
        if (value != null) {
            Property<V> pref = model();
            pref.unbind();
            pref.setValue(value.getValue());
            pref.bind(value);
        }
        return (Self) this;
    }

    /**
     * This preference synchronizes with the specified value.
     */
    default Self model(Variable<V> value) {
        if (value != null) {
            Property<V> pref = model();
            pref.setValue(value.get());
            model().addListener((source, oldValue, newValue) -> {
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
        return Viewtify.signal(model()).skipNull();
    }

    /**
     * Observe the value modification.
     * 
     * @param listener
     * @return
     */
    default Self observe(WiseConsumer<V> listener) {
        model().addListener((p, o, n) -> listener.accept(n));
        return (Self) this;
    }

    /**
     * Observe the value modification.
     * 
     * @param listener
     * @return
     */
    default Self observe(WiseBiConsumer<V, V> listener) {
        model().addListener((p, o, n) -> listener.accept(o, n));
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
    default Self observeNow(WiseConsumer<V> listener) {
        observe(listener);
        listener.accept(model().getValue());
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
        listener.accept(null, model().getValue());
        return (Self) this;
    }

    default <P> Self restrictS(Function<Signal<V>, Signal<Boolean>> condition) {

        return (Self) this;
    }
}
