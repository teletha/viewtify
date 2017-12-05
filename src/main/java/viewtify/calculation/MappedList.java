/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.calculation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.TransformationList;

/**
 * @version 2017/11/26 23:13:46
 */
class MappedList<Out, In> extends TransformationList<Out, In> {

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
    MappedList(ObservableList<? extends In> source, Function<? super In, ? extends Out> mapper) {
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