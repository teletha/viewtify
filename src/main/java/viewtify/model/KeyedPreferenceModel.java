/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.model;

import kiss.Managed;
import viewtify.Viewtify;

@Managed
public abstract class KeyedPreferenceModel<Self extends KeyedPreferenceModel> extends PreferenceModel<Self> {

    private PreferenceAware preferenceable;

    final void assign(PreferenceAware preferenceable) {
        this.preferenceable = preferenceable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String locate() {
        return Viewtify.UserPreference.exact().file(getModelName() + "@" + preferenceable.id() + ".json").path();
    }
}
