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

import javafx.beans.binding.Bindings;
import javafx.scene.control.TableColumnBase;

import viewtify.Viewtify;

/**
 * @version 2018/02/05 20:43:01
 */
public abstract class UITableColumnBase<Column extends TableColumnBase, Self extends UITableColumnBase, RowValue, ColumnValue> {

    /** The actual widget. */
    protected final Column ui;

    /**
     * @param ui
     */
    protected UITableColumnBase(Column ui) {
        this.ui = ui;
    }

    /**
     * Set column header text.
     * 
     * @param texts
     * @return
     */
    public Self header(Object... texts) {
        ui.textProperty().bind(Viewtify.inUI(Bindings.concat(Viewtify.observe(texts))));
        return (Self) this;
    }
}
