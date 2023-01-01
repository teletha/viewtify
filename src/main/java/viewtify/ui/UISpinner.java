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

import java.util.function.Function;

import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.ListSpinnerValueFactory;
import javafx.util.StringConverter;

import kiss.I;
import viewtify.ui.helper.CollectableHelper;
import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.User;
import viewtify.ui.helper.ValueHelper;

public class UISpinner<T> extends UserInterface<UISpinner<T>, Spinner<T>>
        implements CollectableHelper<UISpinner<T>, T>, ValueHelper<UISpinner<T>, T>, ContextMenuHelper<UISpinner<T>> {

    /**
     * Enchanced view.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    public UISpinner(View view) {
        super(new Spinner(new SpinnerValueFactory.ListSpinnerValueFactory(FXCollections.observableArrayList())), view);

        when(User.Scroll, e -> {
            if (e.getDeltaY() > 0) {
                ui.increment();
            } else if (e.getDeltaY() < 0) {
                ui.decrement();
            }
            e.consume();
        });

        I.Lang.observe().to(this::updateDisplay);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<T> valueProperty() {
        return ui.getValueFactory().valueProperty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<ObservableList<T>> itemsProperty() {
        return ((ListSpinnerValueFactory<T>) ui.getValueFactory()).itemsProperty();
    }

    /**
     * Attempts to imcrement the {@link #valueProperty() value} by the given number of steps.
     *
     * @param steps The number of increments that should be performed on the value.
     * @return Chainable API.
     */
    public UISpinner<T> increment(int steps) {
        ui.increment(steps);
        return this;
    }

    /**
     * Attempts to decrement the {@link #valueProperty() value} by the given number of steps.
     *
     * @param steps The number of decrements that should be performed on the value.
     * @return Chainable API.
     */
    public UISpinner<T> decrement(int steps) {
        ui.decrement(steps);
        return this;
    }

    /**
     * The wrapAround property is used to specify whether the value factory should be circular. For
     * example, should an integer-based value model increment from the maximum value back to the
     * minimum value (and vice versa).
     * 
     * @return Chainable API.
     */
    public UISpinner<T> around() {
        ui.getValueFactory().setWrapAround(true);
        return this;
    }

    /**
     * Set value-display formatter.
     * 
     * @param formatter A value formatter.
     * @return Chainable API.
     */
    public UISpinner<T> format(Function<T, String> formatter) {
        ui.getValueFactory().setConverter(new GenericFormatter(formatter));
        updateDisplay();

        return this;
    }

    /**
     * Update the current display.
     */
    private void updateDisplay() {
        ui.getEditor().setText(ui.getValueFactory().getConverter().toString(ui.getValue()));
    }

    /**
     * 
     */
    private class GenericFormatter extends StringConverter<T> {

        private final Function<T, String> converter;

        /**
         * @param converter
         */
        private GenericFormatter(Function<T, String> converter) {
            this.converter = converter;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString(T object) {
            return converter.apply(object);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public T fromString(String string) {
            // If this exception will be thrown, it is bug of this program. So we must rethrow the
            // wrapped error in here.
            throw new Error("Don't call!");
        }
    }
}