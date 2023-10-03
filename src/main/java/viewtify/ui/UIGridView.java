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

import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import viewtify.ui.helper.AlignmentHelper;

public class UIGridView<E extends UserInterfaceProvider<? extends Node>> extends AbstractPane<E, GridPane, UIGridView<E>>
        implements AlignmentHelper<UIGridView<E>> {

    /**
     * @param view
     */
    public UIGridView(View view) {
        super(new GridPane(), view);
    }

    /**
     * Add constraint for column.
     * 
     * @param grow
     * @return
     */
    public UIGridView<E> constrainColumn(Priority grow) {
        return constrainColumn(grow, HPos.CENTER);
    }

    /**
     * Add constraint for column.
     * 
     * @param grow
     * @param alignment
     * @return
     */
    public UIGridView<E> constrainColumn(Priority grow, HPos alignment) {
        return constrainColumn(grow, alignment, true);
    }

    /**
     * Add constraint for column.
     * 
     * @param grow
     * @param alignment
     * @param fill
     * @return
     */
    public UIGridView<E> constrainColumn(Priority grow, HPos alignment, boolean fill) {
        ColumnConstraints column = new ColumnConstraints();
        column.setHgrow(grow);
        column.setHalignment(alignment);
        column.setFillWidth(fill);

        return constrain(column);
    }

    /**
     * Add constraint for column.
     * 
     * @param constraint
     * @return
     */
    public UIGridView<E> constrain(ColumnConstraints constraint) {
        return constrain(constraint, 1);
    }

    /**
     * Add constraint for column.
     * 
     * @param constraint
     * @return
     */
    public UIGridView<E> constrain(ColumnConstraints constraint, int sequence) {
        if (constraint != null) {
            ObservableList<ColumnConstraints> list = ui.getColumnConstraints();
            for (int i = 0; i < sequence; i++) {
                list.add(constraint);
            }
        }
        return this;
    }

    /**
     * Add constraint for row.
     * 
     * @param grow
     * @return
     */
    public UIGridView<E> constrainRow(Priority grow) {
        return constrainRow(grow, VPos.CENTER);
    }

    /**
     * Add constraint for row.
     * 
     * @param grow
     * @param alignment
     * @return
     */
    public UIGridView<E> constrainRow(Priority grow, VPos alignment) {
        return constrainRow(grow, alignment, true);
    }

    /**
     * Add constraint for row.
     * 
     * @param grow
     * @param alignment
     * @param fill
     * @return
     */
    public UIGridView<E> constrainRow(Priority grow, VPos alignment, boolean fill) {
        RowConstraints row = new RowConstraints();
        row.setVgrow(grow);
        row.setValignment(alignment);
        row.setFillHeight(fill);

        return constrain(row);
    }

    /**
     * Add constraint for row.
     * 
     * @param constraint
     * @return
     */
    public UIGridView<E> constrain(RowConstraints constraint) {
        return constrain(constraint, 1);
    }

    /**
     * Add constraint for row.
     * 
     * @param constraint
     * @return
     */
    public UIGridView<E> constrain(RowConstraints constraint, int sequence) {
        if (constraint != null) {
            ObservableList<RowConstraints> list = ui.getRowConstraints();
            for (int i = 0; i < sequence; i++) {
                list.add(constraint);
            }
        }
        return this;
    }
}