/*
 * Copyright (C) 2021 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import javafx.scene.control.MenuItem;

import viewtify.ui.helper.DisableHelper;
import viewtify.ui.helper.LabelHelper;
import viewtify.ui.helper.StyleHelper;
import viewtify.ui.helper.UserActionHelper;

public class UIMenuItem
        implements StyleHelper<UIMenuItem, MenuItem>, DisableHelper<UIMenuItem>, LabelHelper<UIMenuItem>, UserActionHelper<UIMenuItem> {

    /** The actual ui. */
    final MenuItem ui;

    /**
     * Enchanced view.
     * 
     * @param ui
     */
    UIMenuItem(MenuItem ui) {
        this.ui = ui;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MenuItem ui() {
        return ui;
    }
}