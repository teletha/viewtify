/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * @version 2018/09/11 16:24:02
 */
public class RegulatableProperty<V> implements Property<V> {

    /** The base property. */
    private final Property<V> base;

    /** The conditions. */
    private final List<Predicate<V>> regulations = new ArrayList();

    /**
     * @param base
     */
    public RegulatableProperty(Property<V> base) {
        this.base = Objects.requireNonNull(base);
    }

    /**
     * Register the regulation.
     * 
     * @param regulation
     */
    public void ensure(Predicate<V> regulation) {
        if (regulation != null) {
            regulations.add(regulation);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getBean() {
        return base.getBean();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bind(ObservableValue<? extends V> observable) {
        base.bind(observable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return base.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(V value) {
        for (Predicate<V> regulation : regulations) {
            if (!regulation.test(value)) {
                return;
            }
        }
        base.setValue(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unbind() {
        base.unbind();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(InvalidationListener listener) {
        base.addListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBound() {
        return base.isBound();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bindBidirectional(Property<V> other) {
        base.bindBidirectional(other);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(InvalidationListener listener) {
        base.removeListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unbindBidirectional(Property<V> other) {
        base.unbindBidirectional(other);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(ChangeListener<? super V> listener) {
        base.addListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(ChangeListener<? super V> listener) {
        base.removeListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V getValue() {
        return base.getValue();
    }

}
