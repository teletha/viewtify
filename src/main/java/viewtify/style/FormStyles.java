/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
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
 * Built-in form CSS
 */
public interface FormStyles extends StyleDSL {

    Style FormRow = () -> {
        margin.vertical(4, px);
        text.verticalAlign.middle();
    };

    Style FormLabel = () -> {
        display.width(120, px);
        padding.top(3, px);
    };

    Style FormLabelMin = () -> {
        display.width(80, px);
        padding.top(3, px);
    };

    Style FormInput = () -> {
        display.width(160, px);
        margin.right(5, px);
    };

    Style FormInputMin = () -> {
        display.width(80, px);
        margin.right(5, px);
    };

    Style FormButton = () -> {
        display.width(62, px);
    };

    Style ValidationToolTip = () -> {
        font.size(12, px).color("-fx-light-text-color");
        background.color($.rgba(60, 60, 60, 0.8));
        padding.vertical(8, px).horizontal(12, px);
    };

    Style Column = () -> {
        border.radius(2, px);
        background.color("-fx-control-inner-background");
        margin.bottom(1, px);
        padding.size(10, px);

        $.hover(() -> {
            background.color("-fx-control-inner-background-alt");
        });
    };

    Style Description = () -> {
        text.verticalAlign.middle();
        margin.right(20, px);
        display.width.fill();
    };

    Style Value = () -> {
        display.width(160, px);
    };
}