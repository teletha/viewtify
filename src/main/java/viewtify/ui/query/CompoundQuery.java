/*
 * Copyright (C) 2021 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import kiss.Disposable;
import kiss.I;
import kiss.Managed;
import kiss.Signal;
import kiss.Signaling;
import kiss.Variable;

public class CompoundQuery<M> implements Predicate<M>, Disposable {

    /** The internal query modification event stream. */
    private final Signaling<CompoundQuery<M>> update = new Signaling();

    /** The query modification event stream. */
    public final Signal<CompoundQuery<M>> updated = update.expose;

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
     * Add String based {@link Query}.
     * 
     * @param <V>
     * @param description The human-readable description.
     */
    public Query<M, String> addQuery(String description) {
        return addQuery(new SimpleStringProperty(description));
    }

    /**
     * Add String based {@link Query}.
     * 
     * @param <V>
     * @param description The human-readable description.
     */
    public Query<M, String> addQuery(StringProperty description) {
        return addQuery(description, String.class, String::valueOf);
    }

    /**
     * Add {@link Query}.
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
     * Add {@link Query}.
     * 
     * @param <V>
     * @param description The human-readable description.
     * @param type A value type.
     * @param extractor An actual value {@link Extractor}.
     */
    public <V> Query<M, V> addQuery(StringProperty description, Class<V> type, Function<M, V> extractor) {
        Query<M, V> query = new Query<>(description, type, extractor);
        query.disposer = query.input.observe().as(Object.class).merge(query.tester.observe()).to(() -> update.accept(this));
        queries.add(query);

        return query;
    }

    /**
     * Add {@link Query}.
     * 
     * @param <V>
     * @param description The human-readable description.
     * @param type A value type.
     * @param extractor An actual value {@link Extractor}.
     */
    public <V> Query<M, V> addObservableQuery(StringProperty description, Class<V> type, Function<M, ObservableValue<V>> extractor) {
        Query<M, V> query = new Query<>(description, type, m -> extractor.apply(m).getValue());
        query.disposer = query.input.observe().as(Object.class).merge(query.tester.observe()).to(() -> update.accept(this));
        queries.add(query);

        return query;
    }

    /**
     * Remove the specified {@link Query}.
     * 
     * @param query
     */
    public void removeQuery(Query query) {
        if (query != null && queries.remove(query)) {
            query.disposer.dispose();
            update.accept(this);
        }
    }

    /**
     * Remove all managed {@link Query}.
     */
    public void removeAllQueries() {
        for (Query<M, ?> query : queries) {
            query.disposer.dispose();
        }
        queries.clear();
        update.accept(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void vandalize() {
        removeAllQueries();
    }

    /**
     * 
     */
    public static final class Tester<V> implements Function<V, V>, BiPredicate<V, V> {

        /** The builtin filter for {@link Comparable} value. */
        public static final Tester<Comparable> Equal = new Tester<>("is equal to", Comparable.class, (value, tester) -> value
                .compareTo(tester) == 0);

        /** The builtin filter for {@link Comparable} value. */
        public static final Tester<Comparable> NotEqual = new Tester<>("is not equal to", Comparable.class, (value, tester) -> value
                .compareTo(tester) != 0);

        /** The builtin filter for {@link Comparable} value. */
        public static final Tester<Comparable> GreaterThan = new Tester<>("is greater than", Comparable.class, (value, tester) -> value
                .compareTo(tester) > 0);

        /** The builtin filter for {@link Comparable} value. */
        public static final Tester<Comparable> GreaterThanOrEqual = new Tester<>("is greater than or equal to", Comparable.class, (value, tester) -> value
                .compareTo(tester) >= 0);

        /** The builtin filter for {@link Comparable} value. */
        public static final Tester<Comparable> LessThan = new Tester<>("is less than", Comparable.class, (value, tester) -> value
                .compareTo(tester) < 0);

        /** The builtin filter for {@link Comparable} value. */
        public static final Tester<Comparable> LessThanOrEqual = new Tester<>("is less than or equal to", Comparable.class, (value, tester) -> value
                .compareTo(tester) <= 0);

        /** The builtin filter for {@link String} value. */
        public static final Tester<String> Contain = new Tester<>("contains", String.class, String.class, String::toLowerCase, (value, tester) -> value
                .toLowerCase()
                .contains(tester));

        /** The builtin filter for {@link String} value. */
        public static final Tester<String> NotContain = new Tester<>("don't contain", String.class, String.class, String::toLowerCase, (value, tester) -> !value
                .toLowerCase()
                .contains(tester));

        /** The builtin filter for {@link String} value. */
        public static final Tester<String> StartWith = new Tester<>("starts with", String.class, String.class, String::toLowerCase, (value, tester) -> value
                .toLowerCase()
                .startsWith(tester));

        /** The builtin filter for {@link String} value. */
        public static final Tester<String> EndWith = new Tester<>("ends with", String.class, String.class, String::toLowerCase, (value, tester) -> value
                .toLowerCase()
                .endsWith(tester));

        /** The builtin filter for {@link String} value. */
        public static final Tester<String> Match = new Tester<>("matches", String.class, String.class, String::toLowerCase, (value, tester) -> value
                .equalsIgnoreCase(tester));

        /** The builtin filter for {@link String} value. */
        public static final Tester<String> RegEx = new Tester<>("regular expression", String.class, Pattern.class, v -> Pattern
                .compile(v, Pattern.CASE_INSENSITIVE), (value, tester) -> tester.matcher(value).find());

        /** The builtin set. */
        private final static Tester[] STRINGS = {Contain, NotContain, StartWith, EndWith, Match, RegEx};

        /** The builtin set. */
        private final static Tester[] COMPARABLES = {Equal, NotEqual, GreaterThan, GreaterThanOrEqual, LessThan, LessThanOrEqual};

        /** The description of this filter. */
        public final Variable<String> description;

        /** The value normalizer. */
        private final Function<V, V> normalizer;

        /** The actual filter. */
        private final BiPredicate<V, V> condition;

        /**
         * Builtin filters.
         * 
         * @param <V>
         * @param description
         * @param type
         * @param condition
         */
        public Tester(String description, Class<V> type, BiPredicate<V, V> condition) {
            this(description, type, type, Function.identity(), condition);
        }

        /**
         * Builtin filters.
         * 
         * @param <V>
         * @param description
         * @param type
         * @param condition
         */
        public <N> Tester(String description, Class<V> type, Class<N> normalizedType, Function<V, N> normalizer, BiPredicate<V, N> condition) {
            this.description = I.translate(description);
            this.normalizer = (Function<V, V>) normalizer;
            this.condition = (BiPredicate<V, V>) condition;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean test(V model, V tester) {
            return condition.test(model, tester);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public V apply(V model) {
            return normalizer.apply(model);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return description.v;
        }

        /**
         * Collect type specific filters.
         * 
         * @param type
         * @return
         */
        public static <T> Tester<T>[] by(Class<T> type) {
            if (type == String.class) {
                return STRINGS;
            } else if (Comparable.class.isAssignableFrom(type)) {
                return COMPARABLES;
            } else {
                return STRINGS;
            }
        }
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
        public final Variable<Tester<V>> tester = Variable.<Tester<V>> empty().intercept((oldTester, newTester) -> {
            // normalize the current input
            if (input.v != null) {
                normalized = newTester.apply(input.v);
            }

            // normalize the input in future
            input.intercept((oldInput, newInput) -> {
                normalized = newTester.apply(newInput);
                return newInput;
            });

            return newTester;
        });

        /** The normalized input. */
        private V normalized;

        /** The deconstruction. */
        private Disposable disposer;

        /**
         * @param type
         */
        private Query(StringProperty description, Class<V> type, Function<M, V> extractor) {
            this.description = Objects.requireNonNull(description);
            this.type = Objects.requireNonNull(type);
            this.extractor = Objects.requireNonNull(extractor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean test(M model) {
            if (model == null || tester.v == null || input.v == null || (input.v instanceof String && ((String) input.v).isBlank())) {
                return true;
            } else {
                V extracted = extractor.apply(model);
                if (extracted == null) {
                    return true;
                }
                return tester.v.test(extracted, normalized);
            }
        }
    }
}
