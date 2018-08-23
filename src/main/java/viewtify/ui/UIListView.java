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
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import kiss.Disposable;
import kiss.Signal;
import viewtify.View;
import viewtify.Viewtify;
import viewtify.bind.Calculation;
import viewtify.ui.helper.PreferenceHelper;

/**
 * @version 2018/01/12 21:34:22
 */
public class UIListView<T> extends UserInterface<UIListView, ListView<T>> {

    /** The original list. */
    private ObservableList<T> values;

    private Calculation<Predicate<T>> filter;

    /** The list ui refresher. */
    private Disposable refresher;

    /**
     * Enchanced view.
     * 
     * @param ui
     */
    private UIListView(ListView<T> ui, View view) {
        super(ui, view);

        this.values = ui.getItems();
    }

    public UIListView<T> values(List<T> values, Signal<?> refreshTiming) {
        if (refresher != null) {
            refresher.dispose();
        }
        refresher = refreshTiming.to(ui::refresh);
        return values(FXCollections.observableList(values));
    }

    public UIListView<T> values(ObservableList<T> values) {
        this.values = values;
        bind();
        return this;
    }

    private void bind() {
        ui.itemsProperty().bind(Viewtify.calculate(filter).map(f -> f == null ? values : new FilteredList<T>(values, f)));
    }

    public UIListView<T> cell(ListCell<T> cell) {
        ui.setCellFactory(e -> cell);
        return this;
    }

    public UIListView<T> cell(Function<UIListView<T>, ListCell<T>> factory) {
        ui.setCellFactory(e -> factory.apply(this));
        return this;
    }

    /**
     * Scroll helper.
     * 
     * @return
     */
    public UIListView<T> scrollTo(int index) {
        ui.scrollTo(index);
        return this;
    }

    /**
     * Scroll helper.
     * 
     * @return
     */
    public UIListView<T> scrollToBottom() {
        return scrollTo(ui.getItems().size() - 1);
    }

    /**
     * Scroll helper.
     * 
     * @return
     */
    public UIListView<T> scrollToTop() {
        return scrollTo(0);
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
    public <V> UIListView<T> take(ObservableValue<V> value, BiPredicate<T, V> filter) {
        this.filter = Viewtify.calculate(value, () -> t -> filter.test(t, value.getValue()));
        bind();

        return this;
    }

    /**
     * Filter items by the specified condition.
     * 
     * @param value An additional infomation.
     * @param filter A conditional filer.
     * @return
     */
    public <V> UIListView<T> take(PreferenceHelper<?, V> value, BiPredicate<T, V> filter) {
        return take(value.model(), filter);
    }
}
