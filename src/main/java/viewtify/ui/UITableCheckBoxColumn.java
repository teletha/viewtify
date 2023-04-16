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

import javafx.collections.FXCollections;
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
            // sync to the current state
            checkbox.value(selected.contains(value.ⅰ));

            // sync state by user
            checkbox.focusable(false).observe().to(on -> {
                if (on) {
                    selected.add(value.ⅰ);
                } else {
                    selected.remove(value.ⅰ);
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

    public void select(RowV value) {
        Viewtify.inUI(() -> {
            ui.getTableView().getSelectionModel().select(value);
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