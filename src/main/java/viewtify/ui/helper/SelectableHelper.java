/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
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
import kiss.Signal;
import kiss.Variable;
import viewtify.Viewtify;
import viewtify.ui.helper.SelectionModelWrappers.IndexedCheckModelWrapper;
import viewtify.ui.helper.SelectionModelWrappers.SingleSelectionModelWrapper;

public interface SelectableHelper<Self extends SelectableHelper<Self, E>, E> extends PropertyAccessHelper {

    /**
     * Retrieve the {@link SelectionModel}.
     * 
     * @return
     */
    default SelectionModel<E> selectionModelProperty() {
        try {
            return property(Type.SelectionModel).getValue();
        } catch (Exception e) {
            return new IndexedCheckModelWrapper(property(Type.CheckModel).getValue());
        }
    }

    /**
     * Retrieve the {@link MultipleSelectionModel}.
     * 
     * @return
     */
    private MultipleSelectionModel<E> model() {
        try {
            SelectionModel<E> model = selectionModelProperty();
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
     * Check the current selection.
     * 
     * @return
     */
    default boolean isSelected() {
        return !selectionModelProperty().isEmpty();
    }

    /**
     * Check the current selection.
     * 
     * @return
     */
    default boolean isNotSelected() {
        return selectionModelProperty().isEmpty();
    }

    /**
     * Check the current selection by index.
     * 
     * @return
     */
    default boolean isSelectedAt(int index) {
        return selectionModelProperty().isSelected(index);
    }

    /**
     * Check the current selection by index.
     * 
     * @return
     */
    default boolean isNotSelectedAt(int index) {
        return !isSelectedAt(index);
    }

    /**
     * Handle the selected state.
     * 
     * @return
     */
    default Signal<Boolean> hasSelection() {
        return Viewtify.observing(selectionModelProperty().selectedItemProperty()).map(v -> v != null);
    }

    /**
     * Handle the selected state.
     * 
     * @return
     */
    default Signal<Boolean> hasNoSelection() {
        return hasSelection().map(v -> !v);
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
        return Variable.of(selectionModelProperty().getSelectedItem());
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
     * Get live-state list of the selected items.
     * 
     * @return
     */
    default ObservableList<Integer> selectedIndices() {
        return model().getSelectedIndices();
    }

    /**
     * Select the specified item.
     * 
     * @param item
     * @return Chainable API.
     */
    default Self select(E item) {
        if (item != null) {
            selectionModelProperty().select(item);
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
            selectionModelProperty().select(index);
        }
        return (Self) this;
    }

    /**
     * Select the item by index.
     * 
     * @param index An item index.
     * @return Chainable API.
     */
    default Self toggleAt(int index) {
        if (0 <= index) {
            SelectionModel<E> model = selectionModelProperty();
            if (model.isSelected(index)) {
                model.clearSelection(index);
            } else {
                model.select(index);
            }
        }
        return (Self) this;
    }

    /**
     * Traverse selection.
     * 
     * @return Chainable API.
     */
    default Self selectNext() {
        selectionModelProperty().selectNext();
        return (Self) this;
    }

    /**
     * Traverse selection.
     * 
     * @return Chainable API.
     */
    default Self selectPrevious() {
        selectionModelProperty().selectPrevious();
        return (Self) this;
    }

    /**
     * Traverse selection.
     * 
     * @return Chainable API.
     */
    default Self selectFirst() {
        selectionModelProperty().selectFirst();
        return (Self) this;
    }

    /**
     * Traverse selection.
     * 
     * @return Chainable API.
     */
    default Self selectLast() {
        selectionModelProperty().selectLast();
        return (Self) this;
    }

    /**
     * Clear selection.
     * 
     * @return Chainable API.
     */
    default Self unselect() {
        selectionModelProperty().clearSelection();
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
        selectionModelProperty().clearSelection(index);
        return (Self) this;
    }

    /**
     * Handle the selected item.
     * 
     * @return
     */
    default Signal<E> selected() {
        return Viewtify.observing(selectionModelProperty().selectedItemProperty()).skipNull();
    }

    /**
     * Handle the selected item.
     * 
     * @param selected
     * @return
     */
    default Self whenSelected(Consumer<E> selected) {
        if (selected != null) {
            selected().to(selected);
        }
        return (Self) this;
    }
}