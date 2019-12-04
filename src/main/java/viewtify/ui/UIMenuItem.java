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

import javafx.beans.property.Property;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
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
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<Boolean> disable() {
        return ui.disableProperty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Event> void addEventHandler(EventType<T> eventType, EventHandler<? super T> eventHandler) {
        ui.addEventHandler(eventType, (EventHandler) eventHandler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Event> void removeEventHandler(EventType<T> eventType, EventHandler<? super T> eventHandler) {
        ui.addEventHandler(eventType, (EventHandler) eventHandler);
    }
}
