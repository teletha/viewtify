/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
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
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;

public class UITreeTableView<RowV> extends UITableBase<RowV, TreeTableView<RowV>, UITreeTableView<RowV>> {

    /** The root item. */
    public final UITreeItem<RowV> root;

    /**
     * Enchanced view.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    public UITreeTableView(View view) {
        super(new TreeTableView(), view);

        TreeItem item = new TreeItem();
        ui.setRoot(item);
        ui.setShowRoot(false);

        root = new UITreeItem(ui, item);

        // Use user data properties to pass UI instances to TableColumn.
        ui.getProperties().put(UITreeTableView.class, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<ObservableList<RowV>> itemsProperty() {
        // If this exception will be thrown, it is bug of this program. So we must rethrow the
        // wrapped error in here.
        throw new Error();
    }

    /**
     * Specifies ROW renderer.
     */
    public UITreeTableView<RowV> render(Function<UITreeTableView<RowV>, TreeTableRow<RowV>> renderer) {
        ui.setRowFactory(table -> renderer.apply(this));

        return this;
    }
}