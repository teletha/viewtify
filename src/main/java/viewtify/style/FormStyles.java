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

    int GAP = 4;

    Style Row = () -> {
        margin.vertical(GAP, px);
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

    Style Title = () -> {
        margin.bottom(6, px);
        font.size(20, px).weight.bold();

        $.descendant(() -> {
            font.smooth.grayscale();
        });
    };

    Style Description = () -> {
        margin.vertical(GAP, px);
    };

    Style DescriptionTitle = () -> {
        margin.bottom(0, px).top(10, px);
        font.size(14, px).weight.bold().color("-fx-light-text-color");

        $.descendant(() -> {
            font.smooth.grayscale();
        });
    };

    Style DescriptionDetail = () -> {
        $.descendant(".label", () -> {
            font.size(11, px).color("-fx-mid-text-color");
        });
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
            display.width(480, px);

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

    Style Sequencial = () -> {
        margin.left(GAP, px);
    };
}