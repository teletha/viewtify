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

import kiss.Managed;
import kiss.Singleton;

@Managed(Singleton.class)
public class CommandManager {

    /**
     * Hide.
     */
    private CommandManager() {
    }

    public void register(Command id, Runnable command) {

    }
}
