/*
 * Copyright (C) 2021 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.util;

import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.Function;

import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * @version 2018/09/11 9:45:06
 */
public class DelegatingProperty<Origin, Wrap> implements Property<Wrap> {

    /** The original property, */
    private final Property<Origin> origin;

    /** The value converter. */
    private final Function<Origin, Wrap> encoder;

    /** The value converter. */
    private final Function<Wrap, Origin> decoder;

    /** The listner mapping. */
    private final Map<InvalidationListener, InvalidationListener> invalidations = new WeakHashMap();

    /** The listner mapping. */
    private final Map<ChangeListener, ChangeListener> changes = new WeakHashMap();

    /**
     * @param origin
     * @param encoder
     * @param decoder
     */
    public DelegatingProperty(Property<Origin> origin, Function<Origin, Wrap> encoder, Function<Wrap, Origin> decoder) {
        Objects.requireNonNull(encoder);
        Objects.requireNonNull(decoder);

        this.origin = Objects.requireNonNull(origin);
        this.encoder = v -> v == null ? null : encoder.apply(v);
        this.decoder = v -> v == null ? null : decoder.apply(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(InvalidationListener listener) {
        InvalidationListener mapped = invalidations.get(listener);

        if (mapped == null) {
            mapped = o -> listener.invalidated(this);
            invalidations.put(listener, mapped);
            origin.addListener(mapped);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(InvalidationListener listener) {
        InvalidationListener removed = invalidations.remove(listener);

        if (removed != null) {
            origin.removeListener(removed);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(Wrap value) {
        origin.setValue(decoder.apply(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getBean() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return origin.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(ChangeListener<? super Wrap> listener) {
        ChangeListener<? super Origin> mapped = changes.get(listener);

        if (mapped == null) {
            mapped = (observable, oldValue, newValue) -> {
                listener.changed(this, encoder.apply(oldValue), encoder.apply(newValue));
            };
            changes.put(listener, mapped);
            origin.addListener(mapped);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(ChangeListener<? super Wrap> listener) {
        ChangeListener removed = changes.remove(listener);

        if (removed != null) {
            origin.removeListener(removed);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Wrap getValue() {
        return encoder.apply(origin.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bind(ObservableValue<? extends Wrap> observable) {
        // If this exception will be thrown, it is bug of this program. So we must rethrow the
        // wrapped error in here.
        throw new Error();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unbind() {
        origin.unbind();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBound() {
        return origin.isBound();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bindBidirectional(Property<Wrap> other) {
        // If this exception will be thrown, it is bug of this program. So we must rethrow the
        // wrapped error in here.
        throw new Error();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unbindBidirectional(Property<Wrap> other) {
        // If this exception will be thrown, it is bug of this program. So we must rethrow the
        // wrapped error in here.
        throw new Error();
    }
}