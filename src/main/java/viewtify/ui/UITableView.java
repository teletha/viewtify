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
import javafx.scene.Node;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

import viewtify.View;
import viewtify.Viewtify;
import viewtify.bind.CalculationList;

/**
 * @version 2017/11/15 9:54:15
 */
public class UITableView<T> extends UIControl<UITableView, TableView<T>> {

    /** The root item. */
    public final ObservableList<T> values;

    /**
     * Enchanced view.
     * 
     * @param ui
     */
    private UITableView(TableView<T> ui, View view) {
        super(ui, view);

        values = ui.getItems();
    }

    public UITableView<T> placeholder(String text) {
        return placeholder(new Text(text));
    }

    public UITableView<T> placeholder(Node node) {
        ui.setPlaceholder(node);
        return this;
    }

    /**
     * <p>
     * Specifies the selection mode to use in this selection model. The selection mode specifies how
     * many items in the underlying data model can be selected at any one time.
     * <p>
     */
    public UITableView<T> selectMultipleRows() {
        ui.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        return this;
    }

    /**
     * <p>
     * Specifies the selection mode to use in this selection model. The selection mode specifies how
     * many items in the underlying data model can be selected at any one time.
     * <p>
     */
    public UITableView<T> selectSingleRow() {
        ui.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        return this;
    }

    /**
     * Specifies ROW renderer.
     */
    public UITableView<T> render(Function<UITableView<T>, TableRow<T>> renderer) {
        ui.setRowFactory(table -> renderer.apply(this));

        return this;
    }

    /**
     * Get all selected values.
     * 
     * @return
     */
    public CalculationList<T> getSelected() {
        return Viewtify.calculate(ui.getSelectionModel().getSelectedItems());
    }
}
