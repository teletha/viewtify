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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javafx.collections.ModifiableObservableListBase;
import kiss.Managed;
import kiss.Singleton;
import kiss.Storable;
import viewtify.Viewtify;

@Managed(Singleton.class)
public final class StorableList<E extends StorableModel> extends ModifiableObservableListBase<E> implements Storable<StorableList<E>> {

    /** The model id. */
    private final String id;

    /** The actual container. */
    private final ArrayList<E> list = new ArrayList();

    /**
     * Hide constructor.
     */
    private StorableList(Class<E> type) {
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
        return Viewtify.UserPreference.exact().file(id + ".json").path();
    }

    private static final Map<Class, StorableList> cache = new ConcurrentHashMap();

    public static <M extends StorableModel> StorableList<M> of(Class<M> type) {
        return cache.computeIfAbsent(type, StorableList::new);
    }
}