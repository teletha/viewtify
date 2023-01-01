/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import java.util.function.Function;

import javafx.beans.property.Property;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

import viewtify.ui.helper.User;

public class UITableView<RowV> extends UITableBase<RowV, TableView<RowV>, UITableView<RowV>> {

    /**
     * Enchanced view.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    public UITableView(View view) {
        super(new TableView(), view);

        placeholder("");
        when(User.Sort, () -> sort(ui.getComparator()));

        // Use user data properties to pass UI instances to TableColumn.
        ui.getProperties().put(UITableView.class, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<ObservableList<RowV>> itemsProperty() {
        return ui.itemsProperty();
    }

    /**
     * Configure item renderer.
     */
    public UITableView<RowV> render(Function<UITableView<RowV>, TableRow<RowV>> renderer) {
        ui.setRowFactory(table -> renderer.apply(this));
        return this;
    }

    public UITableView<RowV> operatable(boolean enable) {
        for (TableColumn column : ui.getColumns()) {
            column.setSortable(enable);
            column.setResizable(enable);
            column.setReorderable(enable);
        }

        ui.getColumns().addListener((ListChangeListener<TableColumn<RowV, ?>>) change -> {
            while (change.next()) {
                for (TableColumn column : change.getAddedSubList()) {
                    column.setSortable(enable);
                    column.setResizable(enable);
                    column.setReorderable(enable);
                }
            }
        });
        return this;
    }

    /**
     * Remove all table decoration styles and behaviors.
     * 
     * @return
     */
    public UITableView<RowV> simplify() {
        ui.getStyleClass().add("simple");
        ui.setSelectionModel(null);
        ui.setFocusTraversable(false);
        return operatable(false);
    }
}