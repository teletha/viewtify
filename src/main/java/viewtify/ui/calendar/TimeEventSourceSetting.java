/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.calendar;

import viewtify.model.Preferences;

/**
 * Preference for calendar.
 */
public class TimeEventSourceSetting extends Preferences {

    /** The user defined name. */
    public final Preference<String> name = initialize("");

    /** The availability. */
    public final Preference<Boolean> enable = initialize(true);

    /** The user defined color. */
    public final Preference<stylist.value.Color> color = initialize(stylist.value.Color.Transparent);
}