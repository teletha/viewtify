/*
 * Copyright (C) 2020 viewtify Development Team
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
import stylist.StyleRule;
import stylist.value.Color;
import viewtify.CSSProcessor;

/**
 * @version 2018/09/24 7:46:41
 */
class CSSProcessorTest implements StyleDSL {

    /**
     * Write {@link Style}.
     * 
     * @param style
     * @return
     */
    private StyleRule writeStyle(Style style) {
        StyleRule rule = StyleRule.create(style);

        new CSSProcessor().accept(rule.properties);

        return rule;
    }

    @Test
    void width() {
        StyleRule style = writeStyle(() -> {
            display.width(10, px);
            display.maxWidth(10, px);
            display.minWidth(10, px);
        });

        assert style.properties.is("-fx-pref-width", "10px");
        assert style.properties.is("-fx-pref-width", "10px");
        assert style.properties.is("-fx-max-width", "10px");
        assert style.properties.is("-fx-min-width", "10px");
    }

    @Test
    void height() {
        StyleRule style = writeStyle(() -> {
            display.height(10, px);
            display.maxHeight(10, px);
            display.minHeight(10, px);
        });

        assert style.properties.is("-fx-pref-height", "10px");
        assert style.properties.is("-fx-max-height", "10px");
        assert style.properties.is("-fx-min-height", "10px");
    }

    @Test
    void padding() {
        StyleRule style = writeStyle(() -> {
            padding.size(10, px);
        });
        assert style.properties.is("-fx-padding", "10px 10px 10px 10px");

        style = writeStyle(() -> {
            padding.horizontal(10, px);
        });
        assert style.properties.is("-fx-padding", "0 10px 0 10px");

        style = writeStyle(() -> {
            padding.top(10, px).left(5, px);
        });
        assert style.properties.is("-fx-padding", "10px 0 0 5px");
    }

    @Test
    void curosr() {
        StyleRule style = writeStyle(() -> {
            cursor.pointer();
        });
        assert style.properties.is("-fx-cursor", "hand");

        style = writeStyle(() -> {
            cursor.waiting();
        });
        assert style.properties.is("-fx-cursor", "wait");

        style = writeStyle(() -> {
            cursor.text();
        });
        assert style.properties.is("-fx-cursor", "text");
    }

    @Test
    void borderWidth() {
        StyleRule style = writeStyle(() -> {
            border.bottom.width(2, px).solid().color(Color.Black);
        });
        assert style.properties.is("-fx-border-width", "0 0 2px 0");
    }

    @Test
    void borderColor() {
        StyleRule style = writeStyle(() -> {
            border.bottom.width(2, px).solid().color(Color.Black);
        });
        assert style.properties.is("-fx-border-color", "transparent transparent black transparent");
    }

    @Test
    void alignment() {
        StyleRule style = writeStyle(() -> {
            text.align.center().verticalAlign.middle();
        });
        assert style.properties.is("-fx-alignment", "center");

        style = writeStyle(() -> {
            text.align.left();
        });
        assert style.properties.is("-fx-alignment", "center-left");

        style = writeStyle(() -> {
            text.align.right();
        });
        assert style.properties.is("-fx-alignment", "center-right");

        style = writeStyle(() -> {
            text.verticalAlign.middle();
        });
        assert style.properties.is("-fx-alignment", "center-left");
    }

    @Test
    void color() {
        StyleRule style = writeStyle(() -> {
            font.color($.rgb(0, 0, 0));
        });
        assert style.properties.is("-fx-text-fill", "black");
    }

    @Test
    void strokeDashArray() {
        StyleRule style = writeStyle(() -> {
            stroke.dashArray(1, 2);
        });
        assert style.properties.is("-fx-stroke-dash-array", "1 2");
    }

    @Test
    void textIndent() {
        StyleRule style = writeStyle(() -> {
            text.indent(10, px);
        });
        assert style.properties.is("-fx-label-padding", "0 0 0 10px");
    }

    @Test
    void select() {
        StyleRule style = writeStyle(() -> {
            text.unselectable();
        });
        assert style.properties.is("-fx-focus-traversable", "false");
    }

    @Test
    void visibility() {
        StyleRule style = writeStyle(() -> {
            visibility.hidden();
        });
        assert style.properties.is("visibility", "hidden");
    }
}