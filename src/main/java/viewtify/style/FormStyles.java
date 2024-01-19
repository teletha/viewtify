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

import kiss.Variable;
import stylist.Style;

/**
 * Built-in form CSS
 */
public interface FormStyles extends ViewtyStyle {

    Variable<Integer> Column = Variable.of(30);

    Variable<Integer> Gap = Variable.of(2);

    Style Row = () -> {
        margin.vertical(Gap.v, px);
        text.verticalAlign.middle();
        background.color("red");
    };

    Style Label = () -> {
        margin.top(6, px);
    };

    Style LabelInfo = Label.with(() -> {
        font.size(0.88, em);
    });

    Style Title = () -> {
        margin.bottom(6, px);
        font.size(20, px).weight.bold();

        $.descendant(() -> {
            font.smooth.grayscale();
        });
    };

    Style Description = () -> {
        margin.vertical(Gap.v, px);
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

    Style Label70 = () -> {
        $.descendant(Label, () -> {
            display.width(70, px);
        });
    };

    Style Label90 = () -> {
        $.descendant(Label, () -> {
            display.width(90, px);
        });
    };

    Style Label110 = () -> {
        $.descendant(Label, () -> {
            display.width(110, px);
        });
    };

    Style Label130 = () -> {
        $.descendant(Label, () -> {
            display.width(130, px);
        });
    };

    Style Label150 = () -> {
        $.descendant(Label, () -> {
            display.width(150, px);
        });
    };

    Style LabelLeft = () -> {
        $.descendant(Label, () -> {
            text.align.left();
        });
    };

    Style LabelRight = () -> {
        $.descendant(Label, () -> {
            text.align.right();
        });
    };

    Style LabelCenter = () -> {
        $.descendant(Label, () -> {
            text.align.center();
        });
    };

    Style Sequencial = () -> {
        margin.right(Gap.v, px);
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

    Style Separator = () -> {
        margin.vertical(6, px);
        display.opacity(0);
    };

    private static Style colum(int size) {
        return () -> {
            display.width(Column.v * size + Gap.v * (size - 1), px);
        };
    }

    Style Column1 = colum(1);

    Style Column2 = colum(2);

    Style Column3 = colum(3);

    Style Column4 = colum(4);

    Style Column5 = colum(5);

    Style Column6 = colum(6);

    Style Column7 = colum(7);

    Style Column8 = colum(8);

    Style Column9 = colum(9);

    Style Column10 = colum(10);

    Style Column11 = colum(11);

    Style Column12 = colum(12);

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
}