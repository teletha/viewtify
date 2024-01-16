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

/**
 * Built-in form CSS
 */
public interface FormStyles extends ViewtyStyle {

    int HorizontalGap = 2;

    int VerticalGap = 4;

    Style Row = () -> {
        margin.vertical(VerticalGap, px);
        text.verticalAlign.middle();
        background.color("red");
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
        margin.vertical(VerticalGap, px);
    };

    Style DescriptionTitle = () -> {
        margin.bottom(0, px).top(13, px);
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
            display.minWidth(350, px).minHeight(48, px);
            border.radius(2, px);
            background.color("-fx-control-inner-background");
            margin.bottom(1, px);
            padding.horizontal(10, px).vertical(10, px);
            text.verticalAlign.middle().align.right();

            $.hover(() -> {
                background.color("-fx-control-inner-background-alt");
            });

            $.descendant(Row, () -> {
                display.minHeight(36, px);
                padding.vertical(5, px);
            });
        });

        $.descendant(Label, () -> {
            display.width.fill();
            text.verticalAlign.baseline();
            margin.right(20, px).top(4, px);
        });
    };

    Style FormMin = () -> {
        $.descendant(Label, () -> {
            display.width(80, px);
        });

        $.descendant(LabelMin, () -> {
            display.width(40, px);
        });
    };

    Style FormSlim = () -> {
        $.descendant(Label, () -> {
            display.width(100, px);
        });

        $.descendant(LabelMin, () -> {
            display.width(60, px);
        });
    };

    Style Form = () -> {
        $.descendant(Label, () -> {
            display.width(120, px);
        });

        $.descendant(LabelMin, () -> {
            display.width(80, px);
        });
    };

    Style FormWide = () -> {
        $.descendant(Label, () -> {
            display.width(140, px);
        });

        $.descendant(LabelMin, () -> {
            display.width(100, px);
        });
    };

    Style FormMax = () -> {
        $.descendant(Label, () -> {
            display.width(160, px);
        });

        $.descendant(LabelMin, () -> {
            display.width(120, px);
        });
    };

    Style LabelLeft = () -> {
        $.descendant(Label, () -> {
            text.align.left();
        });

        $.descendant(LabelMin, () -> {
            text.align.left();
        });
    };

    Style LabelRight = () -> {
        $.descendant(Label, () -> {
            text.align.right();
        });

        $.descendant(LabelMin, () -> {
            text.align.right();
        });
    };

    Style LabelCenter = () -> {
        $.descendant(Label, () -> {
            text.align.center();
        });

        $.descendant(LabelMin, () -> {
            text.align.center();
        });
    };

    Style Sequencial = () -> {
        margin.right(HorizontalGap, px);
    };

    Style Combined = () -> {
        display.height.fitContent();
        padding.size(0, px).right(2, px);
        margin.size(0, px);
    };

    Style CombinedItem = () -> {
        text.align.center();
    };

    Style CheckBox = () -> {
        text.align.left();
    };
}