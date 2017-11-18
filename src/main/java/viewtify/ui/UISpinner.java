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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.collections.FXCollections;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

import kiss.I;
import kiss.Signal;

/**
 * @version 2017/11/18 1:30:40
 */
public class UISpinner<T> extends UI<UISpinner<T>, Spinner<T>> {

    /**
     * Enchanced view.
     * 
     * @param ui
     */
    private UISpinner(Spinner ui) {
        super(ui);

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
}
