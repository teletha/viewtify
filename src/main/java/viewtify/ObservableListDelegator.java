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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Binding;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * @version 2017/12/11 0:57:14
 */
public class ObservableListDelegator<E> implements ObservableList<E> {

    private final Binding<ObservableList<E>> base;

    /**
     * @param base
     */
    public ObservableListDelegator(Binding<ObservableList<E>> base) {
        this.base = base;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(ListChangeListener<? super E> listener) {
        base.getValue().addListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(ListChangeListener<? super E> listener) {
        base.getValue().removeListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(InvalidationListener listener) {
        base.getValue().addListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addAll(E... elements) {
        return base.getValue().addAll(elements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setAll(E... elements) {
        return base.getValue().setAll(elements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setAll(Collection<? extends E> col) {
        return base.getValue().setAll(col);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeAll(E... elements) {
        return base.getValue().removeAll(elements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(InvalidationListener listener) {
        base.getValue().removeListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean retainAll(E... elements) {
        return base.getValue().retainAll(elements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(int from, int to) {
        base.getValue().remove(from, to);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return base.getValue().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return base.getValue().isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(Object o) {
        return base.getValue().contains(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<E> iterator() {
        return base.getValue().iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] toArray() {
        return base.getValue().toArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T[] toArray(T[] a) {
        return base.getValue().toArray(a);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(E e) {
        return base.getValue().add(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(Object o) {
        return base.getValue().remove(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        return base.getValue().containsAll(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        return base.getValue().addAll(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return base.getValue().addAll(index, c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        return base.getValue().removeAll(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        return base.getValue().retainAll(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        base.getValue().clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        return base.getValue().equals(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return base.getValue().hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E get(int index) {
        return base.getValue().get(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E set(int index, E element) {
        return base.getValue().set(index, element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(int index, E element) {
        base.getValue().add(index, element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E remove(int index) {
        return base.getValue().remove(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int indexOf(Object o) {
        return base.getValue().indexOf(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int lastIndexOf(Object o) {
        return base.getValue().lastIndexOf(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListIterator<E> listIterator() {
        return base.getValue().listIterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListIterator<E> listIterator(int index) {
        return base.getValue().listIterator(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return base.getValue().subList(fromIndex, toIndex);
    }

}
