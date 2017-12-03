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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.TransformationList;

import kiss.I;
import kiss.Variable;
import kiss.WiseBiFunction;

/**
 * @version 2017/11/26 22:01:18
 */
public class ListBindingBuilder<E> {

    /** The source list. */
    private final ObservableList<E> list;

    /** The extractors for observing property. */
    private final List<Function<E, Observable>> extractors = new ArrayList();

    /**
     * New builder.
     * 
     * @param list A {@link ObservableList} source.
     */
    public ListBindingBuilder(ObservableList<E> list) {
        this.list = list;
    }

    /**
     * Add observing property.
     * 
     * @param propertyExtractor
     * @return
     */
    public ListBindingBuilder<E> observe(Function<E, Observable> propertyExtractor) {
        extractors.add(propertyExtractor);
        return this;
    }

    /**
     * Add observing property.
     * 
     * @param propertyExtractor
     * @return
     */
    public ListBindingBuilder<E> observeVariable(Function<E, Variable> propertyExtractor) {
        extractors.add(e -> new ObservableVariable(propertyExtractor.apply(e)));
        return this;
    }

    /**
     * Create new mapped {@link ListBindingBuilder}.
     * 
     * @param mapper List mapper.
     * @return
     */
    public <R> ListBindingBuilder<R> flatVariable(Function<E, Variable<R>> mapper) {
        return new ListBindingBuilder<>(new MappedList<>(list, e -> mapper.apply(e).v));
    }

    /**
     * Create new mapped {@link ListBindingBuilder}.
     * 
     * @param mapper List mapper.
     * @return
     */
    public <R> ListBindingBuilder<R> map(Function<E, R> mapper) {
        return new ListBindingBuilder(new MappedList(list, mapper));
    }

    /**
     * Create new reduce binding.
     * 
     * @param init
     * @param accumulator
     * @return
     */
    public <R> ObjectBinding<R> reduce(R init, WiseBiFunction<R, E, R> accumulator) {
        return new ListBinding<R>(l -> I.signal(list).scan(init, accumulator).to().v);
    }

    /**
     * Create new collect binding.
     * 
     * @param collector
     * @return
     */
    public <R, A> ObjectBinding<R> collect(Collector<? super E, A, R> collector) {
        return new ListBinding<R>(l -> l.stream().collect(collector));
    }

    /**
     * Create new indexed item binding.
     * 
     * @param index
     * @return
     */
    public MonadicBinding<E> item(int index) {
        return new PreboundBinding<E>(list) {

            /**
             * {@inheritDoc}
             */
            @Override
            protected E computeValue() {
                return list.size() <= index ? null : list.get(index);
            }
        };
    }

    /**
     * @version 2017/11/26 23:01:52
     */
    private class ListBinding<R> extends ObjectBinding<R> {

        /** The element observer. */
        private final InvalidationListener forElement = o -> invalidate();

        /** The list observer. */
        private final ListChangeListener<E> forList = this::onChanged;

        private final Function<List<E>, R> computer;

        /**
         * 
         */
        private ListBinding(Function<List<E>, R> computer) {
            this.computer = computer;

            list.addListener(forList);
            list.forEach(e -> {
                for (Function<E, Observable> property : extractors) {
                    property.apply(e).addListener(forElement);
                }
            });
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected R computeValue() {
            return computer.apply(list);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void dispose() {
            list.forEach(elem -> {
                if (elem instanceof ObservableValue) {
                    ((ObservableValue) elem).removeListener(forElement);
                }
            });
            list.removeListener(forList);
        }

        /**
         * Called after a change has been made to an ObservableList.
         *
         * @param change an object representing the change that was done
         */
        private void onChanged(Change<? extends E> change) {
            while (change.next()) {
                for (E e : change.getRemoved()) {
                    for (Function<E, Observable> property : extractors) {
                        property.apply(e).removeListener(forElement);
                    }
                }
                for (E e : change.getAddedSubList()) {
                    for (Function<E, Observable> property : extractors) {
                        property.apply(e).addListener(forElement);
                    }
                }
                invalidate();
            }
        }
    }

    /**
     * @version 2017/11/26 23:13:46
     */
    private static class MappedList<Out, In> extends TransformationList<Out, In> {

        /** The value mapper. */
        private final Function<? super In, ? extends Out> mapper;

        /**
         * <p>
         * Create new mapping list.
         * </p>
         * 
         * @param source
         * @param mapper
         */
        public MappedList(ObservableList<? extends In> source, Function<? super In, ? extends Out> mapper) {
            super(source);

            this.mapper = mapper;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getSourceIndex(int index) {
            return index;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Out get(int index) {
            return mapper.apply(getSource().get(index));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int size() {
            return getSource().size();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void sourceChanged(Change<? extends In> change) {
            fireChange(new Change<Out>(this) {

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
            });
        }
    }
}
