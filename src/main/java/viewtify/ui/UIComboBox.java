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
import java.util.function.Function;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;

import kiss.Signal;
import viewtify.View;
import viewtify.Viewtify;
import viewtify.ui.helper.EditableHelper;
import viewtify.ui.helper.SelectablePreferenceHelper;
import viewtify.ui.helper.User;

/**
 * @version 2018/08/03 19:20:46
 */
public class UIComboBox<T> extends UserInterface<UIComboBox<T>, ComboBox<T>>
        implements SelectablePreferenceHelper<UIComboBox<T>, T>, EditableHelper<UIComboBox> {

    /**
     * Enchanced view.
     * 
     * @param ui
     */
    private UIComboBox(ComboBox<T> ui, View view) {
        super(ui, view);

        // FUNCTIONALITY : wheel scroll will change selection.
        when(User.Scroll, Action.traverse(ui.getSelectionModel()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanProperty editable() {
        return ui.editableProperty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<T> model() {
        return ui.valueProperty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UIComboBox<T> values(List<T> values) {
        ui.setItems(FXCollections.observableList(values));
        return this;
    }

    /**
     * Get current index.
     */
    public int index() {
        return ui.getSelectionModel().getSelectedIndex();
    }

    /**
     * Set value-display converter.
     * 
     * @param converter
     * @return
     */
    public UIComboBox<T> text(Function<T, String> factory) {
        if (factory != null) {
            // IMPORTANT : Don't share cell factory instance.
            ui.setCellFactory(e -> new Cell(factory));
            ui.setButtonCell(new Cell(factory));
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Signal<?> validateWhen() {
        return Viewtify.signal(model());
    }

    /**
     * @version 2017/11/18 21:42:55
     */
    private class Cell extends ListCell<T> {

        /** The text factory. */
        private final Function<T, String> factory;

        /**
         * @param factory A text factory.
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

            if (item == null || empty) {
                setText(null);
            } else {
                setText(factory.apply(item));
            }
        }
    }
}
