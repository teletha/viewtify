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

    Style Row = () -> {
        margin.vertical(4, px);
        text.verticalAlign.baseline();
    };

    Style Label = () -> {
        display.width(120, px);
    };

    Style LabelMin = () -> {
        display.width(80, px);
    };

    Style Input = () -> {
        display.width(160, px);
    };

    Style InputMin = () -> {
        display.width(80, px);
    };

    Style Button = () -> {
        display.width(62, px);
    };

    Style ValidationToolTip = () -> {
        font.size(12, px).color("-fx-light-text-color");
        background.color($.rgba(60, 60, 60, 0.8));
        padding.vertical(8, px).horizontal(12, px);
    };

    Style Preferences = () -> {
        // marker class

        $.descendant(Row, () -> {
            border.radius(2, px);
            background.color("-fx-control-inner-background");
            margin.bottom(1, px);
            padding.horizontal(10, px).vertical(10, px);

            $.hover(() -> {
                background.color("-fx-control-inner-background-alt");
            });

            $.descendant(Row, () -> {
                padding.vertical(5, px);
            });
        });

        $.descendant(Label, () -> {
            text.verticalAlign.baseline().align.right();
            margin.right(20, px);
            display.minWidth(180, px).maxWidth(360, px).width.fill();
        });
    };
}