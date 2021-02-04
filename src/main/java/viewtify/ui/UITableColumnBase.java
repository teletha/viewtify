/*
 * Copyright (C) 2021 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import org.controlsfx.control.PopOver.ArrowLocation;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.stage.PopupWindow.AnchorLocation;
import viewtify.ui.filter.GenricFilterView;
import viewtify.ui.helper.CollectableHelper;
import viewtify.ui.helper.LabelHelper;
import viewtify.ui.helper.StyleHelper;
import viewtify.ui.helper.TooltipHelper;

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
     * Enable the enhanced filtering user-interface.
     * 
     * @param enable
     * @return
     */
    public final Self filterable(boolean enable) {
        Node graphic = ui.getGraphic();

        if (enable) {
            if (graphic == null) {
                Button button = new Button();
                button.setFocusTraversable(false);
                button.setOnAction(e -> {
                    TooltipHelper.popover(ui.getStyleableNode(), p -> {
                        GenricFilterView<RowValue> view = new GenricFilterView();
                        view.register("Market Name", String.class, String::valueOf);

                        p.setDetachable(false);
                        p.setAnchorLocation(AnchorLocation.CONTENT_TOP_LEFT);
                        p.setArrowLocation(ArrowLocation.BOTTOM_CENTER);
                        p.setContentNode(view.ui());
                    });
                });

                ui.setGraphic(button);
            }
        } else {
            if (graphic != null) {
                ui.setGraphic(null);
            }
        }

        return (Self) this;
    }

    /**
     * A boolean property to toggle on and off the 'sortability' of this column. When this property
     * is true, this column can be included in sort operations. If this property is false, it will
     * not be included in sort operations, even if it is contained within the sort order list of the
     * underlying UI control (e.g. {@link TableView#getSortOrder()} or
     * {@link TreeTableView#getSortOrder()}).
     * <p>
     * For example, iIf a TableColumn instance is contained within the TableView sortOrder
     * ObservableList, and its sortable property toggles state, it will force the TableView to
     * perform a sort, as it is likely the view will need updating.
     * 
     * @param enable
     * @return
     */
    public final Self sortable(boolean enable) {
        ui.setSortable(enable);
        return (Self) this;
    }

    /**
     * Used to indicate whether the width of this column can change. It is up to the resizing policy
     * to enforce this however.
     * 
     * @param enable
     * @return
     */
    public final Self resizable(boolean enable) {
        ui.setResizable(enable);
        return (Self) this;
    }

    /**
     * A boolean property to toggle on and off the 'reorderability' of this column (with drag and
     * drop - reordering by modifying the appropriate <code>columns</code> list is always allowed).
     * When this property is true, this column can be reordered by users simply by dragging and
     * dropping the columns into their desired positions. When this property is false, this ability
     * to drag and drop columns is not available.
     *
     * @param enable
     * @return
     */
    public final Self reorderable(boolean enable) {
        ui.setReorderable(enable);
        return (Self) this;
    }

    /**
     * @param enable
     * @return
     */
    public final Self operatable(boolean enable) {
        filterable(enable);
        sortable(enable);
        resizable(enable);
        reorderable(enable);
        return (Self) this;
    }

    /**
     * 
     */
    private class FilterView extends View {

        UILabel desc;

        UIText<String> input;

        @SuppressWarnings("unused")
        class View extends ViewDSL {
            {
                $(vbox, () -> {
                    $(desc);
                    $(input);
                });
            }
        }

        private final CollectableHelper<?, RowValue> items;

        private final Class type = String.class;

        /**
         * 
         */
        private FilterView() {
            if (ui instanceof TableColumn) {
                items = (CollectableHelper) ((TableColumn) ui).getTableView().getUserData();
            } else if (ui instanceof TreeTableColumn) {
                items = (CollectableHelper) ((TreeTableColumn) ui).getTreeTableView().getUserData();
            } else {
                throw new Error("Unkwno table type");
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void initialize() {
            desc.text("Filter by " + text());
            input.placeholder("Filter by text").observe().to(value -> {
                items.take(v -> {
                    return true;
                });
            });
        }
    }

}