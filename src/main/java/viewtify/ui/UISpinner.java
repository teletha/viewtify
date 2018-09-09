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

import java.util.List;
import java.util.function.Function;

import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;

import viewtify.ui.helper.SelectablePreferenceHelper;

/**
 * @version 2018/09/09 12:00:56
 */
public class UISpinner<T> extends UserInterface<UISpinner<T>, Spinner<T>> implements SelectablePreferenceHelper<UISpinner<T>, T> {

    /**
     * Enchanced view.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    private UISpinner(View view) {
        super(new Internal(), view);

        ui.setOnScroll(e -> {
            if (e.getDeltaY() > 0) {
                ui.increment();
            } else if (e.getDeltaY() < 0) {
                ui.decrement();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<T> model() {
        return ui.getValueFactory().valueProperty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UISpinner<T> values(List<T> values) {
        ui.setValueFactory(new SpinnerValueFactory.ListSpinnerValueFactory(FXCollections.observableList(values)));
        return this;
    }

    /**
     * Set value-display converter.
     * 
     * @param converter
     * @return
     */
    public UISpinner<T> text(Function<T, String> converter) {
        ui.getValueFactory().setConverter(new Converter(converter));
        ui.getEditor().setText(converter.apply(ui.getValue()));

        return this;
    }

    /**
     * @version 2017/11/18 15:12:07
     */
    private class Converter extends StringConverter<T> {

        private final Function<T, String> converter;

        /**
         * @param converter
         */
        private Converter(Function<T, String> converter) {
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

    /**
     * @version 2018/09/09 23:26:36
     */
    private static class Internal extends Spinner {

        /**
         * {@inheritDoc}
         */
        @Override
        public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
            return EnhancedCSSProperty.metadata(super.getControlCssMetaData());
        }
    }
}
