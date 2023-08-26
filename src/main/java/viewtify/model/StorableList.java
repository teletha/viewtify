/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.model;

import java.util.ArrayList;

import javafx.collections.ModifiableObservableListBase;

import kiss.Managed;
import kiss.Model;
import kiss.Singleton;
import kiss.Storable;
import viewtify.Viewtify;

@Managed(Singleton.class)
public abstract class StorableList<E extends StorableModel> extends ModifiableObservableListBase<E> implements Storable<StorableList<E>> {

    /** The actual container. */
    private final ArrayList<E> list = new ArrayList();

    /**
     * Hide constructor.
     */
    protected StorableList() {
        sync();
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

    /**
     * Synchronize data from/to source.
     */
    protected final void sync() {
        Viewtify.UserPreference.observing().to(x -> {
            // Not all property values are preserved in the restore source, so they must always be
            // reset before restoring. If not reset, some properties may continue to apply the
            // previous user's values to the new user.
            clear();
            restore();
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String locate() {
        return Viewtify.UserPreference.exact().file(getModelName() + ".json").path();
    }

    /**
     * Get the identical model name.
     * 
     * @return
     */
    protected String getModelName() {
        return Model.of(this).type.getName();
    }
}