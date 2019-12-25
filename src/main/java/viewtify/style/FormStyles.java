/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.style;

import static stylist.StyleDSL.*;

import stylist.Style;
import stylist.StyleDeclarations;

/**
 * 
 */
public interface FormStyles extends StyleDeclarations {

    Style FormRow = () -> {
        display.minHeight(30, px);
        padding.vertical(3, px);
        text.verticalAlign.middle();
    };

    Style FormLabel = () -> {
        display.minWidth(120, px).width(120, px);
        padding.top(4, px);
    };

    Style FormHeaderLabel = () -> {
        text.align.center();
    };

    Style FormInput = () -> {
        display.minWidth(170, px).width(170, px);
        margin.right(10, px);
    };

    Style FormInputMin = () -> {
        display.minWidth(60, px).width(60, px);
    };

    Style FormInputMiddle = () -> {
        display.minWidth(80, px).width(80, px);
    };

    Style FormCheck = () -> {
        display.minWidth(60, px);
        text.align.center().verticalAlign.middle();
    };

    Style FormCheck2 = () -> {
        display.minWidth(160, px);
        text.align.center().verticalAlign.middle();
    };
}