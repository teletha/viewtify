/*
 * Copyright (C) 2021 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.helper;

import java.util.function.Consumer;

import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.SingleSelectionModel;

import kiss.Variable;
import viewtify.ui.helper.SelectionModelWrappers.IndexedCheckModelWrapper;
import viewtify.ui.helper.SelectionModelWrappers.SingleSelectionModelWrapper;

public interface SelectableHelper<Self extends SelectableHelper<Self, E>, E> extends PropertyAccessHelper {

    /**
     * Retrieve the {@link MultipleSelectionModel}.
     * 
     * @return
     */
    private MultipleSelectionModel<E> model() {
        try {
            SelectionModel<E> model = property(Type.SelectionModel).getValue();
            if (model instanceof MultipleSelectionModel) {
                return (MultipleSelectionModel<E>) model;
            } else {
                return new SingleSelectionModelWrapper((SingleSelectionModel) model);
            }
        } catch (Exception e) {
            return new IndexedCheckModelWrapper(property(Type.CheckModel).getValue());
        }
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

    /**
     * Get the latest selected item.
     * 
     * @return
     */
    default Variable<E> selectedItem() {
        return Variable.of(model().getSelectedItem());
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

    /**
     * Clear selection.
     * 
     * @param item Item to unselect.
     * @return Chainable API.
     */
    default Self unselect(E item) {
        if (item != null) {
            MultipleSelectionModel<E> model = model();
            int index = model.getSelectedItems().indexOf(item);
            if (index != -1) {
                model.clearAndSelect(index);
            }
        }
        return (Self) this;
    }

    /**
     * Clear selection.
     * 
     * @param index Item index to unselect.
     * @return Chainable API.
     */
    default Self unselectAt(int index) {
        model().clearSelection(index);
        return (Self) this;
    }

    /**
     * Handle the selected item.
     * 
     * @param selected
     * @return
     */
    default Self whenSelected(Consumer<E> selected) {
        if (selected != null && this instanceof UserActionHelper) {
            ((UserActionHelper<?>) this).when(User.Action).startWithNull().flatVariable(e -> selectedItem()).to(selected::accept);
        }
        return (Self) this;
    }
}