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

import org.controlsfx.control.ToggleSwitch;

import javafx.beans.property.Property;
import javafx.beans.value.ObservableBooleanValue;
import kiss.Variable;
import viewtify.Viewtify;
import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.LabelHelper;
import viewtify.ui.helper.ValueHelper;

public class UICheckSwitch extends UserInterface<UICheckSwitch, ToggleSwitch>
        implements ValueHelper<UICheckSwitch, Boolean>, ContextMenuHelper<UICheckSwitch>, LabelHelper<UICheckSwitch> {

    /** The default label */
    private final Variable[] labels = {Variable.of("ON"), Variable.of("OFF")};

    /**
     * Enchanced view.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    public UICheckSwitch(View view) {
        super(new ToggleSwitch(), view);

        observing().on(Viewtify.UIThread).to(x -> updateLabel(), this);
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

    /**
     * Set the state labels.
     * 
     * @param on
     * @param off
     * @return
     */
    public UICheckSwitch labels(String on, String off) {
        return labels(Variable.of(on), Variable.of(off));
    }

    /**
     * Set the state labels.
     * 
     * @param on
     * @param off
     * @return
     */
    public UICheckSwitch labels(Variable<String> on, Variable<String> off) {
        if (on != null && off != null) {
            labels[0] = on;
            labels[1] = off;

            updateLabel();
        }
        return this;
    }

    private void updateLabel() {
        text(value() ? labels[0] : labels[1]);
    }
}