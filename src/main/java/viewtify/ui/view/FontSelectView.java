/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.view;

import java.util.stream.IntStream;

import javafx.scene.control.Label;
import javafx.scene.text.Font;

import viewtify.style.FormStyles;
import viewtify.ui.UIComboBox;
import viewtify.ui.UISpinner;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;

public class FontSelectView extends View {

    /** The family selector. */
    public UIComboBox<String> family;

    /** The size selector. */
    public UISpinner<Integer> size;

    @Override
    protected ViewDSL declareUI() {
        return new ViewDSL() {
            {
                $(hbox, () -> {
                    $(family, FormStyles.FormInput);
                    $(size, FormStyles.FormInputMin);
                });
            }
        };
    }

    @Override
    protected void initialize() {
        family.items(Font.getFamilies());
        size.items(IntStream.range(8, 18)).format(x -> x + "px");
    }

    /**
     * Apply its font to combo box list.
     * 
     * @return
     */
    public FontSelectView applySelfFont() {
        family.renderByNode(() -> new Label(), (label, value, disposer) -> {
            label.setText(value);
            label.setFont(Font.font(value));
            return label;
        });
        return this;
    }
}
