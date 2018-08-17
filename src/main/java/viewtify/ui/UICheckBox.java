/*
 * Copyright (C) 2018 viewtify Development Team
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
import javafx.scene.control.CheckBox;

import viewtify.View;
import viewtify.ui.helper.PreferenceHelper;

/**
 * @version 2018/01/28 18:09:48
 */
public class UICheckBox extends UserInterface<UICheckBox, CheckBox> implements PreferenceHelper<UICheckBox, Boolean> {

    /**
     * Enchanced view.
     * 
     * @param ui
     */
    private UICheckBox(CheckBox ui, View view) {
        super(ui, view);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<Boolean> model() {
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
