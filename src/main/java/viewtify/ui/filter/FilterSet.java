/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FilterSet<M> implements Predicate<M> {

    /** The filterable point set. */
    final List<Pointer> pointers = new ArrayList();

    /** The user defined query set. */
    private final ObservableList<Query> queries = FXCollections.observableArrayList();

    /**
     * Register the filterable value.
     * 
     * @param <T>
     * @param name The target's name.
     * @param type The value type.
     * @param extractor The value extractor from the model.
     * @return
     */
    public <T> FilterSet<M> register(StringProperty name, Class<T> type, Function<M, T> valueExtractor) {
        pointers.add(new Pointer(name, type, valueExtractor));
        return this;
    }

    public Query addEmptyQuery() {
        Query query = new Query();
        query.pointer.setValue(pointers.get(0));

        queries.add(query);
        return query;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean test(M model) {
        for (Query query : queries) {
            if (!query.test(model)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 
     */
    static class Pointer<M, T> implements Function<M, T> {

        final StringProperty name;

        final Class<T> type;

        final Function<M, T> extractor;

        /**
         * @param name
         * @param type
         * @param extractor
         */
        private Pointer(StringProperty name, Class<T> type, Function<M, T> extractor) {
            this.name = name;
            this.type = type;
            this.extractor = extractor;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public T apply(M model) {
            return extractor.apply(model);
        }
    }

    static class Query<M, T> implements Predicate<M> {

        final Property<Pointer> pointer = new SimpleObjectProperty();

        StringProperty tester;

        final Property<BuiltinMatchers> filter = new SimpleObjectProperty();

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean test(M model) {
            String t = tester.get();
            if (t == null || t.isBlank()) {
                return true;
            }
            return filter.getValue().test(pointer.getValue().apply(model), t);
        }
    }
}
