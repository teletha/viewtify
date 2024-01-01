/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
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

public class UIMenuItem<M extends MenuItem>
        implements StyleHelper<UIMenuItem<M>, MenuItem>, DisableHelper<UIMenuItem<M>>, LabelHelper<UIMenuItem<M>>,
        UserActionHelper<UIMenuItem<M>> {

    /** The actual ui. */
    final M ui;

    /**
     * Enchanced view.
     * 
     * @param ui
     */
    UIMenuItem(M ui) {
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