/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify;

import java.util.HashMap;
import java.util.Map;

import kiss.Managed;
import kiss.Singleton;
import kiss.Storable;

@Managed(Singleton.class)
public class ShortcutManager implements Storable<ShortcutManager> {

    private Map<Key, Command<? extends Enum>> normals = new HashMap();

    /**
     * Hide
     */
    private ShortcutManager() {
        restore();
    }

    public void register(Key key, Command command) {

    }
}
