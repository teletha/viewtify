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

import java.util.Objects;

public class Combi<V> {

    public V previous;

    public V next;

    public Combi() {
    }

    public Combi(V initial) {
        reset(initial);
    }

    public Combi<V> set(V value) {
        this.previous = next;
        this.next = value;

        return this;
    }

    public void reset(V value) {
        this.previous = value;
        this.next = value;
    }

    public boolean isDiff() {
        return !isSame();
    }

    public boolean isSame() {
        return Objects.equals(previous, next);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Combi [previous=" + previous + ", next=" + next + "]";
    }
}