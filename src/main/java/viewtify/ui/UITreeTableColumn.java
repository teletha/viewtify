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

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import kiss.I;
import kiss.Variable;
import kiss.WiseFunction;
import viewtify.Viewtify;
import viewtify.property.SmartProperty;
import viewtify.ui.helper.CollectableItemRenderingHelper;

public class UITreeTableColumn<RowV, ColumnV>
        extends UITableColumnBase<TreeTableColumn<RowV, ColumnV>, UITreeTableColumn<RowV, ColumnV>, RowV, ColumnV, UITreeTableView<RowV>>
        implements CollectableItemRenderingHelper<UITreeTableColumn<RowV, ColumnV>, ColumnV> {

    /** The value provider utility. */
    private TypeMappingProvider mappingProvider;

    /**
     * Enchanced view.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    public UITreeTableColumn(View view, Class<RowV> rowType, Class<ColumnV> columnType) {
        super(new TreeTableColumn(), rowType, columnType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final UITreeTableView table() {
        return (UITreeTableView) ui.getTreeTableView().getProperties().get(UITreeTableView.class);
    }

    /**
     * Add value provider.
     * 
     * @param provider
     * @return
     */
    public <P extends Function<RowV, ObservableValue<ColumnV>>> UITreeTableColumn<RowV, ColumnV> model(Class<P> provider) {
        return modelByProperty(I.make(provider));
    }

    /**
     * Add value provider.
     * 
     * @param provider
     * @return
     */
    public <T extends RowV> UITreeTableColumn<RowV, ColumnV> model(Class<T> type, WiseFunction<T, ColumnV> provider) {
        return modelByProperty(type, row -> new SmartProperty(provider.apply(row)));
    }

    /**
     * Add value provider.
     * 
     * @param provider
     * @return
     */
    public UITreeTableColumn<RowV, ColumnV> model(WiseFunction<RowV, ColumnV> provider) {
        return modelByProperty(row -> new SmartProperty(provider.apply(row)));
    }

    /**
     * Add value provider.
     * 
     * @param provider
     * @return
     */
    public UITreeTableColumn<RowV, ColumnV> modelByProperty(Function<RowV, ObservableValue<ColumnV>> provider) {
        ui.setCellValueFactory(data -> provider.apply(data.getValue().getValue()));
        return this;
    }

    /**
     * Add value provider.
     * 
     * @param provider
     * @return
     */
    public <T extends RowV> UITreeTableColumn<RowV, ColumnV> modelByProperty(Class<T> type, Function<T, ObservableValue<ColumnV>> provider) {
        if (mappingProvider == null) {
            modelByProperty(mappingProvider = new TypeMappingProvider());
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
    public UITreeTableColumn<RowV, ColumnV> modelByVar(WiseFunction<RowV, Variable<ColumnV>> provider) {
        return modelByProperty(row -> Viewtify.property(provider.apply(row)));
    }

    /**
     * Add value provider.
     * 
     * @param provider
     * @return
     */
    public <T extends RowV> UITreeTableColumn<RowV, ColumnV> modelByVar(Class<T> type, WiseFunction<T, Variable<ColumnV>> provider) {
        return modelByProperty(type, row -> Viewtify.property(provider.apply(row)));
    }

    /**
     * @version 2017/12/02 16:23:03
     */
    private class TypeMappingProvider<T> implements Function<T, ObservableValue<ColumnV>> {

        private final Map<Class<T>, Function<T, ObservableValue<ColumnV>>> mapper = new HashMap();

        /**
         * {@inheritDoc}
         */
        @Override
        public ObservableValue<ColumnV> apply(T value) {
            Class type = value.getClass();
            Function<T, ObservableValue<ColumnV>> map = mapper.get(type);

            while (map == null && type != Object.class) {
                type = type.getSuperclass();
                map = mapper.get(type);
            }

            if (map == null) {
                return new SmartProperty();
            } else {
                return map.apply(value);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <C> UITreeTableColumn<RowV, ColumnV> renderByNode(Supplier<C> context, BiFunction<C, ColumnV, ? extends Node> renderer) {
        ui.setCellFactory(table -> new GenericCell(context, renderer));
        return this;
    }

    /**
     * 
     */
    private static class GenericCell<RowValue, ColumnValue, C> extends TreeTableCell<RowValue, ColumnValue> {

        /** The context. */
        private final C context;

        /** The user defined cell renderer. */
        private final BiFunction<C, ColumnValue, Node> renderer;

        /**
         * @param renderer
         */
        private GenericCell(Supplier<C> context, BiFunction<C, ColumnValue, Node> renderer) {
            this.context = context.get();
            this.renderer = renderer;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void updateItem(ColumnValue item, boolean empty) {
            super.updateItem(item, empty);

            if (item == null || empty) {
                setGraphic(null);
            } else {
                setGraphic(renderer.apply(context, item));
            }
        }
    }
}