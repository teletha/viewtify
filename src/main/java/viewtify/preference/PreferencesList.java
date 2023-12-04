/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.preference;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.sun.javafx.collections.ObservableListWrapper;

import kiss.Disposable;
import kiss.I;
import kiss.Managed;
import kiss.Storable;
import viewtify.Viewtify;

class PreferencesList<E extends Preferences> extends ObservableListWrapper<E> implements Storable<PreferencesList<E>> {

    /** The model id. */
    private final String id;

    /** The state manager. */
    private boolean restoring;

    /** The saving state. */
    private Disposable request;

    /**
     * Hide constructor.
     */
    PreferencesList(Class<E> type) {
        super(new ArrayList());

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

        // Synchronize data from/to source.
        Viewtify.UserPreference.observing().to(x -> {
            // Not all property values are preserved in the restore source, so they must always be
            // reset before restoring. If not reset, some properties may continue to apply the
            // previous user's values to the new user.
            clear();
            restore();

            for (E item : this) {
                synchronize(item);
            }
        });
    }

    /**
     * Make the item synchronizable.
     * 
     * @param item
     */
    private void synchronize(E item) {
        if (item != null) {
            item.container = this;
            item.auto();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PreferencesList<E> restore() {
        try {
            restoring = true;
            return Storable.super.restore();
        } finally {
            restoring = false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PreferencesList<E> store() {
        if (!restoring) {
            if (request != null) {
                request.dispose();
            }
            request = I.schedule(1, TimeUnit.SECONDS).to(Storable.super::store, I::error, () -> request = null);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String locate() {
        return Viewtify.UserPreference.exact().file(id + ".json").path();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doAdd(int index, E element) {
        super.doAdd(index, element);
        synchronize(element);
        store();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected E doSet(int index, E element) {
        E e = super.doSet(index, element);
        synchronize(element);
        store();

        return e;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected E doRemove(int index) {
        E e = super.doRemove(index);
        store();
        return e;
    }
}