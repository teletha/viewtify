/*
 * Copyright (C) 2019 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import javafx.scene.control.Tab;

import kiss.Signal;
import viewtify.Viewtify;
import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.LabelHelper;
import viewtify.ui.helper.StyleHelper;

public class UITab implements StyleHelper<UITab, Tab>, LabelHelper<UITab>, ContextMenuHelper<UITab> {

    /** The actual ui. */
    public final Tab ui;

    /** The event signal. */
    public final Signal<UITab> closing;

    /** The event signal. */
    public final Signal<UITab> closed;

    /**
     * @param tab
     */
    public UITab(Tab tab) {
        this.ui = tab;
        this.closing = Viewtify.observe(ui.onCloseRequestProperty(), this);
        this.closed = Viewtify.observe(ui.onClosedProperty(), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tab ui() {
        return ui;
    }
}
