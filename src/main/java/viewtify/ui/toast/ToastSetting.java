/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.toast;

import javafx.util.Duration;
import viewtify.model.Preferences;
import viewtify.util.Corner;
import viewtify.util.ScreenSelector;

/**
 * Preference for {@link Toast}.
 */
public class ToastSetting extends Preferences {

    /** The enable status. */
    public final Preference<Boolean> enable = initialize(true);

    /** The maximum size of notifications. */
    public final Preference<Integer> max = initialize(3).requireMin(0);

    /** The animation time. */
    public final Preference<Duration> animation = initialize(Duration.millis(333)).requireMin(Duration.ONE);

    /** The automatic hiding time. */
    public final Preference<Duration> autoHide = initialize(Duration.seconds(15)).requireMin(Duration.ZERO);

    /** The notification area. */
    public final Preference<Corner> area = initialize(Corner.BottomRight);

    /** The notification screen. */
    public final Preference<ScreenSelector> screen = initialize(ScreenSelector.InWindow);

    /** The opacity of notification area. */
    public final Preference<Double> opacity = initialize(0.9).requireBetween(0, 1);

    /** The width of notification area. */
    public final Preference<Integer> width = initialize(250).requireMin(50);
}