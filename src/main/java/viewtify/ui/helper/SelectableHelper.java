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

import javafx.scene.control.SelectionModel;
import kiss.Variable;

public interface SelectableHelper<Self extends SelectableHelper<Self, E>, E> extends PropertyAccessHelper {

    /**
     * Retrieve the {@link SelectionModel}.
     * 
     * @return
     */
    private SelectionModel<E> model() {
        try {
            return property(Type.SelectionModel).getValue();
        } catch (Exception e) {
            return new MultipleCheckModel(property(Type.CheckModel).getValue());
        }
    }

    /**
     * Get the latest selected item.
     * 
     * @return
     */
    default Variable<E> selectedItem() {
        return Variable.of(model().getSelectedItem());
    }

    /**
     * Select the specified item.
     * 
     * @param item
     * @return Chainable API.
     */
    default Self select(E item) {
        if (item != null) {
            model().select(item);
        }
        return (Self) this;
    }

    /**
     * Select the item by index.
     * 
     * @param index An item index.
     * @return Chainable API.
     */
    default Self selectAt(int index) {
        if (0 <= index) {
            model().select(index);
        }
        return (Self) this;
    }

    /**
     * Traverse selection.
     * 
     * @return Chainable API.
     */
    default Self selectNext() {
        model().selectNext();
        return (Self) this;
    }

    /**
     * Traverse selection.
     * 
     * @return Chainable API.
     */
    default Self selectPrevious() {
        model().selectPrevious();
        return (Self) this;
    }

    /**
     * Traverse selection.
     * 
     * @return Chainable API.
     */
    default Self selectFirst() {
        model().selectFirst();
        return (Self) this;
    }

    /**
     * Traverse selection.
     * 
     * @return Chainable API.
     */
    default Self selectLast() {
        model().selectLast();
        return (Self) this;
    }

    /**
     * Clear selection.
     * 
     * @return Chainable API.
     */
    default Self unselect() {
        model().clearSelection();
        return (Self) this;
    }
}
