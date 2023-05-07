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

import java.util.Objects;

import psychopath.Directory;

class LocalSite extends UpdateSite {

    /** The update site. */
    private final Directory site;

    /**
     * @param site
     */
    LocalSite(Directory site) {
        this.site = Objects.requireNonNull(site);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    long lastModified() {
        return site.lastModifiedMilli();
    }
}
