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

import javafx.beans.Observable;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.sun.javafx.collections.ImmutableObservableList;

import kiss.I;

/**
 * @version 2017/12/03 11:41:00
 */
public abstract class Calculation<T> extends ObjectBinding<T> implements Calculatable<T> {

    /** Store for {@link #dispose()}. */
    private final Observable[] dependencies;

    /**
     * @param dependencies
     */
    public Calculation(Observable... dependencies) {
        bind(this.dependencies = I.signal(dependencies).skipNull().toList().toArray(new Observable[0]));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void dispose() {
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
}