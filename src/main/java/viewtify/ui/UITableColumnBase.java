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

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumnBase;

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
     * @param value
     * @return
     */
    public Self header(ObservableValue calculation) {
        ui.textProperty().bind(calculation);
        return (Self) this;
    }

    /**
     * Set column header text.
     * 
     * @param value
     * @return
     */
    public Self header(Object value) {
        ui.setText(String.valueOf(value));
        return (Self) this;
    }
}
