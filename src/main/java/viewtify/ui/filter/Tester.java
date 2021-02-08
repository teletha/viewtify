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

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.regex.Pattern;

import kiss.I;
import kiss.Variable;

class Tester<V> implements Function<V, V>, BiPredicate<V, V> {

    /** The builtin filter for {@link Comparable} value. */
    static final Tester<Comparable> Equal = new Tester<>("is equal to", Comparable.class, (value, tester) -> value.compareTo(tester) == 0);

    /** The builtin filter for {@link Comparable} value. */
    static final Tester<Comparable> NotEqual = new Tester<>("is not equal to", Comparable.class, (value, tester) -> value
            .compareTo(tester) != 0);

    /** The builtin filter for {@link Comparable} value. */
    static final Tester<Comparable> GreaterThan = new Tester<>("is greater than", Comparable.class, (value, tester) -> value
            .compareTo(tester) > 0);

    /** The builtin filter for {@link Comparable} value. */
    static final Tester<Comparable> GreaterThanOrEqual = new Tester<>("is greater than or equal to", Comparable.class, (value, tester) -> value
            .compareTo(tester) >= 0);

    /** The builtin filter for {@link Comparable} value. */
    static final Tester<Comparable> LessThan = new Tester<>("is less than", Comparable.class, (value, tester) -> value
            .compareTo(tester) < 0);

    /** The builtin filter for {@link Comparable} value. */
    static final Tester<Comparable> LessThanOrEqual = new Tester<>("is less than or equal to", Comparable.class, (value, tester) -> value
            .compareTo(tester) <= 0);

    /** The builtin filter for {@link String} value. */
    static final Tester<String> Contain = new Tester<>("contains", String.class, String.class, String::toLowerCase, (value, tester) -> value
            .toLowerCase()
            .contains(tester));

    /** The builtin filter for {@link String} value. */
    static final Tester<String> NotContain = new Tester<>("don't contain", String.class, String.class, String::toLowerCase, (value, tester) -> !value
            .toLowerCase()
            .contains(tester));

    /** The builtin filter for {@link String} value. */
    static final Tester<String> StartWith = new Tester<>("starts with", String.class, String.class, String::toLowerCase, (value, tester) -> value
            .toLowerCase()
            .startsWith(tester));

    /** The builtin filter for {@link String} value. */
    static final Tester<String> EndWith = new Tester<>("ends with", String.class, String.class, String::toLowerCase, (value, tester) -> value
            .toLowerCase()
            .endsWith(tester));

    /** The builtin filter for {@link String} value. */
    static final Tester<String> Match = new Tester<>("matches", String.class, String.class, String::toLowerCase, (value, tester) -> value
            .equalsIgnoreCase(tester));

    /** The builtin filter for {@link String} value. */
    static final Tester<String> RegEx = new Tester<>("regular expression", String.class, Pattern.class, v -> Pattern
            .compile(v, Pattern.CASE_INSENSITIVE), (value, tester) -> tester.matcher(value).find());

    /** The builtin set. */
    private final static Tester[] STRINGS = {Contain, NotContain, StartWith, EndWith, RegEx};

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
    private Tester(String description, Class<V> type, BiPredicate<V, V> condition) {
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
    private <N> Tester(String description, Class<V> type, Class<N> normalizedType, Function<V, N> normalizer, BiPredicate<V, N> condition) {
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
     * Collect type specific filters.
     * 
     * @param type
     * @return
     */
    static <T> Tester<T>[] by(Class<T> type) {
        if (type == String.class) {
            return STRINGS;
        } else if (Comparable.class.isAssignableFrom(type)) {
            return COMPARABLES;
        } else {
            return STRINGS;
        }
    }
}
