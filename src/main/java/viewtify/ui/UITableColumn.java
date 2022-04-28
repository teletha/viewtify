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
import java.util.function.Function;
import java.util.function.Supplier;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

import kiss.Disposable;
import kiss.I;
import kiss.Signal;
import kiss.Variable;
import kiss.WiseFunction;
import kiss.WiseTriConsumer;
import kiss.WiseTriFunction;
import kiss.Ⅱ;
import viewtify.Viewtify;
import viewtify.property.SmartProperty;
import viewtify.ui.helper.CollectableItemRenderingHelper;

public class UITableColumn<RowV, ColumnV>
        extends UITableColumnBase<TableColumn<RowV, ColumnV>, UITableColumn<RowV, ColumnV>, RowV, ColumnV, UITableView<RowV>>
        implements CollectableItemRenderingHelper<UITableColumn<RowV, ColumnV>, Ⅱ<RowV, ColumnV>> {

    /** The value provider utility. */
    private TypeMappingProvider mappingProvider;

    /**
     * Enchanced view.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    public UITableColumn(View view, Class<RowV> rowType, Class<ColumnV> columnType) {
        super(new TableColumn(), rowType, columnType);

        ui.setCellValueFactory(v -> new SmartProperty(v.getValue()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final UITableView table() {
        return (UITableView) ui.getTableView().getProperties().get(UITableView.class);
    }

    /**
     * Add value provider.
     * 
     * @param provider
     * @return
     */
    public <P extends Function<RowV, ObservableValue<ColumnV>>> UITableColumn<RowV, ColumnV> model(Class<P> provider) {
        return modelByProperty(I.make(provider));
    }

    /**
     * Add value provider.
     * 
     * @param provider
     * @return
     */
    public <T extends RowV> UITableColumn<RowV, ColumnV> model(Class<T> type, WiseFunction<T, ColumnV> provider) {
        return modelByProperty(type, row -> new SmartProperty(provider.apply(row)));
    }

    /**
     * Add value provider.
     * 
     * @param provider
     * @return
     */
    public UITableColumn<RowV, ColumnV> model(WiseFunction<RowV, ColumnV> provider) {
        return modelByProperty(row -> new SmartProperty(provider.apply(row)));
    }

    /**
     * Add value provider.
     * 
     * @param provider
     * @return
     */
    public UITableColumn<RowV, ColumnV> modelByProperty(Function<RowV, ObservableValue<ColumnV>> provider) {
        if (provider != null) {
            ui.setCellValueFactory(new Callback<>() {

                private final WeakHashMap<RowV, ObservableValue<ColumnV>> properties = new WeakHashMap();

                @Override
                public synchronized ObservableValue<ColumnV> call(CellDataFeatures<RowV, ColumnV> cellData) {
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
    public <T extends RowV> UITableColumn<RowV, ColumnV> modelByProperty(Class<T> type, Function<T, ObservableValue<ColumnV>> provider) {
        if (mappingProvider == null) {
            modelByProperty(mappingProvider = new TypeMappingProvider());
        }
        mappingProvider.mapper.put(type, provider);

        return this;
    }

    /**
     * Add value provider.
     * 
     * @param mapper
     * @return
     */
    public UITableColumn<RowV, ColumnV> modelBySignal(WiseFunction<RowV, Signal<ColumnV>> mapper) {
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
    public UITableColumn<RowV, ColumnV> modelByVar(WiseFunction<RowV, Variable<ColumnV>> provider) {
        return modelByProperty(row -> Viewtify.property(provider.apply(row)));
    }

    /**
     * Add value provider.
     * 
     * @param provider
     * @return
     */
    public <T extends RowV> UITableColumn<RowV, ColumnV> modelByVar(Class<T> type, WiseFunction<T, Variable<ColumnV>> provider) {
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
     * Render the human-readable item expression.
     * 
     * @param renderer A renderer.
     * @return
     */
    public UITableColumn<RowV, ColumnV> render(WiseTriConsumer<UILabel, RowV, ColumnV> renderer) {
        Objects.requireNonNull(renderer);
        return renderByUI(() -> new UILabel(null), (label, values, disposer) -> {
            renderer.accept(label, values.ⅰ, values.ⅱ);
            return label;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <C> UITableColumn<RowV, ColumnV> renderByNode(Supplier<C> context, WiseTriFunction<C, Ⅱ<RowV, ColumnV>, Disposable, ? extends Node> renderer) {
        Objects.requireNonNull(renderer);
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
        private final WiseTriFunction<C, Ⅱ<RowValue, ColumnValue>, Disposable, Node> renderer;

        /** The cell disposer. */
        private Disposable disposer = Disposable.empty();

        /**
         * @param renderer
         */
        private GenericCell(Supplier<C> context, WiseTriFunction<C, Ⅱ<RowValue, ColumnValue>, Disposable, Node> renderer) {
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

                disposer.dispose();
                disposer = Disposable.empty();
            } else {
                setGraphic(renderer.apply(context, I.pair(row, item), disposer));
            }
        }
    }

}