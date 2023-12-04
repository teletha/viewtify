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

import java.util.concurrent.CopyOnWriteArrayList;

import kiss.Managed;
import kiss.Storable;
import viewtify.Viewtify;

@SuppressWarnings("serial")
final class PreferencesList<E extends Preferences> extends CopyOnWriteArrayList<E> implements Storable<PreferencesList<E>> {

    /** The model id. */
    private final String id;

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

        // Synchronize data from/to source.
        Viewtify.UserPreference.observing().to(x -> {
            // Not all property values are preserved in the restore source, so they must always be
            // reset before restoring. If not reset, some properties may continue to apply the
            // previous user's values to the new user.
            clear();
            restore();

            for (E item : this) {
                item.container = this;
                item.auto();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String locate() {
        return Viewtify.UserPreference.exact().file(id + ".json").path();
    }
}