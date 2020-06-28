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

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

import kiss.I;
import kiss.Signal;
import kiss.Variable;
import kiss.WiseFunction;
import viewtify.Viewtify;
import viewtify.ui.helper.CollectableItemRenderingHelper;

public class UITableColumn<RowValue, ColumnValue>
        extends UITableColumnBase<TableColumn<RowValue, ColumnValue>, UITableColumn<RowValue, ColumnValue>, RowValue, ColumnValue>
        implements CollectableItemRenderingHelper<UITableColumn<RowValue, ColumnValue>, ColumnValue> {

    /** The value provider utility. */
    private TypeMappingProvider mappingProvider;

    /**
     * Enchanced view.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    public UITableColumn(View view) {
        super(new TableColumn());

        ui.setCellValueFactory(v -> new SimpleObjectProperty(v.getValue()));
    }

    public UITableColumn<RowValue, ColumnValue> modelBySignal(WiseFunction<RowValue, Signal<ColumnValue>> mapper) {
        if (mapper != null) {
            modelByProperty(v -> mapper.apply(v).to(new SimpleObjectProperty(), SimpleObjectProperty::set));
        }
        return this;
    }

    /**
     * Add value provider.
     * 
     * @param provider
     * @return
     */
    public <P extends Function<RowValue, ObservableValue<ColumnValue>>> UITableColumn<RowValue, ColumnValue> model(Class<P> provider) {
        return modelByProperty(I.make(provider));
    }

    /**
     * Add value provider.
     * 
     * @param provider
     * @return
     */
    public <T extends RowValue> UITableColumn<RowValue, ColumnValue> model(Class<T> type, WiseFunction<T, ColumnValue> provider) {
        return modelByProperty(type, row -> new SimpleObjectProperty(provider.apply(row)));
    }

    /**
     * Add value provider.
     * 
     * @param provider
     * @return
     */
    public UITableColumn<RowValue, ColumnValue> model(WiseFunction<RowValue, ColumnValue> provider) {
        return modelByProperty(row -> new SimpleObjectProperty(provider.apply(row)));
    }

    /**
     * Add value provider.
     * 
     * @param provider
     * @return
     */
    public UITableColumn<RowValue, ColumnValue> modelByProperty(Function<RowValue, ObservableValue<ColumnValue>> provider) {
        if (provider != null) {
            ui.setCellValueFactory(new Callback<>() {

                private final WeakHashMap<RowValue, ObservableValue<ColumnValue>> properties = new WeakHashMap();

                @Override
                public ObservableValue<ColumnValue> call(CellDataFeatures<RowValue, ColumnValue> param) {
                    return properties.computeIfAbsent(param.getValue(), provider::apply);
                }
            });
        }
        return this;
    }

    /**
     * Add value provider.
     * 
     * @param provider
     * @return
     */
    public <T extends RowValue> UITableColumn<RowValue, ColumnValue> modelByProperty(Class<T> type, Function<T, ObservableValue<ColumnValue>> provider) {
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
    public UITableColumn<RowValue, ColumnValue> modelByVar(WiseFunction<RowValue, Variable<ColumnValue>> provider) {
        return modelByProperty(row -> Viewtify.property(provider.apply(row)));
    }

    /**
     * Add value provider.
     * 
     * @param provider
     * @return
     */
    public <T extends RowValue> UITableColumn<RowValue, ColumnValue> modelByVar(Class<T> type, WiseFunction<T, Variable<ColumnValue>> provider) {
        return modelByProperty(type, row -> Viewtify.property(provider.apply(row)));
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
            Class type = value.getClass();
            Function<T, ObservableValue<ColumnValue>> map = mapper.get(type);

            while (map == null && type != Object.class) {
                type = type.getSuperclass();
                map = mapper.get(type);
            }

            if (map == null) {
                return new SimpleObjectProperty();
            } else {
                return map.apply(value);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <C> UITableColumn<RowValue, ColumnValue> renderByNode(Supplier<C> context, BiFunction<C, ColumnValue, ? extends Node> renderer) {
        ui.setCellFactory(table -> new GenericCell(context, renderer));
        return this;
    }

    /**
     * 
     */
    private static class GenericCell<RowValue, ColumnValue, C> extends TableCell<RowValue, ColumnValue> {

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
