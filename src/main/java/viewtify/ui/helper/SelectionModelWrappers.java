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

import org.controlsfx.control.IndexedCheckModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SingleSelectionModel;
import viewtify.Viewtify;

class SelectionModelWrappers {

    /**
     * For {@link SingleSelectionModel}.
     */
    static class SingleSelectionModelWrapper<E> extends MultipleSelectionModel<E> {

        /** The actual model. */
        private final SingleSelectionModel<E> model;

        /** The index list. */
        private ObservableList<Integer> indices;

        /** The item list. */
        private ObservableList<E> items;

        /**
         * 
         */
        SingleSelectionModelWrapper(SingleSelectionModel<E> model) {
            this.model = model;

            indices = FXCollections.observableArrayList();
            Viewtify.observe(model.selectedIndexProperty()).as(Integer.class).to(index -> {
                setSelectedIndex(index);
                if (indices.isEmpty()) {
                    indices.add(index);
                } else {
                    indices.set(0, index);
                }
            });

            items = FXCollections.observableArrayList();
            Viewtify.observe(model.selectedItemProperty()).to(item -> {
                setSelectedItem(item);
                if (items.isEmpty()) {
                    items.add(item);
                } else {
                    items.set(0, item);
                }
            });
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ObservableList<Integer> getSelectedIndices() {
            return indices;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ObservableList<E> getSelectedItems() {
            return items;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void selectIndices(int index, int... indices) {
            model.select(index);
            for (int i : indices) {
                model.select(i);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void selectAll() {
            model.selectLast();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void selectFirst() {
            model.selectFirst();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void selectLast() {
            model.selectLast();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void clearAndSelect(int index) {
            model.clearAndSelect(index);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void select(int index) {
            model.select(index);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void select(E obj) {
            model.select(obj);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void clearSelection(int index) {
            model.clearSelection(index);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void clearSelection() {
            model.clearSelection();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isSelected(int index) {
            return model.isSelected(index);
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
            model.selectPrevious();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void selectNext() {
            model.selectNext();
        }
    }

    /**
     * For {@link IndexedCheckModel}.
     */
    static class IndexedCheckModelWrapper<E> extends MultipleSelectionModel<E> {

        /** The actual model. */
        private final IndexedCheckModel<E> model;

        /**
         * Delegation.
         * 
         * @param model
         */
        IndexedCheckModelWrapper(IndexedCheckModel<E> model) {
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
}