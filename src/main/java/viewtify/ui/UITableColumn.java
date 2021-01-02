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
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

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
import kiss.WiseTriConsumer;
import kiss.WiseTriFunction;
import viewtify.Viewtify;
import viewtify.property.SmartProperty;
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

        ui.setCellValueFactory(v -> new SmartProperty(v.getValue()));
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
        return modelByProperty(type, row -> new SmartProperty(provider.apply(row)));
    }

    /**
     * Add value provider.
     * 
     * @param provider
     * @return
     */
    public UITableColumn<RowValue, ColumnValue> model(WiseFunction<RowValue, ColumnValue> provider) {
        return modelByProperty(row -> new SmartProperty(provider.apply(row)));
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
                public synchronized ObservableValue<ColumnValue> call(CellDataFeatures<RowValue, ColumnValue> cellData) {
                    return properties.computeIfAbsent(cellData.getValue(), provider::apply);
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
    public UITableColumn<RowValue, ColumnValue> modelBySignal(WiseFunction<RowValue, Signal<ColumnValue>> mapper) {
        if (mapper != null) {
            modelByProperty(v -> mapper.apply(v).to(new SmartProperty(), SmartProperty::set));
        }
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
                return new SmartProperty();
            } else {
                return map.apply(value);
            }
        }
    }

    /**
     * Render the human-readable item expression.
     * 
     * @param renderer A renderer.
     * @return
     */
    public UITableColumn<RowValue, ColumnValue> render(WiseTriConsumer<UILabel, RowValue, ColumnValue> renderer) {
        Objects.requireNonNull(renderer);
        return renderByUI(() -> new UILabel(null), (label, row, column) -> {
            renderer.accept(label, row, column);
            return label;
        });
    }

    /**
     * Render the human-readable item expression.
     * 
     * @param renderer A renderer.
     * @return
     */
    public <C> UITableColumn<RowValue, ColumnValue> renderByUI(Supplier<C> context, WiseTriFunction<C, RowValue, ColumnValue, ? extends UserInterfaceProvider<? extends Node>> renderer) {
        Objects.requireNonNull(renderer);
        return renderByNode(context, (ui, row, column) -> renderer.apply(ui, row, column).ui());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <C> UITableColumn<RowValue, ColumnValue> renderByNode(Supplier<C> context, BiFunction<C, ColumnValue, ? extends Node> renderer) {
        Objects.requireNonNull(renderer);
        return renderByNode(context, (ui, row, column) -> renderer.apply(ui, column));
    }

    /**
     * Render the human-readable item expression.
     * 
     * @param renderer A renderer.
     * @return
     */
    public <C> UITableColumn<RowValue, ColumnValue> renderByNode(Supplier<C> context, WiseTriFunction<C, RowValue, ColumnValue, ? extends Node> renderer) {
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
        private final WiseTriFunction<C, RowValue, ColumnValue, Node> renderer;

        /**
         * @param renderer
         */
        private GenericCell(Supplier<C> context, WiseTriFunction<C, RowValue, ColumnValue, Node> renderer) {
            this.context = context.get();
            this.renderer = renderer;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void updateItem(ColumnValue item, boolean empty) {
            super.updateItem(item, empty);

            RowValue row = getTableRow().getItem();

            if (item == null || row == null || empty) {
                setGraphic(null);
            } else {
                setGraphic(renderer.apply(context, row, item));
            }
        }
    }
}