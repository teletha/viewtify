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

import javafx.scene.control.Control;

import viewtify.ui.helper.CollectableHelper;
import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.PlaceholderHelper;
import viewtify.ui.helper.SelectableHelper;
import viewtify.ui.query.CompoundQuery;

abstract class UITableBase<RowV, FXTable extends Control, Self extends UITableBase<RowV, FXTable, Self>>
        extends UserInterface<Self, FXTable>
        implements SelectableHelper<Self, RowV>, CollectableHelper<Self, RowV>, PlaceholderHelper<Self>, ContextMenuHelper<Self> {

    /** The associated query. */
    protected CompoundQuery<RowV> compound;

    /**
     * Build table UI.
     * 
     * @param ui
     * @param view
     */
    protected UITableBase(FXTable ui, View view) {
        super(ui, view);
    }
}