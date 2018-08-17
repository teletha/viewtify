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

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableSelectionModel;
import javafx.scene.control.TableView;

import viewtify.View;
import viewtify.Viewtify;

/**
 * @version 2018/04/12 13:16:59
 */
public class UITableView<T> extends AbstractTableView<UITableView<T>, TableView<T>, T> {

    /** The items (concurrent safe, snapshot iteration). */
    public final ObservableList<T> values;

    /**
     * Enchanced view.
     * 
     * @param ui
     */
    private UITableView(TableView<T> ui, View view) {
        super(ui, view, Viewtify.calculate(ui.getSelectionModel().getSelectedItems()));

        values = Viewtify.observe(new CopyOnWriteArrayList<>(ui.getItems()));
        ui.setItems(values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ObjectProperty<Node> placeholder() {
        return ui.placeholderProperty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TableSelectionModel selectionModel() {
        return ui.getSelectionModel();
    }

    /**
     * Specifies ROW renderer.
     */
    public UITableView<T> render(Function<UITableView<T>, TableRow<T>> renderer) {
        ui.setRowFactory(table -> renderer.apply(this));

        return this;
    }
}
