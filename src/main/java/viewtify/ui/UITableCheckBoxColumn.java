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
import javafx.scene.control.TableView.TableViewSelectionModel;
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
            ObservableList<RowV> items = ui.getTableView().getItems();
            TableViewSelectionModel<RowV> model = ui.getTableView().getSelectionModel();

            int index = items.indexOf(value.ⅰ);
            if (model.isSelected(index)) {
                checkbox.value(true);
            }

            checkbox.focusable(false).observing().to(on -> {
                if (on) {
                    selected.add(value.ⅰ);
                } else {
                    selected.remove(value.ⅰ);
                }
            }, disposer.add(() -> {
                selected.remove(value.ⅰ);
                checkbox.value(false); // reset value for redrawing
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