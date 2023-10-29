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
import java.util.stream.IntStream;

import javafx.scene.control.Label;
import javafx.scene.text.Font;

import kiss.I;
import viewtify.Theme;
import viewtify.ThemeType;
import viewtify.Viewtify;
import viewtify.model.Preferences;
import viewtify.style.FormStyles;
import viewtify.ui.UIComboBox;
import viewtify.ui.UISpinner;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;

public class AppearanceSettingView extends View {

    /** The theme selector. */
    public UIComboBox<Theme> theme;

    /** The theme type selector. */
    public UIComboBox<ThemeType> themeType;

    /** The locale selector. */
    public UIComboBox<Locale> lang;

    /** The family selector. */
    public UIComboBox<String> family;

    /** The size selector. */
    public UISpinner<Integer> size;

    @Override
    protected ViewDSL declareUI() {
        return new ViewDSL() {
            {
                $(vbox, () -> {
                    form(en("Color Scheme"), theme, themeType);
                    form(en("Language"), lang);
                    form(en("Font"), family, size);
                });
            }
        };
    }

    @Override
    protected void initialize() {
        Setting setting = Preferences.of(Setting.class);

        theme.items(Theme.values()).sync(setting.theme);
        themeType.items(ThemeType.values()).sync(setting.themeType);

        lang.items(Locale.JAPANESE, Locale.ENGLISH).render(x -> x.getDisplayLanguage()).sync(setting.lang);

        family.items(Font.getFamilies()).sync(setting.font);
        size.items(IntStream.range(8, 18)).format(x -> x + "px").sync(setting.fontSize);
    }

    /**
     * Apply its font to combo box list.
     * 
     * @return
     */
    public AppearanceSettingView applySelfFont() {
        family.renderByNode(() -> new Label(), (label, value, disposer) -> {
            label.setText(value);
            label.setFont(Font.font(value));
            return label;
        });
        return this;
    }

    /**
     * Preference for appearance.
     */
    public static class Setting extends Preferences {

        public final Preference<Locale> lang = initialize(Locale.getDefault()).syncTo(Locale::getLanguage, I.Lang);

        public final Preference<Theme> theme = initialize(Theme.Light).syncTo(Viewtify::manage);

        public final Preference<ThemeType> themeType = initialize(ThemeType.Flat).syncTo(Viewtify::manage);

        public final Preference<String> font = initialize(Font.getDefault().getName()).syncTo(x -> Viewtify.manage(Font.font(x)));

        public final Preference<Integer> fontSize = initialize(12).syncTo(x -> Viewtify.manage(Font.font(font.v, x.doubleValue())));
    }
}
