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

import java.util.concurrent.CopyOnWriteArrayList;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import kiss.Variable;

/**
 * @version 2017/11/26 22:05:44
 */
public class ObservableVariable<V> implements ObservableValue<V> {

    private final CopyOnWriteArrayList<InvalidationListener> listeners = new CopyOnWriteArrayList();

    private final Variable<V> var;

    /**
     * @param var
     * @param listeners
     */
    public ObservableVariable(Variable var) {
        this.var = var;

        var.observe().to(v -> {
            for (InvalidationListener listener : listeners) {
                listener.invalidated(this);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(ChangeListener<? super V> listener) {
        // If this exception will be thrown, it is bug of this program. So we must rethrow the
        // wrapped error in here.
        throw new Error();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(ChangeListener<? super V> listener) {
        // If this exception will be thrown, it is bug of this program. So we must rethrow the
        // wrapped error in here.
        throw new Error();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V getValue() {
        return var.v;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(InvalidationListener listener) {
        listeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(InvalidationListener listener) {
        listeners.remove(listener);
    }
}