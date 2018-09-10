/*
 * Copyright (C) 2018 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify;

import javafx.css.Styleable;

import viewtify.UI.UINode;
import viewtify.ui.helper.StyleHelper;

/**
 * Declared interface style.
 * 
 * @version 2018/09/05 14:03:55
 */
public interface Style extends stylist.Style {

    /**
     * {@inheritDoc}
     */
    @Override
    default void accept(Object parent) {
        if (parent instanceof UINode) {
            UINode p = (UINode) parent;

            if (p.node instanceof Styleable) {
                StyleHelper.of((Styleable) p.node).style(this);
            }
        }
    }
}
