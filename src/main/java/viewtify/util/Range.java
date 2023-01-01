/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;

public class Range<T extends Comparable<T>> {

    /** The start value. */
    public final T start;

    /** The end value. */
    public final T end;

    /** The next value calculator. */
    private final UnaryOperator<T> next;

    /** The precious value calculator. */
    private final UnaryOperator<T> previous;

    /**
     * @param start
     * @param end
     * @param next
     */
    public Range(T start, T end, UnaryOperator<T> next, UnaryOperator<T> previous) {
        this.start = Objects.requireNonNull(start);
        this.end = Objects.requireNonNull(end);
        this.next = Objects.requireNonNull(next);
        this.previous = Objects.requireNonNull(previous);
    }

    /**
     * Calculate all values in this {@link Range}.
     * 
     * @return
     */
    public List<T> listup() {
        List<T> list = new ArrayList();
        T now = start;
        while (now.compareTo(end) <= 0) {
            list.add(now);
            now = next.apply(now);
        }
        return list;
    }

    public Range previousStart() {
        return new Range(previous.apply(start), end, next, previous);
    }

    public Range nextStart() {
        return new Range(next.apply(start), end, next, previous);
    }

    public Range previousEnd() {
        return new Range(start, previous.apply(end), next, previous);
    }

    public Range nextEnd() {
        return new Range(start, next.apply(end), next, previous);
    }
}