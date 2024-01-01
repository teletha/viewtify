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

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import kiss.Managed;
import kiss.Signal;
import viewtify.Viewtify;

public class UITableCheckBoxColumn<RowV> extends UITableColumn<RowV, RowV> {

    @Managed
    private final ObservableList<RowV> selected = FXCollections.observableArrayList();

    private final ObservableList<RowV> unmodifiable = FXCollections.unmodifiableObservableList(selected);

    /**
     * Enchanced view.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    public UITableCheckBoxColumn(View view, Class<RowV> rowType) {
        super(view, rowType, rowType);

        renderAsCheckBox((checkbox, value, disposer) -> {
            RowV v = value.ⅰ;

            // sync to the current state
            checkbox.value(selected.contains(v));

            // sync from model to ui
            ListChangeListener<RowV> listener = change -> {
                while (change.next()) {
                    if (change.getRemoved().contains(v)) {
                        checkbox.value(false);
                    }
                    if (change.getAddedSubList().contains(v)) {
                        checkbox.value(true);
                    }
                }
            };
            selected.addListener(listener);
            disposer.add(() -> selected.removeListener(listener));

            // sync from ui to model
            checkbox.focusable(false).observe().to(on -> {
                if (on) {
                    if (!selected.contains(v)) {
                        selected.add(v);
                    }
                } else {
                    if (selected.contains(v)) {
                        selected.remove(v);
                    }
                }
            }, disposer);
        });
    }

    /**
     * Retrieve the selected items.
     * 
     * @return
     */
    public ObservableList<RowV> selected() {
        return unmodifiable;
    }

    /**
     * Select checkbox by value.
     * 
     * @param value
     */
    public void select(RowV value) {
        Viewtify.inUI(() -> {
            if (!selected.contains(value)) {
                selected.add(value);
            }
        });
    }

    /**
     * Select all checkboxes.
     */
    public void selectAll() {
        for (RowV value : ui.getTableView().getItems()) {
            select(value);
        }
    }

    /**
     * Deselect all checkboxes.
     */
    public void deselectAll() {
        Viewtify.inUI(() -> {
            selected.clear();
        });
    }

    /**
     * Observe selection state.
     * 
     * @return
     */
    public Signal<Boolean> isSelected() {
        return Viewtify.observing(selected).map(list -> !list.isEmpty());
    }

    /**
     * Observe selection state.
     * 
     * @return
     */
    public Signal<Boolean> isNotSelected() {
        return Viewtify.observing(selected).map(ObservableList::isEmpty);
    }
}