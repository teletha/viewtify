/*
 * Copyright (C) 2018 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package toybox;

import psychopath.Directory;

/**
 * @version 2018/09/17 22:23:43
 */
public class Console {

    /** The context directory. */
    public Directory directory;

    /**
     * @return
     */
    public String name() {
        return directory.name();
    }
}
