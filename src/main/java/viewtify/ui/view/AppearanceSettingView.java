/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.view;

import java.util.Locale;
import java.util.stream.IntStream;

import javafx.scene.control.Label;
import javafx.scene.text.Font;
import kiss.Variable;
import viewtify.Theme;
import viewtify.ThemeType;
import viewtify.preference.Preferences;
import viewtify.style.FormStyles;
import viewtify.ui.UICheckSwitch;
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

    /** The scroll behavior. */
    public UICheckSwitch smoothScroll;

    /**
     * {@inheritDoc}
     */
    @Override
    public Variable<String> title() {
        return en("Appearance and Language");
    }

    @Override
    protected ViewDSL declareUI() {
        return new ViewDSL() {
            {
                $(vbox, () -> {
                    form(en("Color Scheme"), theme);
                    form(en("Theme Kind"), themeType);
                    form(en("Language"), lang);
                    form(en("Font"), family, size.style(FormStyles.InputMin, FormStyles.Sequencial));
                    form(en("Enable smooth scroll"), FormStyles.InputMin, smoothScroll);
                });
            }
        };
    }

    @Override
    protected void initialize() {
        AppearanceSetting setting = Preferences.of(AppearanceSetting.class);

        theme.items(Theme.values()).sync(setting.theme);
        themeType.items(ThemeType.values()).sync(setting.themeType);

        lang.items(Locale.ENGLISH, Locale.CHINESE, Locale.forLanguageTag("hi"), Locale.forLanguageTag("es"), Locale.FRENCH, Locale
                .forLanguageTag("pt"), Locale.forLanguageTag("id"), Locale.GERMAN, Locale.ITALIAN, Locale.JAPANESE, Locale.KOREAN)
                .initialize(Locale.getDefault())
                .render(x -> x.getDisplayLanguage(x))
                .sync(setting.lang);

        family.items(Font.getFamilies()).sync(setting.font).style(FormStyles.Input);
        size.items(IntStream.range(8, 18)).format(x -> x + "px").sync(setting.fontSize);
        smoothScroll.sync(setting.smoothScroll);
    }

    /**
     * Apply its font to combo box list.
     * 
     * @return
     */
    public final AppearanceSettingView applySelfFont() {
        family.renderByNode(() -> new Label(), (label, value, disposer) -> {
            label.setText(value);
            label.setFont(Font.font(value));
            return label;
        });
        return this;
    }

    /**
     * List up all system fonts.
     * 
     * @return
     */
    public final AppearanceSettingView useSystemFonts() {
        family.items(Font.getFamilies());

        return this;
    }
}