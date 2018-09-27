/*
 * Copyright (C) 2018 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.model;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import kiss.Signal;
import kiss.Signaling;
import kiss.Storable;

/**
 * @version 2018/04/28 8:37:47
 */
public abstract class Sequential<T> implements Iterable<T> {

    /** The internal list. */
    private List<T> items = new CopyOnWriteArrayList();

    /** The event signal. */
    private final Signaling<T> adds = new Signaling();

    /** The event signal. */
    public transient final Signal<T> add = adds.expose;

    /** The event signal. */
    private final Signaling<T> removes = new Signaling();

    /** The event signal. */
    public transient final Signal<T> remove = removes.expose;

    /**
     * 
     * 
     */
    protected Sequential() {
        if (this instanceof Storable) {
            add.merge(remove).mapTo(this).as(Storable.class).to(Storable::store);
        }
    }

    /**
     * Get the items property of this {@link Sequential}.
     * 
     * @return The items property.
     */
    List<T> getItems() {
        return items;
    }

    /**
     * Set the items property of this {@link Sequential}.
     * 
     * @param items The items value to set.
     */
    void setItems(List<T> items) {
        this.items = items;
    }

    /**
     * {@link List#size()}
     */
    public final int size() {
        return items.size();
    }

    /**
     * {@link List#isEmpty()}
     */
    public final boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Iterator<T> iterator() {
        return items.iterator();
    }

    /**
     * {@link List#add(Object)}
     */
    public final boolean add(T e) {
        items.add(e);
        adds.accept(e);
        return true;
    }

    /**
     * {@link List#remove(Object)}
     */
    public final boolean remove(T o) {
        int index = items.indexOf(o);
        if (index == -1) {
            return true;
        }
        removes.accept(o);
        items.remove(index);
        return true;
    }

    /**
     * {@link List#clear()}
     */
    public final void clear() {
        for (T item : items) {
            removes.accept(item);
        }
        items.clear();
    }

    /**
     * {@link List#get(int)}
     */
    public final T get(int index) {
        return items.get(index);
    }

    /**
     * {@link List#set(int, Object)}
     */
    public final T set(int index, T element) {
        T previous = items.get(index);
        removes.accept(previous);
        items.set(index, element);
        adds.accept(element);
        return previous;
    }

    /**
     * {@link List#add(int, Object)}
     */
    public final void add(int index, T element) {
        items.add(index, element);
        adds.accept(element);
    }

    /**
     * {@link List#remove(int)}
     */
    public final T remove(int index) {
        T item = items.get(index);
        removes.accept(item);
        return items.remove(index);
    }

    /**
     * {@link List#indexOf(Object)}
     */
    public final int indexOf(Object o) {
        return items.indexOf(o);
    }

    /**
     * {@link List#lastIndexOf(Object)}
     */
    public final int lastIndexOf(Object o) {
        return items.lastIndexOf(o);
    }
}
