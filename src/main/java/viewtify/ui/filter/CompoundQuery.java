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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import kiss.Disposable;
import kiss.Managed;
import kiss.Signal;
import kiss.Signaling;
import kiss.Variable;

public class CompoundQuery<M> implements Predicate<M>, Disposable {

    private final Signaling<CompoundQuery<M>> signaling = new Signaling();

    public final Signal<CompoundQuery<M>> updated = signaling.expose;

    /** The managed queries. */
    @Managed
    private List<Query<M, ?>> queries = new ArrayList();

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
     * List up all sub queries.
     * 
     * @return
     */
    public final List<Query<M, ?>> queries() {
        return Collections.unmodifiableList(queries);
    }

    /**
     * Add String based {@link Extractor}.
     * 
     * @param <V>
     * @param description The human-readable description.
     */
    public Query<M, String> addQuery(String description) {
        return addQuery(new SimpleStringProperty(description));
    }

    /**
     * Add String based {@link Extractor}.
     * 
     * @param <V>
     * @param description The human-readable description.
     */
    public Query<M, String> addQuery(StringProperty description) {
        return addQuery(description, String.class, String::valueOf);
    }

    /**
     * Add {@link Extractor}.
     * 
     * @param <V>
     * @param description The human-readable description.
     * @param type A value type.
     * @param extractor An actual value {@link Extractor}.
     */
    public <V> Query<M, V> addQuery(String description, Class<V> type, Function<M, V> extractor) {
        return addQuery(new SimpleStringProperty(description), type, extractor);
    }

    /**
     * Add {@link Extractor}.
     * 
     * @param <V>
     * @param description The human-readable description.
     * @param type A value type.
     * @param extractor An actual value {@link Extractor}.
     */
    public <V> Query<M, V> addQuery(StringProperty description, Class<V> type, Function<M, V> extractor) {
        Query<M, V> query = new Query(description, type, extractor);
        query.disposer = query.input.observe().as(Object.class).merge(query.matcher.observe()).to(() -> signaling.accept(this));
        queries.add(query);

        return query;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void vandalize() {
    }

    /**
     * 
     */
    public static interface Matcher<V> extends BiPredicate<V, V>, Function<V, V> {

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
    public static class Query<M, V> implements Predicate<M> {

        /** The query name. */
        public final StringProperty description;

        /** The value type. */
        public final Class<V> type;

        /** The associated {@link Extractor}. */
        public final Function<M, V> extractor;

        /** The current input value. */
        public final Variable<V> input = Variable.empty();

        /** The associated {@link Matcher}. */
        public final Variable<Matcher<V>> matcher = Variable.<Matcher<V>> empty().intercept((oldMatcher, newMatcher) -> {
            // normalize the current input
            if (input.v != null) {
                normalized = newMatcher.apply(input.v);
            }

            // normalize the input in future
            input.intercept((oldInput, newInput) -> {
                normalized = newMatcher.apply(newInput);
                return newInput;
            });

            return newMatcher;
        });

        /** The normalized input. */
        private V normalized;

        /** The deconstruction. */
        Disposable disposer;

        /**
         * @param type
         */
        Query(StringProperty description, Class<V> type, Function<M, V> extractor) {
            this.description = Objects.requireNonNull(description);
            this.type = Objects.requireNonNull(type);
            this.extractor = Objects.requireNonNull(extractor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean test(M model) {
            if (model == null || input.v == null || matcher.v == null) {
                return true;
            } else {
                return matcher.v.test(extractor.apply(model), normalized);
            }
        }
    }
}
