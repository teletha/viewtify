/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.helper;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import kiss.I;
import kiss.Signal;

/**
 * @version 2018/01/28 18:13:59
 */
public interface ValueSelectHelper<Self extends ValueSelectHelper, T> {

    /**
     * Set values.
     * 
     * @param values
     * @return
     */
    default <E extends Enum> Self values(Class<E> enums) {
        return values((T[]) enums.getEnumConstants());
    }

    /**
     * Set values.
     * 
     * @param values
     * @return
     */
    default Self values(T... values) {
        return values(I.signal(values));
    }

    /**
     * Set values.
     * 
     * @param values
     * @return
     */
    default Self values(Iterable<T> values) {
        return values(I.signal(values));
    }

    /**
     * Set values.
     * 
     * @param values
     * @return
     */
    default Self values(Signal<T> values) {
        return values(values.toList());
    }

    /**
     * Set values.
     * 
     * @param values
     * @return
     */
    default Self values(Stream<T> values) {
        return values(values.collect(Collectors.toList()));
    }

    /**
     * Set values.
     * 
     * @param values
     * @return
     */
    Self values(List<T> values);
}
