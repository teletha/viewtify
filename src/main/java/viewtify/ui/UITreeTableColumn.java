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
import java.util.function.Function;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeTableColumn;

import kiss.I;
import viewtify.View;

/**
 * @version 2017/11/15 9:54:15
 */
public class UITreeTableColumn<RowValue, ColumnValue> {

    /** The actual widget. */
    private final TreeTableColumn<RowValue, ColumnValue> ui;

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
     * Set value provider.
     * 
     * @param provider
     * @return
     */
    public <OneRowType> UITreeTableColumn<RowValue, ColumnValue> providerValue(Class<OneRowType> type, Function<OneRowType, ColumnValue> provider) {
        return provider(type, row -> new SimpleObjectProperty(provider.apply(row)));
    }

    /**
     * Set value provider.
     * 
     * @param provider
     * @return
     */
    public <OneRowType> UITreeTableColumn<RowValue, ColumnValue> provider(Class<OneRowType> type, Function<OneRowType, ObservableValue<ColumnValue>> provider) {
        if (mappingProvider == null) {
            provider(mappingProvider = new TypeMappingProvider());
        }
        mappingProvider.mapper.put(type, provider);

        return this;
    }

    /**
     * Set value provider.
     * 
     * @param provider
     * @return
     */
    public <P extends Function<RowValue, ObservableValue<ColumnValue>>> UITreeTableColumn<RowValue, ColumnValue> provider(Class<P> provider) {
        return provider(I.make(provider));
    }

    /**
     * Set value provider.
     * 
     * @param provider
     * @return
     */
    public UITreeTableColumn<RowValue, ColumnValue> provider(Function<RowValue, ObservableValue<ColumnValue>> provider) {
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
}
