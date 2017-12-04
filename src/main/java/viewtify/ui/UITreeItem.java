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

import javafx.collections.ListChangeListener;
import javafx.scene.control.TreeItem;

import kiss.Signal;
import viewtify.Viewtify;

/**
 * @version 2017/12/04 14:50:11
 */
public class UITreeItem<T> {

    /** The actual tree item. */
    private final TreeItem<T> ui;

    /**
     * Create root item.
     * 
     * @param item
     */
    UITreeItem(TreeItem item) {
        this.ui = item;
    }

    /**
     * Create tree item with value.
     * 
     * @param value
     */
    private UITreeItem(T value) {
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
     * Create a child item at last with value.
     * 
     * @param value A child item value.
     * @return A created item.
     */
    public <R extends T> UITreeItem<R> createItem(R value) {
        UITreeItem child = new UITreeItem(value);
        ui.getChildren().add(child.ui);

        return child;
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
        timing.take(1).on(Viewtify.UIThread).to(() -> ui.getParent().getChildren().remove(ui));

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
     * @param timing
     * @return
     */
    public UITreeItem<T> removeWhenEmpty() {
        ui.getChildren().addListener((ListChangeListener) c -> {
            while (c.next()) {
                if (c.wasRemoved()) {
                    if (ui.getChildren().isEmpty()) {
                        Viewtify.inUI(() -> {
                            ui.getParent().getChildren().remove(ui);
                        });
                    }
                }
            }
        });
        return this;
    }
}
