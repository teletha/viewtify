/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify;

import kiss.Extensible;
import kiss.Manageable;
import kiss.Singleton;

/**
 * @version 2017/11/13 20:39:23
 */
@Manageable(lifestyle = Singleton.class)
public abstract class View implements Extensible {

    /**
     * Initialize this view.
     */
    protected abstract void initialize();
}
