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

import java.util.function.Function;

import javafx.beans.property.Property;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

import kiss.I;
import viewtify.ui.helper.User;

public class UITableView<RowV> extends UITableBase<RowV, TableView<RowV>, UITableView<RowV>> {

    /** The configuration. */
    private boolean dndable;

    /**
     * Enchanced view.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    public UITableView(View view) {
        super(new TableView(), view);

        placeholder("");
        when(User.Sort, () -> sort(ui.getComparator()));

        // Use user data properties to pass UI instances to TableColumn.
        ui.getProperties().put(UITableView.class, this);

        ui.setRowFactory(table -> new EnhancedRow());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<ObservableList<RowV>> itemsProperty() {
        return ui.itemsProperty();
    }

    /**
     * Configure item renderer.
     */
    public UITableView<RowV> render(Function<UITableView<RowV>, TableRow<RowV>> renderer) {
        ui.setRowFactory(table -> renderer.apply(this));
        return this;
    }

    public UITableView<RowV> operatable(boolean enable) {
        for (TableColumn column : ui.getColumns()) {
            column.setSortable(enable);
            column.setResizable(enable);
            column.setReorderable(enable);
        }

        ui.getColumns().addListener((ListChangeListener<TableColumn<RowV, ?>>) change -> {
            while (change.next()) {
                for (TableColumn column : change.getAddedSubList()) {
                    column.setSortable(enable);
                    column.setResizable(enable);
                    column.setReorderable(enable);
                }
            }
        });
        return this;
    }

    /**
     * Remove all table decoration styles and behaviors.
     * 
     * @return
     */
    public UITableView<RowV> simplify() {
        ui.getStyleClass().add("simple");
        ui.setSelectionModel(null);
        ui.setFocusTraversable(false);
        return operatable(false);
    }

    public UITableView<RowV> dndable(boolean enable) {
        this.dndable = enable;
        return this;
    }

    /**
     * 
     */
    private class EnhancedRow extends TableRow {

        /**
         * 
         */
        private EnhancedRow() {
            addEventHandler(MouseEvent.DRAG_DETECTED, dndDetector);
            addEventHandler(DragEvent.DRAG_OVER, dndOver);
            addEventHandler(DragEvent.DRAG_DROPPED, dndDropper);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);
        }

        private final EventHandler<MouseEvent> dndDetector = event -> {
            if (!dndable) {
                return;
            }

            ObservableList<TablePosition> selectedCells = ui.getSelectionModel().getSelectedCells();
            if (!selectedCells.isEmpty()) {
                Dragboard dragboard = ui.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(String.valueOf(selectedCells.get(0).getRow()));
                dragboard.setContent(content);
                event.consume();
                System.out.println("OK");
            }
        };

        private final EventHandler<DragEvent> dndOver = event -> {
            if (!dndable) {
                return;
            }

            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
                event.consume();

                System.out.println(detectPosition());
            }
        };

        private final EventHandler<DragEvent> dndDropper = event -> {
            if (!dndable) {
                return;
            }

            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasString()) {

                try {
                    int draggedIndex = Integer.parseInt(dragboard.getString());
                    int dropIndex = 3;
                    swap(draggedIndex, dropIndex);
                    event.setDropCompleted(true);
                    ui.getSelectionModel().clearSelection();
                    event.consume();

                    System.out.println("from " + draggedIndex + " to " + dropIndex);
                } catch (Throwable e) {
                    e.printStackTrace();
                    throw I.quiet(e);
                }
            }
        };

        private int detectPosition() {
            return Math.min(getIndex(), ui.getItems().size() - 1);
        }
    }
}