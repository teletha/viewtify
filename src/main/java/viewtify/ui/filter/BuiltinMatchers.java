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

import java.util.function.BiPredicate;

import kiss.I;
import kiss.Variable;
import viewtify.ui.filter.CompoundQuery.Matcher;

enum BuiltinMatchers implements Matcher {

    /** The builtin filter for {@link Comparable} value. */
    Equal("is equal to", Comparable.class, (value, tester) -> value.compareTo(tester) == 0),

    /** The builtin filter for {@link Comparable} value. */
    NotEqual("is not equal to", Comparable.class, (value, tester) -> value.compareTo(tester) != 0),

    /** The builtin filter for {@link Comparable} value. */
    GreaterThan("is greater than", Comparable.class, (value, tester) -> value.compareTo(tester) > 0),

    /** The builtin filter for {@link Comparable} value. */
    GreaterThanOrEqual("is greater than or equal to", Comparable.class, (value, tester) -> value.compareTo(tester) >= 0),

    /** The builtin filter for {@link Comparable} value. */
    LessThan("is less than", Comparable.class, (value, tester) -> value.compareTo(tester) < 0),

    /** The builtin filter for {@link Comparable} value. */
    LessThanOrEqual("is less than or equal to", Comparable.class, (value, tester) -> value.compareTo(tester) <= 0),

    /** The builtin filter for {@link String} value. */
    Contain("contains", String.class, (value, tester) -> value.contains(tester)),

    /** The builtin filter for {@link String} value. */
    NotContain("don't contain", String.class, (value, tester) -> !value.contains(tester)),

    /** The builtin filter for {@link String} value. */
    StartWith("starts with", String.class, (value, tester) -> value.startsWith(tester)),

    /** The builtin filter for {@link String} value. */
    EndWith("ends with", String.class, (value, tester) -> value.endsWith(tester)),

    /** The builtin filter for {@link String} value. */
    RegEx("matches", String.class, (value, tester) -> value.matches(tester));

    /** The builtin set. */
    private final static BuiltinMatchers[] STRINGS = {Contain, NotContain, StartWith, EndWith, RegEx};

    /** The builtin set. */
    private final static BuiltinMatchers[] COMPARABLES = {Equal, NotEqual, GreaterThan, GreaterThanOrEqual, LessThan, LessThanOrEqual};

    /** The description of this filter. */
    private final Variable<String> description;

    /** The value type. */
    private final Class type;

    /** The actual filter. */
    private final BiPredicate condition;

    /**
     * Builtin filters.
     * 
     * @param <T>
     * @param description
     * @param type
     * @param condition
     */
    private <T> BuiltinMatchers(String description, Class<T> type, BiPredicate<T, T> condition) {
        this.description = I.translate(description);
        this.type = type;
        this.condition = condition;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Variable description() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class type() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean test(Object model, Object tester) {
        return condition.test(model, tester);
    }

    /**
     * Collect type specific filters.
     * 
     * @param type
     * @return
     */
    static <T> Matcher<T>[] by(Class<T> type) {
        if (type == String.class) {
            return STRINGS;
        } else if (Comparable.class.isAssignableFrom(type)) {
            return COMPARABLES;
        } else {
            return STRINGS;
        }
    }
}
