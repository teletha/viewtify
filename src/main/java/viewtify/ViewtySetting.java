/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify;

import java.time.ZonedDateTime;

import kiss.I;
import kiss.Managed;
import kiss.Singleton;
import kiss.Variable;
import psychopath.Locator;
import viewtify.model.Model;

/**
 * General setting holder for {@link Viewtify}.
 */
@Managed(Singleton.class)
public class ViewtySetting extends Model<ViewtySetting> {

    /** The user language. */
    public final Variable<String> language = I.Lang;

    /** The latest updated time. */
    public ZonedDateTime updatedTime = Locator.directory("").lastModifiedDateTime();

    /**
     * Hide constructor.
     */
    private ViewtySetting() {
        restore().auto();
    }
}