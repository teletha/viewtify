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

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import viewtify.Viewtify;

public class UIThreadSafeList<E> implements ObservableList<E> {

    private final ObservableList<E> list;

    private Map<InvalidationListener, InvalidationListener> invalidations;

    private Map<ListChangeListener, ListChangeListener> changes;

    /**
     * @param list
     */
    public UIThreadSafeList() {
        this(FXCollections.observableArrayList());
    }

    /**
     * @param list
     */
    public UIThreadSafeList(ObservableList<E> list) {
        this.list = list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void addListener(InvalidationListener listener) {
        if (invalidations == null) {
            invalidations = new HashMap();
        }
        list.addListener(invalidations.computeIfAbsent(listener, key -> change -> Viewtify.inUI(() -> key.invalidated(change))));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void removeListener(InvalidationListener listener) {
        if (invalidations != null && !invalidations.isEmpty()) {
            list.removeListener(invalidations.remove(listener));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void addListener(ListChangeListener<? super E> listener) {
        if (changes == null) {
            changes = new HashMap();
        }
        list.addListener(changes.computeIfAbsent(listener, key -> change -> Viewtify.inUI(() -> key.onChanged(change))));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void removeListener(ListChangeListener<? super E> listener) {
        if (changes != null && !changes.isEmpty()) {
            list.removeListener(changes.remove(listener));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void forEach(Consumer<? super E> action) {
        list.forEach(action);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addAll(E... elements) {
        return list.addAll(elements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setAll(E... elements) {
        return list.setAll(elements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setAll(Collection<? extends E> col) {
        return list.setAll(col);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeAll(E... elements) {
        return list.removeAll(elements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean retainAll(E... elements) {
        return list.retainAll(elements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(int from, int to) {
        list.remove(from, to);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FilteredList<E> filtered(Predicate<E> predicate) {
        return list.filtered(predicate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SortedList<E> sorted(Comparator<E> comparator) {
        return list.sorted(comparator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SortedList<E> sorted() {
        return list.sorted();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return list.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<E> iterator() {
        return list.iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(E e) {
        return list.add(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        return list.addAll(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return list.addAll(index, c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        list.replaceAll(operator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T[] toArray(IntFunction<T[]> generator) {
        return list.toArray(generator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sort(Comparator<? super E> c) {
        list.sort(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        list.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        return list.equals(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return list.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E get(int index) {
        return list.get(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E set(int index, E element) {
        return list.set(index, element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(int index, E element) {
        list.add(index, element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        return list.removeIf(filter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E remove(int index) {
        return list.remove(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListIterator<E> listIterator() {
        return list.listIterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListIterator<E> listIterator(int index) {
        return list.listIterator(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Spliterator<E> spliterator() {
        return list.spliterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<E> stream() {
        return list.stream();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<E> parallelStream() {
        return list.parallelStream();
    }

}
