/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.util;

import java.util.Optional;

import javafx.scene.paint.Color;

import stylist.CSSValue;
import stylist.Style;
import stylist.StyleRule;

/**
 * @version 2018/09/10 10:57:23
 */
public class FXUtils {

    /**
     * Compute color as JavaFX {@link Color}.
     * 
     * @return
     */
    public static Color color(Style style, String propertyName) {
        StyleRule rule = StyleRule.create(style);
        Optional<CSSValue> color = rule.properties.get(propertyName);

        if (color.isPresent()) {
            CSSValue value = color.get();

            if (value instanceof stylist.value.Color) {
                return Color.web(((stylist.value.Color) value).toRGB());
            } else {
                return Color.web(color.toString());
            }
        } else {
            return Color.TRANSPARENT;
        }
    }
}
