/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.helper;

import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SelectionModel;

import kiss.Variable;

public interface SelectableHelper<Self extends SelectableHelper<Self, E>, E> extends CollectableHelper<Self, E> {

    /**
     * Get the {@link SelectionModel}.
     * 
     * @return
     */
    MultipleSelectionModel<E> selectionModel();

    /**
     * Get the latest selected item.
     * 
     * @return
     */
    default Variable<E> selectedItem() {
        return Variable.of(selectionModel().getSelectedItem());
    }

    /**
     * Get live-state list of the selected items.
     * 
     * @return
     */
    default ObservableList<E> selectedItems() {
        return selectionModel().getSelectedItems();
    }

    /**
     * Config {@link SelectionMode}.
     * 
     * @param mode
     * @return Chainable API.
     */
    default Self mode(SelectionMode mode) {
        if (mode != null) {
            selectionModel().setSelectionMode(mode);
        }
        return (Self) this;
    }
}
