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

public interface MultiSelectableHelper<Self extends MultiSelectableHelper<Self, E>, E> extends SelectableHelper<Self, E> {

    /**
     * Retrieve the {@link SelectionModel}.
     * 
     * @return
     */
    private MultipleSelectionModel<E> model() {
        return (MultipleSelectionModel<E>) property(Type.SelectionModel).getValue();
    }

    /**
     * Get live-state list of the selected items.
     * 
     * @return
     */
    default ObservableList<E> selectedItems() {
        return model().getSelectedItems();
    }

    /**
     * Config {@link SelectionMode}.
     * 
     * @param mode
     * @return Chainable API.
     */
    default Self mode(SelectionMode mode) {
        if (mode != null) {
            model().setSelectionMode(mode);
        }
        return (Self) this;
    }
}