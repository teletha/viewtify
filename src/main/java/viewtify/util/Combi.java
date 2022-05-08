/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.util;

public class Combi<V> {

    public V previous;

    public V next;

    public Combi(V initial) {
        reset(initial);
    }

    public void set(V value) {
        this.previous = next;
        this.next = value;
    }

    public void reset(V value) {
        this.previous = value;
        this.next = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Combi [previous=" + previous + ", next=" + next + "]";
    }
}
