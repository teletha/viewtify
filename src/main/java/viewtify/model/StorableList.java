/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.model;

import java.util.ArrayList;

import javafx.collections.ModifiableObservableListBase;

import kiss.Managed;
import kiss.Singleton;
import kiss.Storable;

@Managed(Singleton.class)
public abstract class StorableList<E extends StorableModel> extends ModifiableObservableListBase<E> implements Storable<StorableList<E>> {

    /** The actual container. */
    private final ArrayList<E> list = new ArrayList();

    /**
     * Hide constructor.
     */
    protected StorableList() {
        restore();
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
    public int size() {
        return list.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doAdd(int index, E model) {
        list.add(index, model);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected E doSet(int index, E model) {
        return list.set(index, model);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected E doRemove(int model) {
        return list.remove(model);
    }
}
