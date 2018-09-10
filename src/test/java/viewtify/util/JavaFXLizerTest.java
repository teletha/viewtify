/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.util;

import org.junit.jupiter.api.Test;

import stylist.StyleTester;
import stylist.value.Color;

/**
 * @version 2018/09/01 22:21:23
 */
class JavaFXLizerTest extends StyleTester {

    JavaFXLizer fxlizer = new JavaFXLizer();

    @Test
    void width() {
        ValidatableStyle style = writeStyle(() -> {
            display.width(10, px);
            display.maxWidth(10, px);
            display.minWidth(10, px);
        }, fxlizer);

        assert style.property("-fx-pref-width", "10px");
        assert style.property("-fx-max-width", "10px");
        assert style.property("-fx-min-width", "10px");
    }

    @Test
    void height() {
        ValidatableStyle style = writeStyle(() -> {
            display.height(10, px);
            display.maxHeight(10, px);
            display.minHeight(10, px);
        }, fxlizer);

        assert style.property("-fx-pref-height", "10px");
        assert style.property("-fx-max-height", "10px");
        assert style.property("-fx-min-height", "10px");
    }

    @Test
    void padding() {
        ValidatableStyle style = writeStyle(() -> {
            padding.size(10, px);
        }, fxlizer);
        assert style.property("-fx-padding", "10px 10px 10px 10px");

        style = writeStyle(() -> {
            padding.horizontal(10, px);
        }, fxlizer);
        assert style.property("-fx-padding", "0 10px 0 10px");

        style = writeStyle(() -> {
            padding.top(10, px).left(5, px);
        }, fxlizer);
        assert style.property("-fx-padding", "10px 0 0 5px");
    }

    @Test
    void curosr() {
        ValidatableStyle style = writeStyle(() -> {
            cursor.pointer();
        }, fxlizer);
        assert style.property("-fx-cursor", "hand");

        style = writeStyle(() -> {
            cursor.waiting();
        }, fxlizer);
        assert style.property("-fx-cursor", "wait");

        style = writeStyle(() -> {
            cursor.text();
        }, fxlizer);
        assert style.property("-fx-cursor", "text");
    }

    @Test
    void borderWidth() {
        ValidatableStyle style = writeStyle(() -> {
            border.bottom.width(2, px).solid().color(Color.Black);
        }, fxlizer);
        assert style.property("-fx-border-width", "0 0 2px 0");
    }

    @Test
    void borderColor() {
        ValidatableStyle style = writeStyle(() -> {
            border.bottom.width(2, px).solid().color(Color.Black);
        }, fxlizer);
        assert style.property("-fx-border-color", "transparent transparent black transparent");
    }

    @Test
    void alignment() {
        ValidatableStyle style = writeStyle(() -> {
            text.align.center().verticalAlign.middle();
        }, fxlizer);
        assert style.property("-fx-alignment", "center");

        style = writeStyle(() -> {
            text.align.left();
        }, fxlizer);
        assert style.property("-fx-alignment", "center-left");

        style = writeStyle(() -> {
            text.align.right();
        }, fxlizer);
        assert style.property("-fx-alignment", "center-right");

        style = writeStyle(() -> {
            text.verticalAlign.middle();
        }, fxlizer);
        assert style.property("-fx-alignment", "center-left");
    }

    @Test
    void color() {
        ValidatableStyle style = writeStyle(() -> {
            font.color($.rgb(0, 0, 0));
        }, fxlizer);
        assert style.property("-fx-text-fill", "black");
    }

    @Test
    void strokeDashArray() {
        ValidatableStyle style = writeStyle(() -> {
            stroke.dashArray(1, 2);
        }, fxlizer);
        assert style.property("-fx-stroke-dash-array", "1 2");
    }

    @Test
    void textIndent() {
        ValidatableStyle style = writeStyle(() -> {
            text.indent(10, px);
        }, fxlizer);
        assert style.property("-fx-label-padding", "0 0 0 10px");
    }

    @Test
    void select() {
        ValidatableStyle style = writeStyle(() -> {
            text.unselectable();
        }, fxlizer);
        assert style.property("-fx-focus-traversable", "false");
    }
}
