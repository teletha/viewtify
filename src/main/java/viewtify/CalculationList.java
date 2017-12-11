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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.function.Predicate;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import kiss.I;
import kiss.Variable;
import kiss.WiseBiFunction;

/**
 * @version 2017/12/09 13:14:23
 */
public class CalculationList<E> extends BindingBase<ObservableList<E>> implements ObservableList<E> {

    private final String name;

    /** The source list. */
    private final ObservableList<E> source;

    /** The selector list. */
    private List<Function<E, ? extends Observable>> observableSelectors;

    /** The listener holder. */
    private List<ListChangeListener<? super E>> changeListeners;

    /** The event delegator. */
    private final ListChangeListener<E> delegationListener = change -> {
        for (ListChangeListener<? super E> listener : changeListeners) {
            listener.onChanged(change);
        }
    };

    private final InvalidationListener itemListener = o -> {
        updateItem(o);
    };

    /** The item change observer. */
    private final ListChangeListener<E> itemChangeListener = change -> {
        while (change.next()) {
            if (observableSelectors != null) {
                if (change.wasRemoved()) {
                    change.getRemoved().forEach(e -> observableSelectors.forEach(s -> unbind(s.apply(e))));
                }

                if (change.wasAdded()) {
                    change.getAddedSubList().forEach(e -> observableSelectors.forEach(s -> bind(s.apply(e))));
                }
            }
            invalidate();
        }
    };

    /**
     * @param name
     * @param source
     */
    CalculationList(String name, ObservableList<E> source, Observable... dependencies) {
        this.name = name;
        this.source = source;

        bind(source);

        for (Observable dependency : dependencies) {
            bind(dependency);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ObservableList<E> computeValue() {
        System.out.println(source);
        return source;
    }

    /**
     * Register {@link Observable} property selector.
     * 
     * @param selector
     * @return
     */
    public final synchronized CalculationList<E> checkObservable(Function<E, ? extends Observable> selector) {
        if (selector != null) {
            if (observableSelectors == null) {
                observableSelectors = new CopyOnWriteArrayList<>();
                source.addListener(itemChangeListener);
            }

            if (!observableSelectors.contains(selector)) {
                observableSelectors.add(selector);
                source.forEach(e -> {
                    bind(selector.apply(e));
                });
            }
        }
        return this;
    }

    private void updateItem(E item) {
        int index = source.indexOf(item);

        Change<E> change = new Change<E>(source) {

            private boolean nextCalled = false;

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean next() {
                if (nextCalled == true) {
                    return false;
                } else {
                    nextCalled = false;
                    return true;
                }
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void reset() {
                nextCalled = false;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public int getFrom() {
                return index;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public int getTo() {
                return index + 1;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public List<E> getRemoved() {
                return Collections.EMPTY_LIST;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            protected int[] getPermutation() {
                return new int[0];
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean wasUpdated() {
                return true;
            }
        };

        if (changeListeners != null) {
            for (ListChangeListener<? super E> listener : changeListeners) {
                listener.onChanged(change);
            }
        }
    }

    /**
     * Create mapped {@link CalculationList} for {@link ObservableValue}.
     * 
     * @param mapper
     * @return
     */
    public <R> CalculationList<R> flatObservable(Function<E, ObservableValue<R>> mapper) {
        checkObservable(mapper);
        return Viewtify.calculate("flatObservable", new MappedList<>(this, e -> mapper.apply(e).getValue()));
    }

    /**
     * Create mapped {@link CalculationList} for {@link Variable}.
     * 
     * @param mapper
     * @return
     */
    public <R> CalculationList<R> flatVariable(Function<E, Variable<R>> mapper) {
        checkObservable(mapper);
        return Viewtify.calculate("flatVariable", new MappedList<>(this, e -> {
            return mapper.apply(e).get();
        }));
    }

    public Calculation<Boolean> isNot(E value) {
        return new Calculation<Boolean>(() -> {
            return !getValue().contains(value);
        }, null, this);
    }

    /**
     * Create new indexed item binding.
     * 
     * @param index
     * @return
     */
    public Calculation<E> item(int index) {
        return Viewtify.calculate(this, () -> {
            List<E> value = getValue();
            return index < value.size() ? value.get(index) : null;
        });
    }

    /**
     * Create mapped {@link CalculationList}.
     * 
     * @param mapper
     * @return
     */
    public <R> CalculationList<R> map(Function<E, R> mapper) {
        return Viewtify.calculate("map", new MappedList<>(this, mapper));
    }

    /**
     * Create new reduce binding.
     * 
     * @param init
     * @param accumulator
     * @return
     */
    public <R> Calculation<R> reduce(R init, WiseBiFunction<R, E, R> accumulator) {
        return new Calculation<R>(() -> {
            return I.signal(getValue()).scan(init, accumulator).to().v;
        }, null, this);
    }

    /**
     * Filter element by the specified condiiton.
     * 
     * @param condition
     * @return
     */
    public CalculationList<E> skip(Predicate<E> condition) {
        return take(condition.negate());
    }

    /**
     * Filter element by the specified condiiton.
     * 
     * @param condition
     * @return
     */
    public CalculationList<E> take(Predicate<E> condition) {
        return new CalculationList<E>("take", new FilteredList(this, condition), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void addListener(ListChangeListener<? super E> listener) {
        if (listener != null) {
            if (changeListeners == null) {
                changeListeners = new CopyOnWriteArrayList();
                source.addListener(delegationListener);
            }
            changeListeners.add(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void removeListener(ListChangeListener<? super E> listener) {
        if (listener != null) {
            if (changeListeners != null) {
                changeListeners.remove(listener);

                if (changeListeners.isEmpty()) {
                    source.removeListener(delegationListener);
                    changeListeners = null;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addAll(E... elements) {
        return getValue().addAll(elements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setAll(E... elements) {
        return getValue().setAll(elements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setAll(Collection<? extends E> col) {
        return getValue().setAll(col);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeAll(E... elements) {
        return getValue().removeAll(elements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean retainAll(E... elements) {
        return getValue().retainAll(elements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(int from, int to) {
        getValue().remove(from, to);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return getValue().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return getValue().isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(Object o) {
        return getValue().contains(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<E> iterator() {
        return getValue().iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] toArray() {
        return getValue().toArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T[] toArray(T[] a) {
        return getValue().toArray(a);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(E e) {
        return getValue().add(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(Object o) {
        return getValue().remove(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        return getValue().containsAll(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        return getValue().addAll(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return getValue().addAll(index, c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        return getValue().removeAll(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        return getValue().retainAll(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        getValue().clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        return getValue().equals(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return getValue().hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E get(int index) {
        return getValue().get(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E set(int index, E element) {
        return getValue().set(index, element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(int index, E element) {
        getValue().add(index, element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E remove(int index) {
        return getValue().remove(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int indexOf(Object o) {
        return getValue().indexOf(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int lastIndexOf(Object o) {
        return getValue().lastIndexOf(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListIterator<E> listIterator() {
        return getValue().listIterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListIterator<E> listIterator(int index) {
        return getValue().listIterator(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return getValue().subList(fromIndex, toIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "CalcList[" + name + "]";
    }
}
