/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.calendar;

import viewtify.preference.Preferences;

/**
 * Preference for calendar.
 */
public class TimeEventSourceSetting extends Preferences {

    /** The availability. */
    public final Preference<Boolean> enable = initialize(true);

    /** The user defined color. */
    public final Preference<stylist.value.Color> color = initialize(stylist.value.Color.Transparent);
}