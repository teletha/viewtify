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
import javafx.beans.value.ObservableBooleanValue;

import org.controlsfx.control.ToggleSwitch;

import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.LabelHelper;
import viewtify.ui.helper.ValueHelper;

public class UICheckSwitch extends UserInterface<UICheckSwitch, ToggleSwitch>
        implements ValueHelper<UICheckSwitch, Boolean>, ContextMenuHelper<UICheckSwitch>, LabelHelper<UICheckSwitch> {

    /**
     * Enchanced view.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    public UICheckSwitch(View view) {
        super(new ToggleSwitch(), view);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<Boolean> valueProperty() {
        return ui.selectedProperty();
    }

    /**
     * Return true when this checkbox is selected.
     * 
     * @return
     */
    public ObservableBooleanValue isSelected() {
        return ui.selectedProperty();
    }

    /**
     * Return true when this checkbox is NOT selected.
     * 
     * @return
     */
    public ObservableBooleanValue isNotSelected() {
        return ui.selectedProperty().not();
    }
}