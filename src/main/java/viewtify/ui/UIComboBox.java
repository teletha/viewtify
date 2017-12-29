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
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;

import kiss.I;
import kiss.Signal;
import viewtify.View;

/**
 * @version 2017/11/15 9:54:15
 */
public class UIComboBox<T> extends UserInterface<UIComboBox, ComboBox<T>> {

    /**
     * Enchanced view.
     * 
     * @param ui
     */
    private UIComboBox(ComboBox<T> ui, View view) {
        super(ui, view);
    }

    /**
     * Set values.
     * 
     * @param values
     * @return
     */
    public UIComboBox<T> values(T... values) {
        return values(I.signal(values));
    }

    /**
     * Set values.
     * 
     * @param values
     * @return
     */
    public UIComboBox<T> values(Signal<T> values) {
        return values(values.toList());
    }

    /**
     * Set values.
     * 
     * @param values
     * @return
     */
    public UIComboBox<T> values(Stream<T> values) {
        return values(values.collect(Collectors.toList()));
    }

    /**
     * Set values.
     * 
     * @param values
     * @return
     */
    public UIComboBox<T> values(List<T> values) {
        ui.setItems(FXCollections.observableList(values));
        return this;
    }

    /**
     * Set initial value.
     * 
     * @param initialValue
     * @return
     */
    public UIComboBox<T> initial(T initialValue) {
        restore(ui.valueProperty(), initialValue);
        return this;
    }

    /**
     * Get current index.
     */
    public int index() {
        return ui.getSelectionModel().getSelectedIndex();
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
    public UIComboBox<T> value(T value) {
        ui.setValue(value);
        return this;
    }

    /**
     * Observe the value modification.
     * 
     * @param listener
     * @return
     */
    public UIComboBox<T> observe(Consumer<T> listener) {
        ui.valueProperty().addListener((p, o, n) -> listener.accept(n));
        return this;
    }

    /**
     * Set value-display converter.
     * 
     * @param converter
     * @return
     */
    public UIComboBox<T> text(Function<T, String> factory) {
        Cell cell = new Cell(factory);

        ui.setCellFactory(e -> cell);
        ui.setButtonCell(cell);

        return this;
    }

    /**
     * @version 2017/11/18 21:42:55
     */
    private class Cell extends ListCell<T> {

        private final Function<T, String> factory;

        /**
         * @param factory
         */
        private Cell(Function<T, String> factory) {
            this.factory = factory;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);

            if (item != null && !empty) {
                setText(factory.apply(item));
            }
        }
    }

}
