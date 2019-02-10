/*
 * Copyright (C) 2019 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.helper;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import kiss.I;
import kiss.Signal;

/**
 * @version 2018/01/28 18:31:40
 */
public interface SelectablePreferenceHelper<Self extends SelectablePreferenceHelper<Self, V>, V> extends PreferenceHelper<Self, V> {

    /**
     * Set values.
     * 
     * @param values
     * @return
     */
    default <E extends Enum> Self values(Class<E> enums) {
        return values((V[]) enums.getEnumConstants());
    }

    /**
     * Set values.
     * 
     * @param values
     * @return
     */
    default <E extends Enum> Self values(int initialValueIndex, Class<E> enums) {
        return values(initialValueIndex, (V[]) enums.getEnumConstants());
    }

    /**
     * Set values.
     * 
     * @param values
     * @return
     */
    default Self values(V... values) {
        return values(I.signal(values));
    }

    /**
     * Set values.
     * 
     * @param values
     * @return
     */
    default Self values(int initialValueIndex, V... values) {
        return values(initialValueIndex, I.signal(values));
    }

    /**
     * Set values.
     * 
     * @param values
     * @return
     */
    default Self values(Iterable<V> values) {
        return values(I.signal(values));
    }

    /**
     * Set values.
     * 
     * @param values
     * @return
     */
    default Self values(int initialValueIndex, Iterable<V> values) {
        return values(initialValueIndex, I.signal(values));
    }

    /**
     * Set values.
     * 
     * @param values
     * @return
     */
    default Self values(Signal<V> values) {
        return values(values.toList());
    }

    /**
     * Set values.
     * 
     * @param values
     * @return
     */
    default Self values(int initialValueIndex, Signal<V> values) {
        return values(initialValueIndex, values.toList());
    }

    /**
     * Set values.
     * 
     * @param values
     * @return
     */
    default Self values(Stream<V> values) {
        return values(values.collect(Collectors.toList()));
    }

    /**
     * Set values.
     * 
     * @param values
     * @return
     */
    default Self values(int initialValueIndex, Stream<V> values) {
        return values(initialValueIndex, values.collect(Collectors.toList()));
    }

    /**
     * Set values.
     * 
     * @param values
     * @return
     */
    Self values(List<V> values);

    /**
     * Set values.
     * 
     * @param initialValueIndex
     * @param values
     * @return
     */
    default Self values(int initialValueIndex, List<V> values) {
        return values(values).initial(values.get(initialValueIndex));
    }
}
