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
import javafx.scene.control.TextField;

/**
 * @version 2018/09/09 12:01:37
 */
public class UIText extends AbstractTextField<UIText, TextField> {

    /**
     * Enchanced view.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    private UIText(View view) {
        super(new Internal(), view);
    }

    /**
     * @version 2018/09/09 23:26:36
     */
    private static class Internal extends TextField {

        /**
         * {@inheritDoc}
         */
        @Override
        public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
            return ExtraCSS.metadata(super.getControlCssMetaData());
        }
    }
}
