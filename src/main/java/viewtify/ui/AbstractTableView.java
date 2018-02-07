/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui;

import javafx.scene.control.Control;

import viewtify.View;
import viewtify.bind.CalculationList;

/**
 * @version 2018/02/07 16:09:43
 */
public abstract class AbstractTableView<Self extends AbstractTableView, W extends Control, T> extends UIControl<Self, W> {

    /** The selection model. */
    private final CalculationList<T> selection;

    /**
     * @param ui
     * @param view
     */
    protected AbstractTableView(W ui, View view, CalculationList<T> selection) {
        super(ui, view);

        this.selection = selection;
    }

    /**
     * Get all selected values.
     * 
     * @return
     */
    public final CalculationList<T> selection() {
        return selection;
    }
}
