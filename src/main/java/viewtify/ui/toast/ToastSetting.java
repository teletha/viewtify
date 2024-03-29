/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.toast;

import javafx.util.Duration;

import viewtify.preference.Preferences;
import viewtify.util.Corner;
import viewtify.util.ScreenSelector;

/**
 * Preference for {@link Toast}.
 */
public class ToastSetting extends Preferences {

    /** The enable status. */
    public final Preference<Boolean> enable = initialize(true);

    /** The maximum size of notifications. */
    public final Preference<Double> max = initialize(3d).requireMin(0);

    /** The animation time. */
    public final Preference<Duration> animation = initialize(Duration.millis(333)).requireMin(Duration.ONE);

    /** The automatic hiding time. */
    public final Preference<Double> autoHide = initialize(20d).requireMin(0);

    /** The notification area. */
    public final Preference<Corner> area = initialize(Corner.BottomRight);

    /** The notification screen. */
    public final Preference<ScreenSelector> screen = initialize(ScreenSelector.InWindow);

    /** The opacity of notification area. */
    public final Preference<Double> opacity = initialize(90d).requireBetween(0, 100);

    /** The width of notification area. */
    public final Preference<Integer> width = initialize(250).requireMin(50);

    /** The gap of notification area. */
    public final Preference<Double> gap = initialize(15d).requireBetween(0, 30);
}