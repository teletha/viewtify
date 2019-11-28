/*
 * Copyright (C) 2019 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.bind;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;

import kiss.I;
import kiss.Variable;
import kiss.WiseBiFunction;
import viewtify.Viewtify;

/**
 * @version 2018/02/07 16:21:38
 */
public class CalculatedList<E> extends BindingBase<ObservableList<E>> implements Iterable<E> {

    /** The source list. */
    private final ObservableList<E> source;

    /** The selector list. */
    private List<Function<E, ? extends Observable>> observableSelectors;

    /**
     * Create {@link CalculatedList} with identical name.
     * 
     * @param source
     */
    public CalculatedList(ObservableList<E> source) {
        this.source = source;

        bind(source);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ObservableList<E> computeValue() {
        if (debug) System.out.println(source);
        return source;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<E> iterator() {
        return getValue().iterator();
    }

    /**
     * Create mapped {@link CalculatedList} for {@link ObservableValue}.
     * 
     * @param mapper
     * @return
     */
    public <R> CalculatedList<R> flatObservable(Function<E, ObservableValue<R>> mapper) {
        observe(mapper);
        return Viewtify.calculate(new MappedList<>(this, e -> mapper.apply(e).getValue()));
    }

    /**
     * Create mapped {@link CalculatedList} for {@link Variable}.
     * 
     * @param mapper
     * @return
     */
    public <R> CalculatedList<R> flatVariable(Function<E, Variable<R>> mapper) {
        observeVariable(mapper);
        return Viewtify.calculate(new MappedList<>(this, e -> mapper.apply(e).get()));
    }

    /**
     * Create boolean {@link Calculated} whether this list has the specified value or not.
     * 
     * @param value
     * @return
     */
    public Calculated<Boolean> is(E value) {
        return new Calculated<Boolean>(() -> getValue().contains(value), null, this);
    }

    /**
     * Create boolean {@link Calculated} whether this list never have the specified value or not.
     * 
     * @param value
     * @return
     */
    public Calculated<Boolean> isNot(E value) {
        return new Calculated<Boolean>(() -> !getValue().contains(value), null, this);
    }

    /**
     * Create new indexed item binding.
     * 
     * @param index
     * @return
     */
    public Calculated<E> item(int index) {
        return Viewtify.calculate(this, () -> {
            List<E> value = getValue();
            return index < value.size() ? value.get(index) : null;
        });
    }

    /**
     * Create mapped {@link CalculatedList}.
     * 
     * @param mapper
     * @return
     */
    public <R> CalculatedList<R> map(Function<E, R> mapper) {
        return Viewtify.calculate(new MappedList<>(this, mapper));
    }

    /**
     * Register {@link Observable} property selector.
     * 
     * @param selector
     * @return
     */
    public final synchronized CalculatedList<E> observe(Function<E, ? extends Observable> selector) {
        if (selector != null) {
            if (observableSelectors == null) {
                observableSelectors = new CopyOnWriteArrayList<>();
                source.addListener((ListChangeListener<E>) change -> {
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
                });
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

    /**
     * Register {@link Observable} property selector.
     * 
     * @param selector
     * @return
     */
    public final synchronized CalculatedList<E> observeVariable(Function<E, ? extends Variable> selector) {
        return observe(e -> Viewtify.calculate(selector.apply(e), s -> invalidate()));
    }

    /**
     * Create new reduce binding.
     * 
     * @param init
     * @param accumulator
     * @return
     */
    public <R> Calculated<R> reduce(R init, WiseBiFunction<R, E, R> accumulator) {
        return new Calculated<R>(() -> {
            return I.signal(getValue()).scanWith(init, accumulator).to().v;
        }, null, this);
    }

    /**
     * Concat this list and the given list.
     * 
     * @param list A list to add.
     * @return A concated list.
     */
    public CalculatedList<E> concat(ObservableList<E> list) {
        return new CalculatedList(new ConcatList(source, list));
    }

    /**
     * Concat this list and the given list.
     * 
     * @param list A list to add.
     * @return A concated list.
     */
    public CalculatedList<E> concat(CalculatedList<E> list) {
        return new CalculatedList(new ConcatList(source, list.source));
    }

    /**
     * @version 2017/12/12 15:25:23
     */
    private static class MappedList<Out, In> extends AbstractList<Out> implements ObservableList<Out> {

        /** The source list reference. */
        private final CalculatedList<In> source;

        /** The value mapper. */
        private final Function<? super In, ? extends Out> mapper;

        /** The listener holder. */
        private List<InvalidationListener> invalidationListeners;

        /** The listener holder. */
        private List<ListChangeListener<? super Out>> changeListeners;

        /**
         * <p>
         * Create new mapping list.
         * </p>
         * 
         * @param source
         * @param mapper
         */
        MappedList(CalculatedList<In> source, Function<? super In, ? extends Out> mapper) {
            this.source = source;
            this.mapper = mapper;

            source.addListener((InvalidationListener) o -> {
                if (invalidationListeners != null) {
                    invalidationListeners.forEach(listener -> {
                        listener.invalidated(this);
                    });
                }
            });

            source.getValue().addListener((ListChangeListener<? super In>) change -> {
                if (changeListeners != null) {
                    changeListeners.forEach(listener -> listener.onChanged(new MappedChange(this, change)));
                }
            });
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Out get(int index) {
            try {
                return mapper.apply(source.getValue().get(index));
            } catch (Throwable e) {
                return null;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int size() {
            return source.getValue().size();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean addAll(Out... elements) {
            return addAll(Arrays.asList(elements));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean setAll(Out... elements) {
            return setAll(Arrays.asList(elements));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean setAll(Collection<? extends Out> col) {
            throw new UnsupportedOperationException();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean removeAll(Out... elements) {
            return removeAll(Arrays.asList(elements));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean retainAll(Out... elements) {
            return retainAll(Arrays.asList(elements));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void remove(int from, int to) {
            removeRange(from, to);
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
         * {@inheritDoc}
         */
        @Override
        public void addListener(ListChangeListener<? super Out> listener) {
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
        public void removeListener(ListChangeListener<? super Out> listener) {
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
         * @version 2017/12/10 17:32:36
         */
        private final class MappedChange extends Change<Out> {

            /**
             * 
             */
            private final Change<? extends In> change;

            /**
             * @param list
             * @param change
             */
            private MappedChange(ObservableList<Out> list, Change<? extends In> change) {
                super(list);

                this.change = change;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean wasAdded() {
                return change.wasAdded();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean wasRemoved() {
                return change.wasRemoved();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean wasReplaced() {
                return change.wasReplaced();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean wasUpdated() {
                return change.wasUpdated();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean wasPermutated() {
                return change.wasPermutated();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public int getPermutation(int i) {
                return change.getPermutation(i);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            protected int[] getPermutation() {
                // This method is only called by the superclass methods
                // wasPermutated() and getPermutation(int), which are
                // both overriden by this class. There is no other way
                // this method can be called.
                throw new AssertionError("Unreachable code");
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public List<Out> getRemoved() {
                ArrayList<Out> res = new ArrayList<>(change.getRemovedSize());
                for (In e : change.getRemoved()) {
                    res.add(mapper.apply(e));
                }
                return res;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public int getFrom() {
                return change.getFrom();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public int getTo() {
                return change.getTo();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean next() {
                return change.next();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void reset() {
                change.reset();
            }
        }
    }

    private static class ConcatList<X> extends AbstractList<X> implements ObservableList<X> {

        /** The target list. */
        private final ObservableList<X> first;

        /** The target list. */
        private final ObservableList<X> second;

        /**
         * @param first
         * @param second
         */
        private ConcatList(ObservableList<X> first, ObservableList<X> second) {
            this.first = first;
            this.second = second;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addListener(InvalidationListener listener) {
            first.addListener(listener);
            second.addListener(listener);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void removeListener(InvalidationListener listener) {
            first.removeListener(listener);
            second.removeListener(listener);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addListener(ListChangeListener<? super X> listener) {
            first.addListener(listener);
            second.addListener(listener);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void removeListener(ListChangeListener<? super X> listener) {
            first.removeListener(listener);
            second.removeListener(listener);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean addAll(X... elements) {
            // If this exception will be thrown, it is bug of this program. So we must rethrow the
            // wrapped error in here.
            throw new Error();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean setAll(X... elements) {
            // If this exception will be thrown, it is bug of this program. So we must rethrow the
            // wrapped error in here.
            throw new Error();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean setAll(Collection<? extends X> col) {
            // If this exception will be thrown, it is bug of this program. So we must rethrow the
            // wrapped error in here.
            throw new Error();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean removeAll(X... elements) {
            boolean result1 = first.removeAll(elements);
            boolean result2 = second.removeAll(elements);
            return result1 || result2;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean retainAll(X... elements) {
            // If this exception will be thrown, it is bug of this program. So we must rethrow the
            // wrapped error in here.
            throw new Error();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void remove(int from, int to) {
            int firstSize = first.size();

            if (from < firstSize) {
                if (to < firstSize) {
                    first.remove(from, to);
                } else {
                    first.remove(from, firstSize);
                    second.remove(0, to - firstSize);
                }
            } else {
                second.remove(from - firstSize, to - firstSize);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public X get(int index) {
            int firstSize = first.size();

            if (index < firstSize) {
                return first.get(index);
            } else {
                return second.get(index - firstSize);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int size() {
            return first.size() + second.size();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean contains(Object o) {
            return first.contains(o) || second.contains(o);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Iterator<X> iterator() {
            return new ConcatIterator(first.iterator(), second.iterator());
        }
    }

    /**
     * 
     */
    private static class ConcatIterator<T> implements Iterator<T> {

        /** The target iterator. */
        private final Iterator<T> first;

        /** The target iterator. */
        private final Iterator<T> second;

        /** The cache. */
        private boolean firstHasNext = true;

        /**
         * @param first
         * @param second
         */
        private ConcatIterator(Iterator<T> first, Iterator<T> second) {
            this.first = first;
            this.second = second;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasNext() {
            return (firstHasNext && (firstHasNext = first.hasNext())) || second.hasNext();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public T next() {
            if (firstHasNext) {
                return first.next();
            } else {
                return second.next();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void remove() {
            if (firstHasNext) {
                first.remove();
            } else {
                second.remove();
            }
        }
    }
}
