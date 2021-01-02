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

import javafx.beans.property.Property;
import javafx.scene.control.Slider;

import viewtify.ui.helper.ValueHelper;

public class UISlider extends UserInterface<UISlider, Slider> implements ValueHelper<UISlider, Double> {

    /**
     * @param ui
     * @param view
     */
    public UISlider(View view) {
        super(new Slider(), view);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<Double> valueProperty() {
        return (Property) ui.valueProperty();
    }

    /**
     * Set the min and max value of this indicator.
     * 
     * @param min A minimum value.
     * @param max A maximum value.
     * @return Chainable API.
     */
    public UISlider range(double min, double max) {
        ui.setMin(min);
        ui.setMax(max);
        return this;
    }

    /**
     * Indicates that the labels for tick marks should be shown.
     * 
     * @param show True shows it, False hides it.
     * @return Chainable API.
     */
    public UISlider showTickLabels(boolean show) {
        ui.setShowTickLabels(show);
        return this;
    }

    /**
     * Indicates that the tick marks should be shown.
     * 
     * @param show True shows it, False hides it.
     * @return Chainable API.
     */
    public UISlider showTickMarks(boolean show) {
        ui.setShowTickMarks(show);
        return this;
    }

    /**
     * Indicates whether the {@code Slider} should always be aligned with the tick marks. This is
     * honored even if the tick marks are not shown.
     * 
     * @param snap True enable snap mode.
     * @return Chaianble API.
     */
    public UISlider snapToTicks(boolean snap) {
        ui.setSnapToTicks(snap);
        return this;
    }
}