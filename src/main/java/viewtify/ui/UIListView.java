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

import java.util.function.Function;

import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import viewtify.View;

/**
 * @version 2017/11/15 9:54:15
 */
public class UIListView<T> extends UI<UIListView, ListView<T>> {

    /**
     * Enchanced view.
     * 
     * @param ui
     */
    private UIListView(ListView<T> ui, View view) {
        super(ui, view);
    }

    public UIListView<T> values(ObservableList<T> values) {
        ui.setItems(values);
        return this;
    }

    public UIListView<T> cell(ListCell<T> cell) {
        ui.setCellFactory(e -> cell);
        return this;
    }

    public UIListView<T> cell(Function<UIListView<T>, ListCell<T>> factory) {
        ui.setCellFactory(e -> factory.apply(this));
        return this;
    }

    public UIListView<T> scrollTo(int index) {
        ui.scrollTo(index);
        return this;
    }

    public UIListView<T> add(T value) {
        return this;
    }
}
