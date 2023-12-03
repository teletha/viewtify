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
import kiss.Storable;
import viewtify.Viewtify;

final class PreferencesList<E extends Preferences> extends ModifiableObservableListBase<E> implements Storable<PreferencesList<E>> {

    /** The model id. */
    private final String id;

    /** The actual container. */
    private final ArrayList<E> list = new ArrayList();

    /**
     * Hide constructor.
     */
    PreferencesList(Class<E> type) {
        Managed annotation = type.getAnnotation(Managed.class);
        if (annotation == null) {
            id = type.getName();
        } else {
            String name = annotation.name();
            if (name == null || name.isBlank()) {
                id = type.getName();
            } else {
                id = name;
            }
        }

        sync();
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
        if (model != null) {
            System.out.println("Add " + model);
            model.sync2();
            list.add(index, model);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected E doSet(int index, E model) {
        System.out.println("Set " + model);
        model.sync2();

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
     * {@inheritDoc}
     */
    @Override
    public final String locate() {
        return Viewtify.UserPreference.exact().file(id + ".json").path();
    }
}