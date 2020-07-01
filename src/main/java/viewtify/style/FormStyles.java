/*
 * Copyright (C) 2020 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.style;

import static stylist.StyleDSL.*;

import stylist.Style;
import stylist.StyleDeclarable;

/**
 * Built-in form CSS
 */
public interface FormStyles extends StyleDeclarable {

    Style FormRow = () -> {
        display.minHeight(30, px);
        padding.vertical(3, px);
        text.verticalAlign.middle();
    };

    Style FormLabel = () -> {
        display.width(120, px);
        padding.top(4, px);
    };

    Style FormLabelMin = () -> {
        $.child().select(FormLabel, () -> {
            display.width(70, px).minWidth(70, px);
        });
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
}