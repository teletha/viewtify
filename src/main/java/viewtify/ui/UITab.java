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

import javafx.beans.binding.Bindings;
import javafx.scene.control.Tab;

import kiss.Signal;
import kiss.Variable;
import viewtify.Viewtify;
import viewtify.ui.helper.StyleHelper;

/**
 * @version 2018/09/09 22:41:39
 */
public class UITab implements StyleHelper<UITab, Tab> {

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

    /**
     * Get text.
     * 
     * @param text
     */
    public String text() {
        return ui.getText();
    }

    /**
     * Set text.
     * 
     * @param text
     */
    public UITab text(Object text) {
        if (text instanceof Variable) {
            ui.textProperty().bind(Viewtify.calculate((Variable) text));
        } else {
            ui.textProperty().bind(Bindings.concat(text));
        }
        return this;
    }
}
