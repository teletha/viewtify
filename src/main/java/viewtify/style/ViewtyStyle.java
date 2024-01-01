/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.style;

import stylist.Style;
import stylist.StyleDSL;

/**
 * Built-in CSS.
 */
public interface ViewtyStyle extends StyleDSL {

    /**
     * Shorthand helper to make font anti-aliased.
     */
    static void useAntiAliasedFont() {
        $.descendant(".text", () -> {
            font.smooth.grayscale();
        });
    }

    Style popup = () -> {
        padding.size(15, px);
        border.radius(5, px).color("derive(-fx-color,-30%)");
        background.color("-fx-control-inner-background");
    };
}