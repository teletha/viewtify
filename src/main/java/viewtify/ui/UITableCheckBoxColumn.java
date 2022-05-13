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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UITableCheckBoxColumn<RowV> extends UITableColumn<RowV, RowV> {

    private final ObservableList<RowV> selected = FXCollections.observableArrayList();

    private final ObservableList<RowV> unmodifiable = FXCollections.unmodifiableObservableList(selected);

    /**
     * Enchanced view.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    public UITableCheckBoxColumn(View view, Class<RowV> rowType) {
        super(view, rowType, rowType);

        renderAsCheckBox((ui, value, disposer) -> {
            ui.focusable(false).observing().to(on -> {
                if (on) {
                    selected.add(value.ⅰ);
                } else {
                    selected.remove(value.ⅰ);
                }
            }, disposer.add(() -> {
                selected.remove(value.ⅰ);
            }));
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
}