/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.dsl;

import java.util.function.Consumer;

import javafx.css.Styleable;

import viewtify.dsl.UIDefinition.UINode;
import viewtify.ui.helper.StyleHelper;

/**
 * @version 2018/08/29 14:55:22
 */
public interface Style extends stylist.Style, Consumer<UINode> {

    /**
     * {@inheritDoc}
     */
    @Override
    default void accept(UINode parent) {
        if (parent.node instanceof Styleable) {
            StyleHelper.of((Styleable) parent.node).style(name());
        }
    }
}
