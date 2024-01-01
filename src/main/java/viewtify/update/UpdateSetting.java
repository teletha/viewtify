/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.update;

import viewtify.preference.Preferences;

/**
 * Preference for {@link Update}.
 */
public class UpdateSetting extends Preferences {

    /** The enable status. */
    public final Preference<Boolean> checkOnStartup = initialize(true);
}