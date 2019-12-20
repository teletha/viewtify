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

import org.controlsfx.control.IndexedCheckModel;

class MultipleCheckModel<E> extends MultipleSelectionModel<E> {
    /**
     * 
     */
    private final IndexedCheckModel<E> model;

    /**
     * @param model
     */
    MultipleCheckModel(IndexedCheckModel<E> model) {
        this.model = model;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObservableList<Integer> getSelectedIndices() {
        return model.getCheckedIndices();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObservableList<E> getSelectedItems() {
        return model.getCheckedItems();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void selectIndices(int index, int... indices) {
        model.check(index);
        model.checkIndices(indices);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void selectAll() {
        model.checkAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void selectFirst() {
        if (0 < model.getItemCount()) {
            model.check(0);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void selectLast() {
        int count = model.getItemCount();

        if (0 < count) {
            model.check(count - 1);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearAndSelect(int index) {
        model.clearCheck(index);
        model.check(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void select(int index) {
        model.check(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void select(E obj) {
        model.check(obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearSelection(int index) {
        model.clearCheck(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearSelection() {
        model.clearChecks();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSelected(int index) {
        return model.isChecked(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return model.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void selectPrevious() {
        throw new Error();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void selectNext() {
        throw new Error();
    }
}