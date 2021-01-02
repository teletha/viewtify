/*
 * Copyright (C) 2021 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.property;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableObjectValue;

/**
 * High performance and thread-safe {@link Property}.
 */
public class SmartProperty<E> implements Property<E>, WritableObjectValue<E> {

    /** The owner. */
    private final Object owner;

    /** The property name. */
    private final String name;

    /** The current value. */
    private E value;

    /** The first invalidation listener. */
    private InvalidationListener invalidate1;

    /** The second invalidation listener. */
    private InvalidationListener invalidate2;

    /** The third invalidation listener. */
    private Set<InvalidationListener> invalidates;

    /** The first invalidation listener. */
    private ChangeListener<? super E> change1;

    /** The second change listener. */
    private ChangeListener<? super E> change2;

    /** The third change listener. */
    private Set<ChangeListener> changes;

    /** The current binding. */
    private Consumer<Property> binding;

    /**
     * 
     */
    public SmartProperty() {
        this(null, null);
    }

    /**
     * @param owner
     * @param name
     */
    public SmartProperty(Object owner, String name) {
        this.owner = owner;
        this.name = Objects.requireNonNullElse(name, "");
    }

    /**
     * @param initialValue Value to set.
     */
    public SmartProperty(E initialValue) {
        this(null, null);
        this.value = initialValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E get() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(E value) {
        setValue(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(E value) {
        if (this.value != value) {
            E old = this.value;
            this.value = value;

            if (invalidate1 != null) {
                invalidate1.invalidated(this);
            }
            if (invalidate2 != null) {
                invalidate2.invalidated(this);
            }
            if (invalidates != null) {
                for (InvalidationListener listener : invalidates) {
                    listener.invalidated(this);
                }
            }
            if (change1 != null) {
                change1.changed(this, old, value);
            }
            if (change2 != null) {
                change2.changed(this, old, value);
            }
            if (changes != null) {
                for (ChangeListener listener : changes) {
                    listener.changed(this, old, value);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getBean() {
        return owner;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void addListener(ChangeListener<? super E> listener) {
        if (change1 == null || change1 == listener) {
            change1 = listener;
        } else if (change2 == null || change2 == listener) {
            change2 = listener;
        } else {
            if (changes == null) {
                changes = new CopyOnWriteArraySet();
            }
            changes.add(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void removeListener(ChangeListener<? super E> listener) {
        if (change1 == listener) {
            change1 = null;
        } else if (change2 == listener) {
            change2 = null;
        } else if (changes != null) {
            changes.remove(listener);
            if (changes.isEmpty()) {
                changes = null;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void addListener(InvalidationListener listener) {
        if (invalidate1 == null || invalidate1 == listener) {
            invalidate1 = listener;
        } else if (invalidate2 == null || invalidate2 == listener) {
            invalidate2 = listener;
        } else {
            if (invalidates == null) {
                invalidates = new CopyOnWriteArraySet();
            }
            invalidates.add(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void removeListener(InvalidationListener listener) {
        if (invalidate1 == listener) {
            invalidate1 = null;
        } else if (invalidate2 == listener) {
            invalidate2 = null;
        } else if (invalidates != null) {
            invalidates.remove(listener);
            if (invalidates.isEmpty()) {
                invalidates = null;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBound() {
        return binding != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void bind(ObservableValue<? extends E> target) {
        unbind();

        if (target != null) {
            ChangeListener<E> tracking = (o, old, now) -> setValue(now);
            target.addListener(tracking);
            binding = other -> target.removeListener(tracking);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void unbind() {
        if (binding != null) {
            binding.accept(this);
            binding = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void bindBidirectional(Property<E> target) {
        unbind();

        if (target != null) {
            ChangeListener<E> tracking = (o, old, now) -> {
                if (o == this) {
                    target.setValue(now);
                } else {
                    setValue(now);
                }
            };
            addListener(tracking);
            target.addListener(tracking);
            binding = other -> {
                if (other == target) {
                    removeListener(tracking);
                    target.removeListener(tracking);
                }
            };
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void unbindBidirectional(Property<E> other) {
        if (binding != null) {
            binding.accept(other);
            binding = null;
        }
    }
}