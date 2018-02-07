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

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TableSelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;

import viewtify.View;
import viewtify.Viewtify;

/**
 * @version 2018/02/07 16:46:46
 */
public class UITreeTableView<T> extends AbstractTableView<UITreeTableView<T>, TreeTableView<T>, T> {

    /** The root item. */
    public final UITreeItem<T> root;

    /**
     * Enchanced view.
     * 
     * @param ui
     */
    private UITreeTableView(TreeTableView<T> ui, View view) {
        super(ui, view, Viewtify.calculate(ui.getSelectionModel().getSelectedItems()).map(TreeItem<T>::getValue));

        TreeItem item = new TreeItem();
        ui.setRoot(item);
        ui.setShowRoot(false);

        root = new UITreeItem(ui, item);
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
    public UITreeTableView<T> render(Function<UITreeTableView<T>, TreeTableRow<T>> renderer) {
        ui.setRowFactory(table -> renderer.apply(this));

        return this;
    }
}
