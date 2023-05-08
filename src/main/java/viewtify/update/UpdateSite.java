/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.update;

import psychopath.Directory;

public abstract class UpdateSite {

    /**
     * Get the last modified time.
     * 
     * @return
     */
    abstract long lastModified();

    /**
     * Locate the root directory.
     */
    abstract Directory locateRoot();

    /**
     * Locate library directory.
     * 
     * @return
     */
    abstract Directory locateLibrary();

    /**
     * @param dest
     */
    protected void copyTo(UpdateSite dest) {
        locateRoot().observeCopyingTo(dest.locateRoot(), o -> o.strip().replaceExisting()).to(file -> {

        });
    }
}
