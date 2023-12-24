/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import javafx.beans.property.Property;
import javafx.scene.control.CheckMenuItem;
import viewtify.ui.helper.DisableHelper;
import viewtify.ui.helper.LabelHelper;
import viewtify.ui.helper.StyleHelper;
import viewtify.ui.helper.UserActionHelper;
import viewtify.ui.helper.ValueHelper;

public class UICheckMenuItem
        implements StyleHelper<UICheckMenuItem, CheckMenuItem>, DisableHelper<UICheckMenuItem>, LabelHelper<UICheckMenuItem>,
        UserActionHelper<UICheckMenuItem>, ValueHelper<UICheckMenuItem, Boolean> {

    /** The actual ui. */
    final CheckMenuItem ui;

    /**
     * Enchanced view.
     * 
     * @param ui
     */
    UICheckMenuItem(CheckMenuItem ui) {
        this.ui = ui;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CheckMenuItem ui() {
        return ui;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<Boolean> valueProperty() {
        return ui.selectedProperty();
    }
}