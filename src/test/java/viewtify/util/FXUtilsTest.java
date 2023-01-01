/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.util;

import org.junit.jupiter.api.Test;

import stylist.Style;
import stylist.StyleDSL;
import stylist.value.Color;

class FXUtilsTest {

    @Test
    void color() {
        javafx.scene.paint.Color color = FXUtils.color(style.color, "color");
        assert color.equals(javafx.scene.paint.Color.BLACK);
    }

    @Test
    void strokeColor() {
        javafx.scene.paint.Color color = FXUtils.color(style.strokeColor, "stroke");
        assert color.equals(javafx.scene.paint.Color.BLACK);
    }

    @Test
    void fillColor() {
        javafx.scene.paint.Color color = FXUtils.color(style.fillColor, "fill");
        assert color.equals(javafx.scene.paint.Color.BLACK);
    }

    interface style extends StyleDSL {
        Style color = () -> {
            font.color(Color.Black);
        };

        Style strokeColor = () -> {
            stroke.color(Color.Black);
        };

        Style fillColor = () -> {
            fill.color(Color.Black);
        };
    }
}