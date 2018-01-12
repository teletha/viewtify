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

import java.util.function.BiPredicate;
import java.util.function.Function;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import viewtify.View;
import viewtify.Viewtify;

/**
 * @version 2018/01/12 21:34:22
 */
public class UIListView<T> extends UserInterface<UIListView, ListView<T>> {

    /** The original list. */
    private final ObservableList<T> values;

    /**
     * Enchanced view.
     * 
     * @param ui
     */
    private UIListView(ListView<T> ui, View view) {
        super(ui, view);

        this.values = ui.getItems();
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

    /**
     * Add new item at the specifind index.
     * 
     * @param value
     * @return
     */
    public UIListView<T> add(int index, T value) {
        values.add(index, value);

        return this;
    }

    /**
     * Add new item at first.
     * 
     * @param value
     * @return
     */
    public UIListView<T> addFirst(T value) {
        return add(0, value);
    }

    /**
     * Add new item at last.
     * 
     * @param value
     * @return
     */
    public UIListView<T> addLast(T value) {
        values.add(value);

        return this;
    }

    /**
     * Remove an item at the specifind index.
     * 
     * @param value
     * @return
     */
    public UIListView<T> remove(int index) {
        values.remove(index);

        return this;
    }

    /**
     * Remove an item at first.
     * 
     * @param value
     * @return
     */
    public UIListView<T> removeFirst() {
        return remove(0);
    }

    /**
     * Remove an item at last.
     * 
     * @param value
     * @return
     */
    public UIListView<T> removeLast() {
        return remove(values.size() - 1);
    }

    /**
     * Add new item.
     * 
     * @param value
     * @return
     */
    public int size() {
        return values.size();
    }

    /**
     * Filter items by the specified condition.
     * 
     * @param value An additional infomation.
     * @param filter A conditional filer.
     * @return
     */
    public <V> UIListView<T> filter(ObservableValue<V> value, BiPredicate<T, V> filter) {
        ui.itemsProperty().bind(Viewtify.calculate(value, () -> new FilteredList<>(values, e -> filter.test(e, value.getValue()))));

        return this;
    }
}
