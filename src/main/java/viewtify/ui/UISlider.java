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

import java.math.BigDecimal;
import java.math.RoundingMode;

import javafx.beans.property.Property;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import viewtify.ui.helper.ValueHelper;

public class UISlider extends UserInterface<UISlider, HBox> implements ValueHelper<UISlider, Double> {

    private final Label input = new Label();

    private final Slider slider = new Slider();

    /**
     * @param view
     */
    public UISlider(View view) {
        super(new HBox(), view);

        input.setStyle("-fx-pref-width: 60px; -fx-alignment: center; -fx-padding: -2px 0 0 0;");
        input.textProperty()
                .bind(slider.valueProperty()
                        .map(x -> BigDecimal.valueOf(x.doubleValue()).setScale(0, RoundingMode.HALF_DOWN).toPlainString()));

        ui.setPadding(new Insets(2, 0, -2, 0));
        ui.getChildren().addAll(input, slider);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<Double> valueProperty() {
        return (Property) slider.valueProperty();
    }

    /**
     * Set the min and max value of this indicator.
     * 
     * @param min A minimum value.
     * @param max A maximum value.
     * @return Chainable API.
     */
    public UISlider range(int min, int max) {
        slider.setMin(min);
        slider.setMax(max);
        return this;
    }
}