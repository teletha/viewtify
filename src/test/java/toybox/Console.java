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

import java.nio.file.Path;

import bee.util.Paths;

/**
 * @version 2018/03/04 20:36:40
 */
public class Console {

    /** The context directory. */
    public Path directory;

    /**
     * @return
     */
    public String name() {
        return Paths.getName(directory);
    }
}
