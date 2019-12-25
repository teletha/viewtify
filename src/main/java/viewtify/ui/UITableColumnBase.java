/*
 * Copyright (C) 2019 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;
import viewtify.ui.helper.LabelHelper;
import viewtify.ui.helper.StyleHelper;

public abstract class UITableColumnBase<Column extends TableColumnBase, Self extends UITableColumnBase, RowValue, ColumnValue>
        implements UserInterfaceProvider<Column>, LabelHelper<Self>, StyleHelper<Self, Column> {

    /** The actual widget. */
    public final Column ui;

    /**
     * @param ui
     */
    protected UITableColumnBase(Column ui) {
        this.ui = ui;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Column ui() {
        return ui;
    }

    /**
     * <p>
     * A boolean property to toggle on and off the 'sortability' of this column. When this property
     * is true, this column can be included in sort operations. If this property is false, it will
     * not be included in sort operations, even if it is contained within the sort order list of the
     * underlying UI control (e.g. {@link TableView#getSortOrder()} or
     * {@link TreeTableView#getSortOrder()}).
     * </p>
     * <p>
     * For example, iIf a TableColumn instance is contained within the TableView sortOrder
     * ObservableList, and its sortable property toggles state, it will force the TableView to
     * perform a sort, as it is likely the view will need updating.
     * </p>
     * 
     * @param enable
     * @return
     */
    public Self sortable(boolean enable) {
        ui.setSortable(enable);
        return (Self) this;
    }
}
