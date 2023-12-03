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
import kiss.Storable;

@Managed
public class NamedPreferences extends Preferences {

    /** The user defined name. */
    public final Preference<String> name = initialize("");

    Storable container;

    /**
     * {@inheritDoc}
     */
    @Override
    public Preferences store() {
        container.store();
        System.out.println("Store " + this);
        return this;
    }
}
