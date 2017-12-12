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

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;

import kiss.I;
import kiss.Variable;
import kiss.WiseFunction;
import viewtify.View;
import viewtify.Viewtify;

/**
 * @version 2017/11/15 9:54:15
 */
public class UITreeTableColumn<RowValue, ColumnValue> {

    /** The actual widget. */
    private final TreeTableColumn<RowValue, ColumnValue> ui;

    /** The value provider utility. */
    private TypeMappingProvider mappingProvider;

    /**
     * Enchanced view.
     * 
     * @param ui
     */
    private UITreeTableColumn(TreeTableColumn<RowValue, ColumnValue> ui, View view) {
        this.ui = ui;
    }

    /**
     * Add value provider.
     * 
     * @param provider
     * @return
     */
    public <Type extends RowValue> UITreeTableColumn<RowValue, ColumnValue> provideVariable(Class<Type> type, WiseFunction<Type, Variable<ColumnValue>> provider) {
        return provideProperty(type, row -> Viewtify.calculate(provider.apply(row)));
    }

    /**
     * Add value provider.
     * 
     * @param provider
     * @return
     */
    public <Type extends RowValue> UITreeTableColumn<RowValue, ColumnValue> provideValue(Class<Type> type, WiseFunction<Type, ColumnValue> provider) {
        return provideProperty(type, row -> new SimpleObjectProperty(provider.apply(row)));
    }

    /**
     * Add value provider.
     * 
     * @param provider
     * @return
     */
    public <Type extends RowValue> UITreeTableColumn<RowValue, ColumnValue> provideProperty(Class<Type> type, Function<Type, ObservableValue<ColumnValue>> provider) {
        if (mappingProvider == null) {
            provide(mappingProvider = new TypeMappingProvider());
        }
        mappingProvider.mapper.put(type, provider);

        return this;
    }

    /**
     * Add value provider.
     * 
     * @param provider
     * @return
     */
    public <P extends Function<RowValue, ObservableValue<ColumnValue>>> UITreeTableColumn<RowValue, ColumnValue> provide(Class<P> provider) {
        return provide(I.make(provider));
    }

    /**
     * Add value provider.
     * 
     * @param provider
     * @return
     */
    public UITreeTableColumn<RowValue, ColumnValue> provide(Function<RowValue, ObservableValue<ColumnValue>> provider) {
        ui.setCellValueFactory(data -> provider.apply(data.getValue().getValue()));
        return this;
    }

    /**
     * @version 2017/12/02 16:23:03
     */
    private class TypeMappingProvider<T> implements Function<T, ObservableValue<ColumnValue>> {

        private final Map<Class<T>, Function<T, ObservableValue<ColumnValue>>> mapper = new HashMap();

        /**
         * {@inheritDoc}
         */
        @Override
        public ObservableValue<ColumnValue> apply(T value) {
            return mapper.get(value.getClass()).apply(value);
        }
    }

    /**
     * Set cell renderer.
     * 
     * @param renderer
     * @return
     */
    public UITreeTableColumn<RowValue, ColumnValue> render(Function<TreeTableColumn<RowValue, ColumnValue>, TreeTableCell<RowValue, ColumnValue>> renderer) {
        ui.setCellFactory(table -> renderer.apply(table));
        return this;
    }

    /**
     * Set cell renderer.
     * 
     * @param renderer
     * @return
     */
    public UITreeTableColumn<RowValue, ColumnValue> render(BiConsumer<UITreeTableCell, ColumnValue> renderer) {
        ui.setCellFactory(table -> new UITreeTableCell(renderer).ui);
        return this;
    }

    /**
     * @version 2017/12/02 18:11:59
     */
    public class UITreeTableCell implements UILabeled<UITreeTableCell, TreeTableCell<RowValue, ColumnValue>> {

        /** The user renderer. */
        private final BiConsumer<UITreeTableCell, ColumnValue> renderer;

        /** The actual widget. */
        private final TreeTableCell<RowValue, ColumnValue> ui = new TreeTableCell<RowValue, ColumnValue>() {

            /**
             * {@inheritDoc}
             */
            @Override
            protected void updateItem(ColumnValue item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    renderer.accept(UITreeTableCell.this, item);
                }
            }
        };

        /**
         * @param renderer
         */
        private UITreeTableCell(BiConsumer<UITreeTableCell, ColumnValue> renderer) {
            this.renderer = renderer;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TreeTableCell ui() {
            return ui;
        }
    }
}
