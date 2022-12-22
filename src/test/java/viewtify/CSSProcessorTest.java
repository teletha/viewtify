/*
 * Copyright (C) 2021 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify;

import org.junit.jupiter.api.Test;

import stylist.Style;
import stylist.StyleDSL;
import stylist.util.Properties;
import stylist.value.Color;

class CSSProcessorTest implements StyleDSL {

    /**
     * Write {@link Properties}.
     * 
     * @param style
     * @return
     */
    private Properties writeStyle(Style style) {
        Properties properties = style.properties();

        new CSSProcessor().accept(properties);

        return properties;
    }

    @Test
    void width() {
        Properties style = writeStyle(() -> {
            display.width(10, px);
            display.maxWidth(10, px);
            display.minWidth(10, px);
        });

        assert style.is("-fx-pref-width", "10px");
        assert style.is("-fx-pref-width", "10px");
        assert style.is("-fx-max-width", "10px");
        assert style.is("-fx-min-width", "10px");
    }

    @Test
    void height() {
        Properties style = writeStyle(() -> {
            display.height(10, px);
            display.maxHeight(10, px);
            display.minHeight(10, px);
        });

        assert style.is("-fx-pref-height", "10px");
        assert style.is("-fx-max-height", "10px");
        assert style.is("-fx-min-height", "10px");
    }

    @Test
    void padding() {
        Properties style = writeStyle(() -> {
            padding.size(10, px);
        });
        assert style.is("-fx-padding", "10px 10px 10px 10px");

        style = writeStyle(() -> {
            padding.horizontal(10, px);
        });
        assert style.is("-fx-padding", "0 10px 0 10px");

        style = writeStyle(() -> {
            padding.top(10, px).left(5, px);
        });
        assert style.is("-fx-padding", "10px 0 0 5px");
    }

    @Test
    void curosr() {
        Properties style = writeStyle(() -> {
            cursor.pointer();
        });
        assert style.is("-fx-cursor", "hand");

        style = writeStyle(() -> {
            cursor.waiting();
        });
        assert style.is("-fx-cursor", "wait");

        style = writeStyle(() -> {
            cursor.text();
        });
        assert style.is("-fx-cursor", "text");
    }

    @Test
    void borderWidth() {
        Properties style = writeStyle(() -> {
            border.bottom.width(2, px).solid().color(Color.Black);
        });
        assert style.is("-fx-border-width", "0 0 2px 0");
    }

    @Test
    void borderColor() {
        Properties style = writeStyle(() -> {
            border.bottom.width(2, px).solid().color(Color.Black);
        });
        assert style.is("-fx-border-color", "transparent transparent black transparent");
    }

    @Test
    void alignment() {
        Properties style = writeStyle(() -> {
            text.align.center().verticalAlign.middle();
        });
        assert style.is("-fx-alignment", "center");

        style = writeStyle(() -> {
            text.align.left();
        });
        assert style.is("-fx-alignment", "center-left");

        style = writeStyle(() -> {
            text.align.right();
        });
        assert style.is("-fx-alignment", "center-right");

        style = writeStyle(() -> {
            text.verticalAlign.middle();
        });
        assert style.is("-fx-alignment", "center-left");
    }

    @Test
    void color() {
        Properties style = writeStyle(() -> {
            font.color($.rgb(0, 0, 0));
        });
        assert style.is("-fx-text-fill", "black");
    }

    @Test
    void strokeDashArray() {
        Properties style = writeStyle(() -> {
            stroke.dashArray(1, 2);
        });
        assert style.is("-fx-stroke-dash-array", "1 2");
    }

    @Test
    void textIndent() {
        Properties style = writeStyle(() -> {
            text.indent(10, px);
        });
        assert style.is("-fx-label-padding", "0 0 0 10px");
    }

    @Test
    void select() {
        Properties style = writeStyle(() -> {
            text.unselectable();
        });
        assert style.is("-fx-focus-traversable", "false");
    }

    @Test
    void visibility() {
        Properties style = writeStyle(() -> {
            display.visibility.hidden();
        });
        assert style.is("visibility", "hidden");
    }
}