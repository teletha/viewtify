/*
 * Copyright (C) 2018 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.scene.Node;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableSelectionModel;
import javafx.scene.control.TableView;

import viewtify.Viewtify;

/**
 * @version 2018/09/10 8:39:01
 */
public class UITableView<T> extends AbstractTableView<UITableView<T>, TableView<T>, T> {

    /** The items (concurrent safe, snapshot iteration). */
    public final ObservableList<T> values;

    /**
     * Enchanced view.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    private UITableView(View view) {
        super(new Internal(), view, ui -> Viewtify.calculate(ui.getSelectionModel().getSelectedItems()));

        values = Viewtify.observe(new CopyOnWriteArrayList<>(ui.getItems()));
        ui.setItems(values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ObjectProperty<Node> placeholder() {
        return ui.placeholderProperty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TableSelectionModel selectionModel() {
        return ui.getSelectionModel();
    }

    /**
     * Specifies ROW renderer.
     */
    public UITableView<T> render(Function<UITableView<T>, TableRow<T>> renderer) {
        ui.setRowFactory(table -> renderer.apply(this));

        return this;
    }

    /**
     * @version 2018/09/09 23:26:36
     */
    private static class Internal<V> extends TableView<V> {

        /**
         * {@inheritDoc}
         */
        @Override
        public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
            return ExtraCSS.metadata(super.getControlCssMetaData());
        }
    }
}
