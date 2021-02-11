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

import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

import kiss.Disposable;
import kiss.I;
import kiss.Managed;
import kiss.Signal;
import kiss.Signaling;
import kiss.Variable;
import viewtify.Viewtify;

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
    public List<Query<M, ?>> queries() {
        return Collections.unmodifiableList(queries);
    }

    /**
     * Add String based {@link Query}.
     * 
     * @param <V>
     * @param name The human-readable description.
     */
    public Query<M, String> addQuery(String name) {
        return addQuery(new SimpleStringProperty(name));
    }

    /**
     * Add String based {@link Query}.
     * 
     * @param <V>
     * @param name The human-readable description.
     */
    public Query<M, String> addQuery(Variable<String> name) {
        return addQuery(Viewtify.property(name));
    }

    /**
     * Add String based {@link Query}.
     * 
     * @param <V>
     * @param name The human-readable description.
     */
    public Query<M, String> addQuery(Property<String> name) {
        return addQuery(name, String.class, String::valueOf);
    }

    /**
     * Add {@link Query}.
     * 
     * @param <V>
     * @param name The human-readable description.
     * @param type A value type.
     * @param extractor An actual value {@link Extractor}.
     */
    public <V> Query<M, V> addQuery(String name, Class<V> type, Function<M, V> extractor) {
        return addQuery(new SimpleStringProperty(name), type, extractor);
    }

    /**
     * Add {@link Query}.
     * 
     * @param <V>
     * @param name The human-readable description.
     * @param type A value type.
     * @param extractor An actual value {@link Extractor}.
     */
    public <V> Query<M, V> addQuery(Variable<String> name, Class<V> type, Function<M, V> extractor) {
        return addQuery(Viewtify.property(name), type, extractor);
    }

    /**
     * Add {@link Query}.
     * 
     * @param <V>
     * @param name The human-readable description.
     * @param type A value type.
     * @param extractor An actual value {@link Extractor}.
     */
    public <V> Query<M, V> addQuery(Property<String> name, Class<V> type, Function<M, V> extractor) {
        Query<M, V> query = new Query<>(name, type, extractor);
        query.disposer = query.input.observe().mapTo(null).merge(query.tester.observe()).to(() -> update.accept(this));
        queries.add(query);

        return query;
    }

    /**
     * Add {@link Query}.
     * 
     * @param <V>
     * @param name The human-readable description.
     * @param type A value type.
     * @param extractor An actual value {@link Extractor}.
     */
    public <V> Query<M, V> addObservableQuery(String name, Class<V> type, Function<M, ObservableValue<V>> extractor) {
        return addObservableQuery(new SimpleStringProperty(name), type, extractor);
    }

    /**
     * Add {@link Query}.
     * 
     * @param <V>
     * @param name The human-readable description.
     * @param type A value type.
     * @param extractor An actual value {@link Extractor}.
     */
    public <V> Query<M, V> addObservableQuery(Variable<String> name, Class<V> type, Function<M, ObservableValue<V>> extractor) {
        return addObservableQuery(Viewtify.property(name), type, extractor);
    }

    /**
     * Add {@link Query}.
     * 
     * @param <V>
     * @param name The human-readable description.
     * @param type A value type.
     * @param extractor An actual value {@link Extractor}.
     */
    public <V> Query<M, V> addObservableQuery(Property<String> name, Class<V> type, Function<M, ObservableValue<V>> extractor) {
        return addQuery(name, type, m -> extractor.apply(m).getValue());
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
        public static final Tester<Comparable> Equal = new Tester<>("is equal to", Comparable.class, (tester, value) -> value
                .compareTo(tester) == 0);

        /** The builtin filter for {@link Comparable} value. */
        public static final Tester<Comparable> NotEqual = new Tester<>("is not equal to", Comparable.class, (tester, value) -> value
                .compareTo(tester) != 0);

        /** The builtin filter for {@link Comparable} value. */
        public static final Tester<Comparable> GreaterThan = new Tester<>("is greater than", Comparable.class, (tester, value) -> value
                .compareTo(tester) > 0);

        /** The builtin filter for {@link Comparable} value. */
        public static final Tester<Comparable> GreaterThanOrEqual = new Tester<>("is greater than or equal to", Comparable.class, (tester, value) -> value
                .compareTo(tester) >= 0);

        /** The builtin filter for {@link Comparable} value. */
        public static final Tester<Comparable> LessThan = new Tester<>("is less than", Comparable.class, (tester, value) -> value
                .compareTo(tester) < 0);

        /** The builtin filter for {@link Comparable} value. */
        public static final Tester<Comparable> LessThanOrEqual = new Tester<>("is less than or equal to", Comparable.class, (tester, value) -> value
                .compareTo(tester) <= 0);

        /** The builtin filter for {@link String} value. */
        public static final Tester<String> Contain = new Tester<>("contains", String.class, Tokens.class, Tokens::new, Tokens::contains);

        /** The builtin filter for {@link String} value. */
        public static final Tester<String> NotContain = new Tester<>("don't contain", String.class, Tokens.class, Tokens::new, (tester, value) -> !tester
                .contains(value));

        /** The builtin filter for {@link String} value. */
        public static final Tester<String> StartWith = new Tester<>("starts with", String.class, Tokens.class, Tokens::new, Tokens::startsWith);

        /** The builtin filter for {@link String} value. */
        public static final Tester<String> EndWith = new Tester<>("ends with", String.class, Tokens.class, Tokens::new, Tokens::endsWith);

        /** The builtin filter for {@link String} value. */
        public static final Tester<String> Match = new Tester<>("matches", String.class, Tokens.class, Tokens::new, Tokens::match);

        /** The builtin filter for {@link String} value. */
        public static final Tester<String> RegEx = new Tester<>("regular expression", String.class, Pattern.class, v -> Pattern
                .compile(v, Pattern.CASE_INSENSITIVE), (tester, value) -> tester.matcher(value).find());

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
        public <T> Tester(String description, Class<V> type, Class<T> normalizedType, Function<V, T> normalizer, BiPredicate<T, V> condition) {
            this.description = I.translate(description);
            this.normalizer = (Function<V, V>) normalizer;
            this.condition = (BiPredicate<V, V>) condition;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean test(V tester, V value) {
            return condition.test(tester, value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public V apply(V model) {
            return model == null ? null : normalizer.apply(model);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return description.v;
        }

        /** The builtin set. */
        private final static Tester[] STRINGS = {Contain, NotContain, StartWith, EndWith, Match, RegEx};

        /** The builtin set. */
        private final static Tester[] COMPARABLES = {Equal, NotEqual, GreaterThan, GreaterThanOrEqual, LessThan, LessThanOrEqual};

        /** The builtin set. */
        private final static Tester[] ENUMS = {Equal, NotEqual};

        /**
         * Collect type specific filters.
         * 
         * @param type
         * @return
         */
        public static <T> Tester<T>[] by(Class<T> type) {
            if (type == String.class) {
                return STRINGS;
            } else if (Enum.class.isAssignableFrom(type)) {
                return ENUMS;
            } else if (Comparable.class.isAssignableFrom(type)) {
                return COMPARABLES;
            } else {
                return STRINGS;
            }
        }

        /**
         * 
         */
        private static class Tokens {

            /** The token set. */
            private final String[] tokens;

            /**
             * Tokenize the inputed text.
             * 
             * @param input
             */
            private Tokens(String input) {
                this.tokens = input.toLowerCase().split("\\s+");
            }

            /**
             * Test whether the model contains the inputed values.
             * 
             * @param value
             * @return
             */
            private boolean contains(String value) {
                String lower = value.toLowerCase();
                for (String token : tokens) {
                    if (lower.contains(token)) {
                        return true;
                    }
                }
                return false;
            }

            /**
             * Test whether the model matches the inputed values.
             * 
             * @param value
             * @return
             */
            private boolean match(String value) {
                String lower = value.toLowerCase();
                for (String token : tokens) {
                    if (lower.equals(token)) {
                        return true;
                    }
                }
                return false;
            }

            /**
             * Test whether the model starts with the inputed values.
             * 
             * @param value
             * @return
             */
            private boolean startsWith(String value) {
                String lower = value.toLowerCase();
                for (String token : tokens) {
                    if (lower.startsWith(token)) {
                        return true;
                    }
                }
                return false;
            }

            /**
             * Test whether the model starts with the inputed values.
             * 
             * @param value
             * @return
             */
            private boolean endsWith(String value) {
                String lower = value.toLowerCase();
                for (String token : tokens) {
                    if (lower.endsWith(token)) {
                        return true;
                    }
                }
                return false;
            }
        }
    }

    /**
     * 
     */
    public static class Query<M, V> implements Predicate<M> {

        /** The query name. */
        public final Property<String> name;

        /** The value type. */
        public final Class<V> type;

        /** The associated {@link Extractor}. */
        public final Function<M, V> extractor;

        /** The current input value. */
        public final Variable<V> input = Variable.empty();

        /** The associated {@link Matcher}. */
        public final Variable<Tester<V>> tester = Variable.<Tester<V>> empty().intercept((oldTester, newTester) -> {
            // normalize the current input
            normalized = newTester.apply(input.v);

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
         * Build new query.
         * 
         * @param name
         * @param type
         * @param extractor
         */
        private Query(Property<String> name, Class<V> type, Function<M, V> extractor) {
            this.name = Objects.requireNonNull(name);
            this.type = Objects.requireNonNull(type);
            this.extractor = Objects.requireNonNull(extractor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean test(M model) {
            if (model == null || tester.v == null || input.v == null) {
                return true;
            } else {
                return tester.v.test(normalized, extractor.apply(model));
            }
        }
    }
}
