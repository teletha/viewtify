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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.function.Function;

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;

import viewtify.ui.helper.ValueHelper;

public class UISlider extends UserInterface<UISlider, HBox> implements ValueHelper<UISlider, Double> {

    private final Label input = new Label();

    private final Slider slider = new Slider();

    private Function<String, String> formatter = Function.identity();

    private ChangeListener<? super Number> stepAdjuster;

    /**
     * @param view
     */
    public UISlider(View view) {
        super(new HBox(), view);
        ui.getStyleClass().add("slider-wrapper");
        ui.getChildren().addAll(input, slider);
        bind();
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

    /**
     * Set the step for each values.
     * 
     * @param step
     * @return
     */
    public UISlider step(double step) {
        if (0 < step) {
            if (stepAdjuster != null) {
                slider.valueProperty().removeListener(stepAdjuster);
            }

            slider.valueProperty().addListener(stepAdjuster = (x, o, n) -> {
                slider.adjustValue(Math.round(n.doubleValue() / step) * step);
            });
        }
        return this;
    }

    /**
     * Format label.
     * 
     * @param formatter
     * @return
     */
    public UISlider format(Function<String, String> formatter) {
        this.formatter = Objects.requireNonNull(formatter);

        bind();

        return this;
    }

    /**
     * Bind label text.
     */
    private void bind() {
        input.textProperty().unbind();
        input.textProperty()
                .bind(slider.valueProperty()
                        .map(x -> formatter.apply(BigDecimal.valueOf(x.doubleValue()).setScale(0, RoundingMode.FLOOR).toPlainString())));
    }
}