/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
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
public class CalculationList<E> extends BindingBase<ObservableList<E>> implements Iterable<E> {

    /** The source list. */
    private final ObservableList<E> source;

    /** The selector list. */
    private List<Function<E, ? extends Observable>> observableSelectors;

    /**
     * Create {@link CalculationList} with identical name.
     * 
     * @param source
     */
    public CalculationList(ObservableList<E> source) {
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
     * Create mapped {@link CalculationList} for {@link ObservableValue}.
     * 
     * @param mapper
     * @return
     */
    public <R> CalculationList<R> flatObservable(Function<E, ObservableValue<R>> mapper) {
        observe(mapper);
        return Viewtify.calculate(new MappedList<>(this, e -> mapper.apply(e).getValue()));
    }

    /**
     * Create mapped {@link CalculationList} for {@link Variable}.
     * 
     * @param mapper
     * @return
     */
    public <R> CalculationList<R> flatVariable(Function<E, Variable<R>> mapper) {
        observeVariable(mapper);
        return Viewtify.calculate(new MappedList<>(this, e -> mapper.apply(e).get()));
    }

    /**
     * Create boolean {@link Calculation} whether this list has the specified value or not.
     * 
     * @param value
     * @return
     */
    public Calculation<Boolean> is(E value) {
        return new Calculation<Boolean>(() -> getValue().contains(value), null, this);
    }

    /**
     * Create boolean {@link Calculation} whether this list never have the specified value or not.
     * 
     * @param value
     * @return
     */
    public Calculation<Boolean> isNot(E value) {
        return new Calculation<Boolean>(() -> !getValue().contains(value), null, this);
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
        return Viewtify.calculate(new MappedList<>(this, mapper));
    }

    /**
     * Register {@link Observable} property selector.
     * 
     * @param selector
     * @return
     */
    public final synchronized CalculationList<E> observe(Function<E, ? extends Observable> selector) {
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
    public final synchronized CalculationList<E> observeVariable(Function<E, ? extends Variable> selector) {
        return observe(e -> Viewtify.calculate(selector.apply(e), s -> invalidate()));
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
            return I.signal(getValue()).scanWith(init, accumulator).to().v;
        }, null, this);
    }

    /**
     * @version 2017/12/12 15:25:23
     */
    private static class MappedList<Out, In> extends AbstractList<Out> implements ObservableList<Out> {

        /** The source list reference. */
        private final CalculationList<In> source;

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
        MappedList(CalculationList<In> source, Function<? super In, ? extends Out> mapper) {
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
}
