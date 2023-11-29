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
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

import viewtify.ui.helper.User;

public class UITableView<RowV> extends UITableBase<RowV, TableView<RowV>, UITableView<RowV>> {

    /** The specialized data format to handle the drag&drop gestures with table rows. */
    private final DataFormat DnD = new DataFormat("drag and drop manager for table " + this);

    /** The configuration for DnD. */
    private boolean dndable;

    /** The index of current dragging row. */
    private int draggingIndex;

    /** DnD starter */
    private final EventHandler<MouseEvent> dndDetector = event -> {
        if (!dndable) {
            return;
        }

        ObservableList<TablePosition> selectedCells = ui.getSelectionModel().getSelectedCells();
        if (selectedCells.size() == 1) {
            event.consume(); // stop event propagation

            EnhancedRow row = (EnhancedRow) event.getSource();

            ClipboardContent content = new ClipboardContent();
            content.put(DnD, DnD.toString());

            Dragboard board = row.startDragAndDrop(TransferMode.MOVE);
            board.setContent(content);

            draggingIndex = row.getIndex();
        }
    };

    /** DnD processor */
    private final EventHandler<DragEvent> dndOver = event -> {
        if (dndable && isValidDragboard(event)) {
            event.acceptTransferModes(TransferMode.MOVE);
            event.consume();

            EnhancedRow row = (EnhancedRow) event.getSource();

            int index = Math.min(row.getIndex(), ui.getItems().size() - 1);
            if (draggingIndex != index) {
                swap(draggingIndex, index);
                draggingIndex = index;
            }
        }
    };

    /** DnD finisher */
    private final EventHandler<DragEvent> dndDropper = event -> {
        if (dndable && isValidDragboard(event)) {
            event.setDropCompleted(true);
            event.consume();
        }
    };

    /**
     * Validates the dragboard content.
     *
     * @param event The drag drop event.
     * @return True if the dragboard of the event is valid.
     */
    private boolean isValidDragboard(DragEvent event) {
        Dragboard board = event.getDragboard();
        return board.hasContent(DnD) && board.getContent(DnD).equals(DnD.toString());
    }

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

    /**
     * Configure draggable row.
     * 
     * @param enable
     * @return
     */
    public UITableView<RowV> dndable(boolean enable) {
        this.dndable = enable;
        return this;
    }

    /**
     * Configure the height of cell.
     * 
     * @return
     */
    public UITableView<RowV> fixRowHeight(double height) {
        ui.setFixedCellSize(height);
        return this;
    }

    /**
     * 
     */
    private class EnhancedRow extends TableRow {

        private EnhancedRow() {
            addEventHandler(MouseEvent.DRAG_DETECTED, dndDetector);
            addEventHandler(DragEvent.DRAG_OVER, dndOver);
            addEventHandler(DragEvent.DRAG_DROPPED, dndDropper);
        }
    }
}