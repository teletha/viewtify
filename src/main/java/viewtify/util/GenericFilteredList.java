/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.util;

import java.util.Objects;
import java.util.function.Predicate;

import org.eclipse.collections.api.map.primitive.IntIntMap;
import org.eclipse.collections.impl.factory.primitive.IntIntMaps;

import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.TransformationList;

/**
 * 
 */
public class GenericFilteredList<E> extends TransformationList<E, E> {

    /** The filtered list. */
    private IntIntMap mapper = IntIntMaps.mutable.empty();

    /** The filter condition. */
    private Predicate<E> condition;

    /**
     * @param source
     */
    public GenericFilteredList(ObservableList<? extends E> source, Predicate<E> condition) {
        super(source);
        this.condition = Objects.requireNonNull(condition);
    }

    private void remap() {
        int sourceCount = 0;
        int filteredCount = 0;
        for (E item : getSource()) {
            if (condition.test(item)) {

            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSourceIndex(int index) {
        return mapper.get(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getViewIndex(int index) {
        throw new Error();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E get(int index) {
        return getSource().get(getSourceIndex(index));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return mapper.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void sourceChanged(Change<? extends E> c) {
        beginChange();
        while (c.next()) {
            if (c.wasPermutated()) {
                permutate(c);
            } else if (c.wasUpdated()) {
                update(c);
            } else {
                addRemove(c);
            }
        }
        endChange();
    }

    /**
     * Handle permutation.
     * 
     * @param c
     */
    private void permutate(Change<? extends E> c) {
        for (int i = c.getFrom(); i < c.getTo(); i++) {
            int permutation = c.getPermutation(i);
            System.out.println(permutation);
        }
    }

    /**
     * Handle addition and remove.
     * 
     * @param c
     */
    private void addRemove(Change<? extends E> c) {
        if (c.wasAdded()) {
            for (int i = c.getFrom(); i < c.getTo(); i++) {
                System.out.println(i);
            }
        }

        if (c.wasRemoved()) {
            System.out.println("remove");
        }
    }

    /**
     * Handle updating.
     * 
     * @param c
     */
    private void update(Change<? extends E> c) {
    }

    private void update() {
    }
}