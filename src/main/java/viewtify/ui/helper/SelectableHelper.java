/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
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

/**
 * An interface providing methods for handling selection in UI elements.
 *
 * @param <Self> The type of the implementing class, enabling method chaining.
 * @param <E> The type of the elements in the selection model.
 */
public interface SelectableHelper<Self extends SelectableHelper<Self, E>, E> extends PropertyAccessHelper {

    /**
     * Retrieves the {@link SelectionModel}.
     *
     * @return The {@code SelectionModel} for the implementing class.
     */
    default SelectionModel<E> selectionModelProperty() {
        try {
            return property(Type.SelectionModel).getValue();
        } catch (Exception e) {
            return new IndexedCheckModelWrapper(property(Type.CheckModel).getValue());
        }
    }

    /**
     * Retrieves the {@link MultipleSelectionModel}.
     *
     * @return The {@code MultipleSelectionModel} for the implementing class.
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
     * Checks if there is any current selection.
     *
     * @return {@code true} if there is a selection, {@code false} otherwise.
     */
    default boolean isSelected() {
        return !selectionModelProperty().isEmpty();
    }

    /**
     * Checks if there is no current selection.
     *
     * @return {@code true} if there is no selection, {@code false} otherwise.
     */
    default boolean isNotSelected() {
        return selectionModelProperty().isEmpty();
    }

    /**
     * Checks if an item at the specified index is currently selected.
     *
     * @param index The index to check.
     * @return {@code true} if the item at the index is selected, {@code false} otherwise.
     */
    default boolean isSelectedAt(int index) {
        return selectionModelProperty().isSelected(index);
    }

    /**
     * Checks if an item at the specified index is not currently selected.
     *
     * @param index The index to check.
     * @return {@code true} if the item at the index is not selected, {@code false} otherwise.
     */
    default boolean isNotSelectedAt(int index) {
        return !isSelectedAt(index);
    }

    /**
     * Provides a {@link Signal} that emits {@code true} when there is a selection, and
     * {@code false} otherwise.
     *
     * @return A {@code Signal} indicating the presence of a selection.
     */
    default Signal<Boolean> hasSelection() {
        return Viewtify.observing(selectionModelProperty().selectedItemProperty()).map(v -> v != null);
    }

    /**
     * Provides a {@link Signal} that emits {@code true} when there is no selection, and
     * {@code false} otherwise.
     *
     * @return A {@code Signal} indicating the absence of a selection.
     */
    default Signal<Boolean> hasNoSelection() {
        return hasSelection().map(v -> !v);
    }

    /**
     * Configures the {@link SelectionMode}.
     *
     * @param mode The {@code SelectionMode} to set.
     * @return The implementing class instance for method chaining.
     */
    default Self mode(SelectionMode mode) {
        if (mode != null) {
            model().setSelectionMode(mode);
        }
        return (Self) this;
    }

    /**
     * Gets the latest selected item as a {@link Variable}.
     *
     * @return A {@code Variable} containing the latest selected item.
     */
    default Variable<E> selectedItem() {
        return Variable.of(selectionModelProperty().getSelectedItem());
    }

    /**
     * Gets a live-state list of the selected items.
     *
     * @return An {@code ObservableList} containing the selected items.
     */
    default ObservableList<E> selectedItems() {
        return model().getSelectedItems();
    }

    /**
     * Gets a live-state list of the selected indices.
     *
     * @return An {@code ObservableList} containing the selected indices.
     */
    default ObservableList<Integer> selectedIndices() {
        return model().getSelectedIndices();
    }

    /**
     * Selects the specified item.
     *
     * @param item The item to select.
     * @return The implementing class instance for method chaining.
     */
    default Self select(E item) {
        if (item != null) {
            selectionModelProperty().select(item);
        }
        return (Self) this;
    }

    /**
     * Selects the item at the specified index.
     *
     * @param index The index of the item to select.
     * @return The implementing class instance for method chaining.
     */
    default Self selectAt(int index) {
        if (0 <= index) {
            selectionModelProperty().select(index);
        }
        return (Self) this;
    }

    /**
     * Toggles the selection state of the item at the specified index.
     *
     * @param index The index of the item to toggle.
     * @return The implementing class instance for method chaining.
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
     * Moves the selection to the next item.
     *
     * @return The implementing class instance for method chaining.
     */
    default Self selectNext() {
        selectionModelProperty().selectNext();
        return (Self) this;
    }

    /**
     * Moves the selection to the previous item.
     *
     * @return The implementing class instance for method chaining.
     */
    default Self selectPrevious() {
        selectionModelProperty().selectPrevious();
        return (Self) this;
    }

    /**
     * Selects the first item in the list.
     *
     * @return The implementing class instance for method chaining.
     */
    default Self selectFirst() {
        selectionModelProperty().selectFirst();
        return (Self) this;
    }

    /**
     * Selects the last item in the list.
     *
     * @return The implementing class instance for method chaining.
     */
    default Self selectLast() {
        selectionModelProperty().selectLast();
        return (Self) this;
    }

    /**
     * Clears the selection.
     *
     * @return The implementing class instance for method chaining.
     */
    default Self unselect() {
        selectionModelProperty().clearSelection();
        return (Self) this;
    }

    /**
     * Clears the selection of the specified item.
     *
     * @param item The item to unselect.
     * @return The implementing class instance for method chaining.
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
     * Clears the selection of the item at the specified index.
     *
     * @param index The index of the item to unselect.
     * @return The implementing class instance for method chaining.
     */
    default Self unselectAt(int index) {
        selectionModelProperty().clearSelection(index);
        return (Self) this;
    }

    /**
     * Provides a {@link Signal} that emits the selected item.
     *
     * @return A {@code Signal} containing the selected item.
     */
    default Signal<E> selected() {
        return Viewtify.observing(selectionModelProperty().selectedItemProperty()).skipNull();
    }

    /**
     * Registers a consumer to be called when an item is selected.
     *
     * @param selected The consumer to be called with the selected item.
     * @return The implementing class instance for method chaining.
     */
    default Self whenSelected(Consumer<E> selected) {
        if (selected != null) {
            selected().to(selected);
        }
        return (Self) this;
    }
}