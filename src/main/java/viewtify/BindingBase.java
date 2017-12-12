/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Binding;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * @version 2017/12/10 15:38:58
 */
public abstract class BindingBase<E> implements Binding<E> {

    /** The debuggable mode. */
    protected static final boolean debug = false;

    /** The listener holder. */
    private List<InvalidationListener> invalidationListeners;

    /** The listener holder. */
    private List<ChangeListener<? super E>> changeListeners;

    /** The listener holder. */
    private List<ListChangeListener<? super E>> listChangeListeners;

    /** The listener holder. */
    private List<Observable> dependencies;

    /** The listener holder. */
    private InvalidationListener dependencyListener;

    /** The value validity. */
    private final AtomicBoolean validity = new AtomicBoolean();

    /** The current value. */
    private E reference;

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void addListener(ChangeListener<? super E> listener) {
        if (listener != null) {
            if (changeListeners == null) {
                changeListeners = new CopyOnWriteArrayList();
            }
            changeListeners.add(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void removeListener(ChangeListener<? super E> listener) {
        if (listener != null) {
            if (changeListeners != null) {
                changeListeners.remove(listener);

                if (changeListeners.isEmpty()) {
                    changeListeners = null;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void addListener(InvalidationListener listener) {
        if (listener != null) {
            if (invalidationListeners == null) {
                invalidationListeners = new CopyOnWriteArrayList();
            }
            invalidationListeners.add(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void removeListener(InvalidationListener listener) {
        if (listener != null) {
            if (invalidationListeners != null) {
                invalidationListeners.remove(listener);

                if (invalidationListeners.isEmpty()) {
                    invalidationListeners = null;
                }
            }
        }
    }

    /**
     * Manage {@link ListChangeListener}.
     * 
     * @param listener
     */
    public synchronized void addListener(ListChangeListener<? super E> listener) {
        if (listener != null) {
            if (listChangeListeners == null) {
                listChangeListeners = new CopyOnWriteArrayList();
            }
            listChangeListeners.add(listener);
        }
    }

    /**
     * Manage {@link ListChangeListener}.
     * 
     * @param listener
     */
    public synchronized void removeListener(ListChangeListener<? super E> listener) {
        if (listener != null) {
            if (listChangeListeners != null) {
                listChangeListeners.remove(listener);

                if (listChangeListeners.isEmpty()) {
                    listChangeListeners = null;
                }
            }
        }
    }

    /**
     * Manage dependency {@link Observable}.
     * 
     * @param dependency
     */
    protected final synchronized void bind(Observable dependency) {
        if (dependency != null) {
            if (dependencies == null) {
                dependencies = new CopyOnWriteArrayList();
                dependencyListener = o -> invalidate();
            }
            dependencies.add(dependency);
            dependency.addListener(dependencyListener);
        }
    }

    /**
     * Manage dependency {@link Observable}.
     * 
     * @param dependency
     */
    protected final synchronized void unbind(Observable dependency) {
        if (dependency != null && dependencies != null) {
            dependencies.remove(dependency);
            dependency.removeListener(dependencyListener);

            if (dependencies.isEmpty()) {
                dependencies = null;
                dependencyListener = null;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        if (changeListeners != null) {
            changeListeners.forEach(this::removeListener);
        }

        if (invalidationListeners != null) {
            invalidationListeners.forEach(this::removeListener);
        }

        if (dependencies != null) {
            dependencies.forEach(this::unbind);
        }
        reference = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObservableList<?> getDependencies() {
        return FXCollections.emptyObservableList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid() {
        return validity.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void invalidate() {
        Viewtify.inUI(() -> {
            if (validity.compareAndSet(true, false)) {
                reference = null;

                if (debug) System.out.println(this + " is invalid now");

                if (invalidationListeners != null) {
                    for (InvalidationListener listener : invalidationListeners) {
                        listener.invalidated(this);
                    }
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E getValue() {
        if (validity.compareAndSet(false, true)) {
            reference = computeValue();
        }
        return reference;
    }

    /**
     * Compute the valid value.
     * 
     * @return
     */
    protected abstract E computeValue();
}
