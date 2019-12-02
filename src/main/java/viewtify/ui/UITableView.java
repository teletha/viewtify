/*
 * Copyright (C) 2019 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import java.util.function.Function;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableSelectionModel;
import javafx.scene.control.TableView;

import viewtify.Viewtify;
import viewtify.ui.helper.CollectableHelper;

public class UITableView<T> extends AbstractTableView<UITableView<T>, TableView<T>, T> implements CollectableHelper<UITableView<T>, T> {

    /**
     * Enchanced view.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    private UITableView(View view) {
        super(new TableView(), view, ui -> Viewtify.calculate(ui.getSelectionModel().getSelectedItems()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<ObservableList<T>> items() {
        return ui.itemsProperty();
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
     * Configure item renderer.
     */
    public UITableView<T> render(Function<UITableView<T>, TableRow<T>> renderer) {
        ui.setRowFactory(table -> renderer.apply(this));
        return this;
    }
}
