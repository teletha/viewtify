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

import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.scene.control.TableSelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;

import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.PlaceholderHelper;
import viewtify.ui.helper.SelectableHelper;

public class UITreeTableView<T> extends UserInterface<UITreeTableView<T>, TreeTableView<T>>
        implements SelectableHelper<UITreeTableView<T>, T>, PlaceholderHelper<UITreeTableView<T>>, ContextMenuHelper<UITreeTableView<T>> {

    /** The root item. */
    public final UITreeItem<T> root;

    /**
     * Enchanced view.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    private UITreeTableView(View view) {
        super(new TreeTableView(), view);

        TreeItem item = new TreeItem();
        ui.setRoot(item);
        ui.setShowRoot(false);

        root = new UITreeItem(ui, item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<ObservableList<T>> items() {
        // If this exception will be thrown, it is bug of this program. So we must rethrow the
        // wrapped error in here.
        throw new Error();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TableSelectionModel selectionModel() {
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
