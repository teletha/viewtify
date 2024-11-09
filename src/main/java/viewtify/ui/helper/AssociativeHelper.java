/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.helper;

import java.util.Objects;

import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.Node;

import kiss.Disposable;
import kiss.I;
import kiss.Variable;
import kiss.WiseFunction;
import kiss.WiseSupplier;
import viewtify.Viewtify;

public interface AssociativeHelper {

    /**
     * Return the JavaFX's UI component.
     * 
     * @return A UI component
     */
    Object ui();

    /**
     * Get the user data.
     * 
     * @param <T>
     * @param key
     */
    default <T> Variable<T> get(Class<T> key) {
        return get(key, null);
    }

    /**
     * Get the user data.
     * 
     * @param <T>
     * @param key
     * @param name
     */
    default <T> Variable<T> get(Class<T> key, String name) {
        if (key == null) {
            return Variable.empty();
        }
        return Variable.of((T) map(ui()).get(Objects.hash(key, name)));
    }

    /**
     * Store the user data.
     * 
     * @param <T>
     * @param key
     */
    default <T> T exact(Class<T> key) {
        return exact(key, null);
    }

    /**
     * Store the user data.
     * 
     * @param <T>
     * @param key
     */
    default <T> T exact(Class<T> key, String name) {
        if (key == null) {
            return null;
        }
        return ensure(key, name, () -> I.make(key), false);
    }

    /**
     * Store the user data.
     * 
     * @param <T>
     * @param key
     * @param value
     */
    default <T> void set(Class<T> key, Object value) {
        set(key, null, value);
    }

    /**
     * Store the user data.
     * 
     * @param <T>
     * @param key
     * @param value
     */
    default <T> void set(Class<T> key, String name, Object value) {
        if (key != null) {
            if (key.isInstance(value)) {
                ensure(key, name, () -> (T) value, true);
            }
        }
    }

    /**
     * Replace the user data.
     * 
     * @param <T>
     * @param key
     */
    default <T> void replace(Class<T> key, T value) {
        replace(key, null, value);
    }

    /**
     * Replace the user data.
     * 
     * @param <T>
     * @param key
     */
    default <T> void replace(Class<T> key, String name, T value) {
        dispose(key, name);
        set(key, name, value);
    }

    /**
     * Dispose the user data.
     * 
     * @param key
     */
    default void dispose(Class key) {
        dispose(key, null);
    }

    /**
     * Dispose the user data.
     * 
     * @param key
     */
    default void dispose(Class key, String name) {
        if (key != null) {
            ObservableMap data = map(ui());
            Object removed = data.remove(Objects.hash(key, name));
            if (removed instanceof Disposable disposable) {
                disposable.dispose();
            }
        }
    }

    /**
     * Store the user data.
     * 
     * @param <T>
     * @param object
     * @param key
     * @param supplier
     * @param override
     * @return
     */
    private <T> T ensure(Class<T> key, String name, WiseSupplier<T> supplier, boolean override) {
        Objects.requireNonNull(key, "Key type is required.");
        Objects.requireNonNull(supplier, "Value supplier is required.");

        ObservableMap data = map(ui());
        Integer hash = Objects.hash(key, name);
        Object value = data.get(hash);
        if (value == null || override) {
            value = supplier.get();
            if (!key.isInstance(value)) {
                throw new IllegalStateException("Assigned value [" + value + "] is not instance of " + key + ".");
            }
            data.put(hash, value);
        }
        return (T) value;
    }

    /**
     * Find data holder.
     * 
     * @param object
     * @return
     */
    private static ObservableMap map(Object object) {
        if (object instanceof Node node) {
            return node.getProperties();
        } else {
            return ReferenceHolder.ASSOCIATIVE.computeIfAbsent(object, key -> FXCollections.observableHashMap());
        }
    }

    default <S, V> void bind(Property<V> property, Variable<S> source, WiseFunction<S, V> converter) {
        if (source == null) {
            dispose(Disposable.class, property.getName());
        } else {
            Disposable disposable = source.observing().on(Viewtify.UIThread).map(converter).to(property::setValue);
            replace(Disposable.class, property.getName(), disposable);
        }
    }

    static AssociativeHelper of(Object object) {
        if (object instanceof AssociativeHelper) {
            return (AssociativeHelper) object;
        } else {
            return () -> object;
        }
    }
}
