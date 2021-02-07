/*
 * Copyright (C) 2021 Nameless Production Committee
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
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;

import kiss.Variable;

public class CompoundQuery<M> implements Predicate<M> {

    public final Variable<Integer> size = Variable.of(0);

    /** The managed queries. */
    final List<Query> queries = new ArrayList();

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
     * Add String based {@link Extractor}.
     * 
     * @param <V>
     * @param description The human-readable description.
     */
    public void addExtractor(String description) {
        addExtractor(new SimpleStringProperty(description));
    }

    /**
     * Add String based {@link Extractor}.
     * 
     * @param <V>
     * @param description The human-readable description.
     */
    public void addExtractor(Property<String> description) {
        addExtractor(description, String.class, String::valueOf);
    }

    /**
     * Add {@link Extractor}.
     * 
     * @param <V>
     * @param description The human-readable description.
     * @param type A value type.
     * @param extractor An actual value {@link Extractor}.
     */
    public <V> void addExtractor(String description, Class<V> type, Function<M, V> extractor) {
        addExtractor(new SimpleStringProperty(description), type, extractor);
    }

    /**
     * Add {@link Extractor}.
     * 
     * @param <V>
     * @param description The human-readable description.
     * @param type A value type.
     * @param extractor An actual value {@link Extractor}.
     */
    public <V> void addExtractor(Property<String> description, Class<V> type, Function<M, V> extractor) {
        Objects.requireNonNull(description);
        Objects.requireNonNull(type);
        Objects.requireNonNull(extractor);

        addExtractor(new Extractor<M, V>() {

            /**
             * {@inheritDoc}
             */
            @Override
            public Property<String> description() {
                return description;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public Class<V> type() {
                return type;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public V apply(M model) {
                return extractor.apply(model);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public String toString() {
                return description.getValue();
            }
        });
    }

    /**
     * Add {@link Extractor}.
     * 
     * @param <V>
     * @param extractor
     */
    public <V> void addExtractor(Extractor<M, V> extractor) {
        if (extractor != null) {
            queries.add(new Query(extractor));
        }
    }

    /**
     * Get the associated {@link CompoundQuery} with the specified {@link Node}.
     * 
     * @param <M>
     * @param node
     * @return
     */
    public static <M> CompoundQuery<M> of(Node node) {
        return (CompoundQuery<M>) node.getProperties().computeIfAbsent(CompoundQuery.class, key -> new CompoundQuery());
    }

    /**
     * 
     */
    public static interface Extractor<M, V> extends Function<M, V> {

        /**
         * Human-readable description.
         * 
         * @return
         */
        Property<String> description();

        /**
         * The acceptable value type.
         * 
         * @return
         */
        Class<V> type();

        /**
         * {@inheritDoc}
         */
        @Override
        V apply(M model);
    }

    /**
     * 
     */
    public static interface Matcher<V> extends BiPredicate<V, V> {

        /**
         * Human-readable description.
         * 
         * @return
         */
        Variable<String> description();

        /**
         * The acceptable value type.
         * 
         * @return
         */
        Class<V> type();

        /**
         * {@inheritDoc}
         */
        @Override
        boolean test(V extracted, V inputed);
    }

    /**
     * 
     */
    static class Query<M, V> implements Predicate<M> {

        /** The associated {@link Extractor}. */
        public final Extractor<M, V> extractor;

        /** The associated {@link Matcher}. */
        public final Variable<Matcher<V>> matcher = Variable.empty();

        /** The current input value. */
        public final Variable<V> input = Variable.empty();

        /**
         * @param type
         */
        private Query(Extractor<M, V> extractor) {
            this.extractor = extractor;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean test(M model) {
            if (input.v == null || matcher.v == null) {
                return true;
            } else {
                return matcher.v.test(extractor.apply(model), input.v);
            }
        }
    }
}
