/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.view;

import java.util.Locale;

import javafx.scene.text.Font;

import kiss.I;
import viewtify.Theme;
import viewtify.ThemeType;
import viewtify.Viewtify;
import viewtify.prference.Preferences;

/**
 * Preference for appearance.
 */
public class AppearanceSetting extends Preferences {

    public final Preference<Locale> lang = initialize(Locale.getDefault()).syncTo(Locale::getLanguage, I.Lang);

    public final Preference<Theme> theme = initialize(Theme.Light).syncTo(Viewtify::manage);

    public final Preference<ThemeType> themeType = initialize(ThemeType.Flat).syncTo(Viewtify::manage);

    public final Preference<String> font = initialize(Font.getDefault().getName()).syncTo(x -> Viewtify.manage(Font.font(x)));

    public final Preference<Integer> fontSize = initialize(12).syncTo(x -> Viewtify.manage(Font.font(font.v, x.doubleValue())));
}