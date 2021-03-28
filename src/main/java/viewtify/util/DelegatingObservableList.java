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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.collections.WeakListChangeListener;

/**
 * @version 2018/08/23 20:11:00
 */
public abstract class DelegatingObservableList<E> extends ObservableListBase<E> implements ObservableList<E> {

    /** The actual list. */
    protected final ObservableList<E> delegate;

    /**
     * @param delegate A target {@link ObservableList} to delegate.
     */
    public DelegatingObservableList(ObservableList<E> delegate) {
        this.delegate = Objects.requireNonNull(delegate);
        this.delegate.addListener(new WeakListChangeListener<E>(this::sourceChanged));
    }

    /**
     * Notify source change event.
     * 
     * @param change A source change event.
     */
    protected abstract void sourceChanged(ListChangeListener.Change<? extends E> change);

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeAll(E... elements) {
        return delegate.removeAll(elements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(int from, int to) {
        delegate.remove(from, to);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E remove(int index) {
        return delegate.remove(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return delegate.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int lastIndexOf(Object o) {
        return delegate.lastIndexOf(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addAll(E... elements) {
        return delegate.addAll(elements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return delegate.subList(fromIndex, toIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E set(int index, E element) {
        return delegate.set(index, element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(int index, E element) {
        delegate.add(index, element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        return delegate.containsAll(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        delegate.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<E> iterator() {
        return delegate.iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        return delegate.removeAll(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T[] toArray(T[] a) {
        return delegate.toArray(a);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(Object o) {
        return delegate.remove(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        return delegate.addAll(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean retainAll(E... elements) {
        return delegate.retainAll(elements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        return delegate.retainAll(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setAll(Collection<? extends E> col) {
        return delegate.setAll(col);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListIterator<E> listIterator(int index) {
        return delegate.listIterator(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(E e) {
        return delegate.add(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return delegate.addAll(index, c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListIterator<E> listIterator() {
        return delegate.listIterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E get(int index) {
        return delegate.get(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setAll(E... elements) {
        return delegate.setAll(elements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int indexOf(Object o) {
        return delegate.indexOf(o);
    }
}