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

import java.util.function.Consumer;

import javafx.beans.property.Property;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;

import viewtify.ui.helper.DisableHelper;
import viewtify.ui.helper.LabelHelper;
import viewtify.ui.helper.StyleHelper;

public class UIMenuItem implements StyleHelper<UIMenuItem, MenuItem>, DisableHelper<UIMenuItem>, LabelHelper<UIMenuItem, MenuItem> {

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

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<Boolean> disable() {
        return ui.disableProperty();
    }

    /**
     * Helper to listen user action event.
     * 
     * @param actionType
     * @param listener
     * @return
     */
    public <T extends Event> UIMenuItem whenUserClick(Runnable listener) {
        return whenUserClick(e -> listener.run());
    }

    /**
     * Helper to listen user action event.
     * 
     * @param actionType
     * @param listener
     * @return
     */
    public <T extends Event> UIMenuItem whenUserClick(EventHandler<ActionEvent> listener) {
        ui.addEventHandler(ActionEvent.ACTION, listener);
        return this;
    }

    /**
     * Helper to listen user action event.
     * 
     * @param actionType
     * @param listener
     * @return
     */
    public <T extends Event, A> UIMenuItem whenUserClick(Consumer<A> listener, A context) {
        return whenUserClick(e -> listener.accept(context));
    }
}
