/*
 * Copyright (C) 2018 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import java.util.List;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.scene.control.Label;

import viewtify.ui.helper.LabelHelper;

/**
 * @version 2018/09/09 11:47:48
 */
public class UILabel extends UserInterface<UILabel, Label> implements LabelHelper<UILabel, Label> {

    /**
     * Build {@link Label}.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    UILabel(View view) {
        super(new Internal(), view);
    }

    /**
     * @version 2018/09/09 23:26:36
     */
    private static class Internal extends Label {

        /**
         * {@inheritDoc}
         */
        @Override
        public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
            return EnhancedCSSProperty.metadata(super.getControlCssMetaData());
        }
    }
}
