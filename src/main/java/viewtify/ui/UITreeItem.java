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

import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;

import kiss.Signal;
import viewtify.Viewtify;
import viewtify.bind.CalculationList;

/**
 * @version 2017/12/04 14:50:11
 */
public class UITreeItem<T> {

    private final TreeTableView<T> table;

    /** The actual tree item. */
    private final TreeItem<T> ui;

    /**
     * Create root item.
     * 
     * @param item
     */
    UITreeItem(TreeTableView<T> table, TreeItem item) {
        this.table = table;
        this.ui = item;
    }

    /**
     * Create tree item with value.
     * 
     * @param value
     */
    private UITreeItem(TreeTableView<T> table, T value) {
        this.table = table;
        this.ui = new TreeItem<>(value);
    }

    /**
     * Get the associated value.
     * 
     * @return
     */
    public T value() {
        return ui.getValue();
    }

    /**
     * Get the list of child values.
     * 
     * @return
     */
    public CalculationList<T> values() {
        return Viewtify.calculate(ui.getChildren()).map(i -> i.getValue());
    }

    /**
     * Create a child item at last with value.
     * 
     * @param value A child item value.
     * @return A created item.
     */
    public <R> UITreeItem<R> createItem(R value) {
        UITreeItem child = new UITreeItem(table, value);
        ui.getChildren().add(child.ui);

        return child;
    }

    /**
     * Create a child item at last with value if the specified value is not associated.
     * 
     * @param value A child item value.
     * @return A created item.
     */
    public <R> UITreeItem<R> createItemIfAbsent(R value) {
        ObservableList<TreeItem<T>> children = ui.getChildren();

        for (TreeItem<T> child : children) {
            if (child.getValue() == value) {
                return new UITreeItem(table, child);
            }
        }
        return createItem(value);
    }

    /**
     * Sets the expanded state of this TreeItem. This has no effect on a TreeItem with no children.
     * On a TreeItem with children however, the result of toggling this property is that visually
     * the children will either become visible or hidden, based on whether expanded is set to true
     * or false.
     *
     * @param value If this TreeItem has children, calling setExpanded with <code>true</code> will
     *            result in the children becoming visible. Calling setExpanded with
     *            <code>false</code> will hide any children belonging to the TreeItem.
     */
    public UITreeItem<T> expand(boolean value) {
        ui.setExpanded(value);

        return this;
    }

    /**
     * Remove this item from parent when the specified {@link Signal} emits first signal.
     * 
     * @param timing
     * @return
     */
    public UITreeItem<T> removeWhen(Signal timing) {
        timing.take(1).on(Viewtify.UIThread).to(() -> {
            // if this item is selected, clear selection too
            for (int index : table.getSelectionModel().getSelectedIndices()) {
                if (table.getTreeItem(index) == ui) {
                    table.getSelectionModel().clearSelection(index);
                    break;
                }
            }

            // remove from tree item model
            ui.getParent().getChildren().remove(ui);
        });

        return this;
    }

    /**
     * Remove this item from parent when the specified {@link Signal} emits first signal.
     * 
     * @param timing
     * @return
     */
    public UITreeItem<T> removeWhen(Function<T, Signal> timing) {
        return removeWhen(timing.apply(ui.getValue()));
    }

    /**
     * Remove this item from parent when this item has no child.
     * 
     * @return
     */
    public UITreeItem<T> removeWhenEmpty() {
        return removeWhen(Viewtify.signal(ui.getChildren()).take(Change::wasRemoved).take(c -> ui.getChildren().isEmpty()));
    }
}
