/*
 * Copyright (C) 2021 viewtify Development Team
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
import viewtify.ui.helper.CollectableHelper;
import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.PlaceholderHelper;
import viewtify.ui.helper.SelectableHelper;
import viewtify.ui.helper.User;

public class UITableView<T> extends UserInterface<UITableView<T>, TableView<T>>
        implements SelectableHelper<UITableView<T>, T>, CollectableHelper<UITableView<T>, T>, PlaceholderHelper<UITableView<T>>,
        ContextMenuHelper<UITableView<T>> {

    static final String ColumnFilter = "column-filter";

    /**
     * Enchanced view.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    public UITableView(View view) {
        super(new TableView(), view);

        placeholder("");
        when(User.Sort, () -> sort(ui.getComparator()));
        
        ui.setUserData(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<ObservableList<T>> itemsProperty() {
        return ui.itemsProperty();
    }

    /**
     * Configure item renderer.
     */
    public UITableView<T> render(Function<UITableView<T>, TableRow<T>> renderer) {
        ui.setRowFactory(table -> renderer.apply(this));
        return this;
    }

    public UITableView<T> operatable(boolean enable) {
        for (TableColumn column : ui.getColumns()) {
            column.setSortable(enable);
            column.setResizable(enable);
            column.setReorderable(enable);
        }

        ui.getColumns().addListener((ListChangeListener<TableColumn<T, ?>>) change -> {
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
    public UITableView<T> simplify() {
        ui.getStyleClass().add("simple");
        ui.setSelectionModel(null);
        ui.setFocusTraversable(false);
        return operatable(false);
    }
}