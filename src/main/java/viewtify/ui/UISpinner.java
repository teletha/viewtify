/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.collections.FXCollections;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;

import kiss.I;
import kiss.Signal;
import viewtify.View;

/**
 * @version 2017/11/18 1:30:40
 */
public class UISpinner<T> extends UI<UISpinner<T>, Spinner<T>> {

    /**
     * Enchanced view.
     * 
     * @param ui
     */
    private UISpinner(Spinner ui, View view) {
        super(ui, view);

        ui.setOnScroll(e -> {
            if (e.getDeltaY() > 0) {
                ui.increment();
            } else if (e.getDeltaY() < 0) {
                ui.decrement();
            }
        });
    }

    /**
     * Set values.
     * 
     * @param values
     * @return
     */
    public <E extends Enum> UISpinner<T> values(Class<E> enums) {
        return values((T[]) enums.getEnumConstants());
    }

    /**
     * Set values.
     * 
     * @param values
     * @return
     */
    public UISpinner<T> values(T... values) {
        return values(I.signal(values));
    }

    /**
     * Set values.
     * 
     * @param values
     * @return
     */
    public UISpinner<T> values(Iterable<T> values) {
        return values(I.signal(values));
    }

    /**
     * Set values.
     * 
     * @param values
     * @return
     */
    public UISpinner<T> values(Signal<T> values) {
        return values(values.toList());
    }

    /**
     * Set values.
     * 
     * @param values
     * @return
     */
    public UISpinner<T> values(Stream<T> values) {
        return values(values.collect(Collectors.toList()));
    }

    /**
     * Set values.
     * 
     * @param values
     * @return
     */
    public UISpinner<T> values(List<T> values) {
        ui.setValueFactory(new SpinnerValueFactory.ListSpinnerValueFactory(FXCollections.observableArrayList(values)));
        return this;
    }

    /**
     * Set initial value.
     * 
     * @param initialValue
     * @return
     */
    public UISpinner<T> initial(T initialValue) {
        restore(ui.getValueFactory().valueProperty(), initialValue);
        return this;
    }

    /**
     * Get current value.
     * 
     * @return
     */
    public T value() {
        return ui.getValue();
    }

    /**
     * Set current value.
     * 
     * @param value
     * @return
     */
    public UISpinner<T> value(T value) {
        ui.getValueFactory().setValue(value);
        return this;
    }

    /**
     * Observe the value modification.
     * 
     * @param listener
     * @return
     */
    public UISpinner<T> observe(Consumer<T> listener) {
        ui.getValueFactory().valueProperty().addListener((p, o, n) -> listener.accept(n));
        return this;
    }

    /**
     * Observe the value modification.
     * 
     * @param listener
     * @return
     */
    public UISpinner<T> observeNow(Consumer<T> listener) {
        observe(listener);
        listener.accept(ui.getValue());
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
}
