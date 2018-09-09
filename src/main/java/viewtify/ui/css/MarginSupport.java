/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.css;

import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;

/**
 * @version 2018/09/09 10:06:42
 */
public class MarginSupport {

    private static final CssMetaData<Node, Insets> Margin = new CssMetaData("-fx-margin", StyleConverter.getInsetsConverter()) {

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isSettable(Styleable styleable) {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StyleableProperty getStyleableProperty(Styleable styleable) {
            return null;
        }
    };
}
